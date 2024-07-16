/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.packetapi;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.cloudnetservice.driver.network.rpc.defaults.object.DefaultObjectMapper;
import eu.cloudnetservice.driver.network.rpc.object.ObjectMapper;
import eu.cloudnetservice.driver.network.rpc.object.ObjectSerializer;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.Cache;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.Caffeine;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.RemovalCause;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.Scheduler;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger("PacketAPI");
    private static final Gson GSON = new Gson();
    private static final String CHANNEL = "darkcube:packetapi";
    private static final String MESSAGE_PACKET = "packet";
    private static final byte TYPE_NO_RESPONSE = 0;
    private static final byte TYPE_QUERY = 1;
    private static final byte TYPE_QUERY_RESPONSE = 2;
    private static PacketAPI instance;

    static {
        instance = new PacketAPI();
    }

    private final Map<Class<? extends Packet>, PacketHandler<?>> handlers = new HashMap<>();
    private volatile ClassLoader classLoader = getClass().getClassLoader();
    private Listener listener;
    private EventManager eventManager = InjectionLayer.boot().instance(EventManager.class);
    private Cache<UUID, QueryEntry<? extends Packet>> queries = Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(10)).scheduler(Scheduler.systemScheduler()).removalListener((UUID unused, QueryEntry<? extends Packet> value, RemovalCause cause) -> {
        if (cause.wasEvicted() && value != null) {
            value.task().completeExceptionally(new TimeoutException());
        }
    }).build();

    private PacketAPI() {
        this.listener = new Listener();
        load();
    }

    /**
     * Old api for acquiring the {@link PacketAPI}.
     *
     * @deprecated {@link #instance()}
     */
    @Deprecated(forRemoval = true)
    public static PacketAPI getInstance() {
        return instance;
    }

    public static PacketAPI instance() {
        return instance;
    }

    @ApiStatus.Internal
    public static void init() {
        DefaultObjectMapper.DEFAULT_MAPPER.registerBinding(Key.class, new ObjectSerializer<Key>() {
            @Override
            public @NotNull Key read(@NotNull DataBuf source, @NotNull Type type, @NotNull ObjectMapper caller) {
                return Key.key(source.readString(), source.readString());
            }

            @Override
            public void write(@NotNull DataBuf.Mutable dataBuf, @NotNull Key object, @NotNull Type type, @NotNull ObjectMapper caller) {
                dataBuf.writeString(object.namespace()).writeString(object.value());
            }
        }, false);
        DefaultObjectMapper.DEFAULT_MAPPER.registerBinding(JsonElement.class, new ObjectSerializer<JsonElement>() {
            @Override
            public @Nullable JsonElement read(@NotNull DataBuf source, @NotNull Type type, @NotNull ObjectMapper caller) {
                return GSON.fromJson(source.readString(), JsonElement.class);
            }

            @Override
            public void write(@NotNull DataBuf.Mutable dataBuf, @NotNull JsonElement object, @NotNull Type type, @NotNull ObjectMapper caller) {
                dataBuf.writeString(GSON.toJson(object));
            }
        }, false);
        DefaultObjectMapper.DEFAULT_MAPPER.registerBinding(JsonObject.class, new ObjectSerializer<JsonObject>() {
            @Override
            public @Nullable JsonObject read(@NotNull DataBuf source, @NotNull Type type, @NotNull ObjectMapper caller) {
                return GSON.fromJson(source.readString(), JsonObject.class);
            }

            @Override
            public void write(@NotNull DataBuf.Mutable dataBuf, @NotNull JsonObject object, @NotNull Type type, @NotNull ObjectMapper caller) {
                dataBuf.writeString(GSON.toJson(object));
            }
        }, false);
    }

    public void classLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    void load() {
        eventManager.registerListener(listener);
    }

    void unload() {
        eventManager.unregisterListener(listener);
    }

    public void sendPacket(Packet packet) {
        preparePacket(packet, null, TYPE_NO_RESPONSE).targetAll().build().send();
    }

    public void sendPacket(Packet packet, ServiceInfoSnapshot snapshot) {
        preparePacket(packet, null, TYPE_NO_RESPONSE).targetService(snapshot.name()).build().send();
    }

    public void sendPacketAsync(Packet packet) {
        preparePacket(packet, null, TYPE_NO_RESPONSE).targetAll().build().send();
    }

    public void sendPacketAsync(Packet packet, ServiceInfoSnapshot snapshot) {
        preparePacket(packet, null, TYPE_NO_RESPONSE).targetService(snapshot.name()).build().send();
    }

    public void sendPacketSync(Packet packet) {
        preparePacket(packet, null, TYPE_NO_RESPONSE).targetAll().build().sendQuery();
    }

    public <T extends Packet> T sendPacketQuery(Packet packet, Class<T> responsePacketType) {
        return sendPacketQueryAsync(packet, responsePacketType).join();
    }

    public <T extends Packet> CompletableFuture<T> sendPacketQueryAsync(Packet packet, Class<T> responsePacketType) {
        var uuid = UUID.randomUUID();
        var task = new CompletableFuture<T>();
        var entry = new QueryEntry<T>(task, responsePacketType);
        queries.put(uuid, entry);
        preparePacket(packet, uuid, TYPE_QUERY).targetAll().build().send();
        return task;
    }

    private ChannelMessage.Builder preparePacket(Packet packet, UUID uuid, byte type) {
        var buf = DataBuf.empty();
        buf.writeByte(type);
        if (type == TYPE_QUERY || type == TYPE_QUERY_RESPONSE) {
            buf.writeUniqueId(uuid);
        }
        PacketSerializer.serialize(packet, buf);
        return prepareMessage(buf);
    }

    private ChannelMessage.Builder prepareMessage(DataBuf buffer) {
        return ChannelMessage.builder().channel(CHANNEL).message(MESSAGE_PACKET).buffer(buffer);
    }

    public void registerGroup(HandlerGroup group) {
        handlers.putAll(group.handlers());
    }

    public void unregisterGroup(HandlerGroup group) {
        handlers.keySet().removeAll(group.handlers().keySet());
    }

    public <T extends Packet> void registerHandler(Class<T> clazz, PacketHandler<T> handler) {
        handlers.put(clazz, handler);
    }

    public void unregisterHandler(PacketHandler<?> handler) {
        handlers.values().remove(handler);
    }

    private record QueryEntry<T>(CompletableFuture<T> task, Class<T> resultType) {
        void complete(Packet packet) {
            task.complete(resultType.cast(packet));
        }
    }

    public class Listener {

        @EventListener
        public void handle(ChannelMessageReceiveEvent e) {
            if (!e.channel().equals(CHANNEL)) return;
            if (!e.message().equals(MESSAGE_PACKET)) return;

            var content = e.content();
            try {
                content.startTransaction();
                var messageType = content.readByte();

                var query = messageType == TYPE_QUERY;
                var queryResponse = messageType == TYPE_QUERY_RESPONSE;
                var queryId = query || queryResponse ? content.readUniqueId() : null;

                if (queryResponse) {
                    var entry = queries.getIfPresent(queryId);
                    if (entry == null) return;
                    try {
                        var packet = PacketSerializer.readPacket(content, classLoader);
                        entry.complete(packet);
                    } catch (NoClassDefFoundError error) {
                        LOGGER.error("Unknown Packet response. Expected: " + entry.resultType.getName(), error);
                        entry.task.completeExceptionally(error);
                    }
                    return;
                }

                var packetClass = PacketSerializer.getClass(content, classLoader);
                if (handlers.containsKey(packetClass)) {
                    try {
                        var received = content.readObject(packetClass);
                        var handler = (PacketHandler<Packet>) handlers.get(packetClass);
                        var response = handler.handle(received);

                        if (query) {
                            preparePacket(response, queryId, TYPE_QUERY_RESPONSE).target(e.sender().toTarget()).build().send();
                        } else if (response != null) {
                            LOGGER.warn("Gave a response packet to a Packet that isn't a query packet! Handler: {}", handler.getClass().getName());
                        }
                    } catch (Throwable ex) {
                        LOGGER.error("Packet Handling Failed", ex);
                    }
                }
            } finally {
                if (content.accessible()) content.redoTransaction();
            }
        }
    }
}
