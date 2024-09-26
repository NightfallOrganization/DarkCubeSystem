/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.packetapi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval = true)
public class PacketAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger("PacketAPI");
    private static final Gson GSON = new Gson();
    private static final String CHANNEL = "darkcube:packetapi";
    private static final String MESSAGE_PACKET = "packet";
    // private static final byte TYPE_NO_RESPONSE = 0;
    // private static final byte TYPE_QUERY = 1;
    // private static final byte TYPE_QUERY_RESPONSE = 2;
    private static PacketAPI instance;

    static {
        instance = new PacketAPI();
    }

    private final Map<Class<? extends Packet>, PacketHandler<?>> handlers = new HashMap<>();
    private volatile ClassLoader classLoader = getClass().getClassLoader();
    private Listener listener;
    private EventManager eventManager = InjectionLayer.boot().instance(EventManager.class);
    // private Cache<UUID, QueryEntry<? extends Packet>> queries = Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(10)).scheduler(Scheduler.systemScheduler()).removalListener((UUID unused, QueryEntry<? extends Packet> value, RemovalCause cause) -> {
    //     if (cause.wasEvicted() && value != null) {
    //         value.task().completeExceptionally(new TimeoutException());
    //     }
    // }).build();

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

    public void sendPacket(@NotNull Packet packet) {
        preparePacket(packet).targetAll().build().send();
    }

    public void sendPacket(@NotNull Packet packet, @NotNull ServiceInfoSnapshot snapshot) {
        preparePacket(packet).targetService(snapshot.name()).build().send();
    }

    public void sendPacketAsync(@NotNull Packet packet) {
        preparePacket(packet).targetAll().build().send();
    }

    public void sendPacketAsync(@NotNull Packet packet, @NotNull ServiceInfoSnapshot snapshot) {
        preparePacket(packet).targetService(snapshot.name()).build().send();
    }

    public void sendPacketSync(@NotNull Packet packet) {
        preparePacket(packet).targetAll().sendSync(true).build().send();
    }

    public void sendPacketEmptyQuery(@NotNull Packet packet) {
        preparePacket(packet).targetAll().build().sendQuery();
    }

    public <T extends Packet> T sendPacketQuery(@NotNull Packet packet, @NotNull Class<T> responsePacketType) {
        return sendPacketQueryAsync(packet, responsePacketType).join();
    }

    public <T extends Packet> CompletableFuture<T> sendPacketQueryAsync(@NotNull Packet packet, @NotNull Class<T> responsePacketType) {
        return preparePacket(packet).targetAll().build().sendSingleQueryAsync().handle((message, throwable) -> {
            if (throwable != null) throw PacketAPI.propagate(throwable);
            try {
                var content = message.content();
                return responsePacketType.cast(PacketSerializer.readPacket(content, classLoader));
            } catch (NoClassDefFoundError error) {
                LOGGER.error("Unknown packet response. Expected {}", responsePacketType.getName(), error);
                throw error;
            } catch (ClassCastException cast) {
                LOGGER.error("Wrong packet response.", cast);
                throw cast;
            }
        });
    }

    private static <T extends Throwable> void throwException(Throwable throwable) throws T {
        throw (T) throwable;
    }

    private static RuntimeException propagate(Throwable throwable) {
        PacketAPI.throwException(throwable);
        return new RuntimeException();
    }

    private ChannelMessage.Builder preparePacket(@NotNull Packet packet) {
        var buf = DataBuf.empty();
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

    public class Listener {

        @EventListener
        public void handle(ChannelMessageReceiveEvent e) {
            if (!e.channel().equals(CHANNEL)) return;
            if (!e.message().equals(MESSAGE_PACKET)) return;

            var content = e.content();
            try {
                content.startTransaction();

                var query = e.query();
                var className = content.readString();
                var packetClass = PacketSerializer.getClass(className, classLoader);
                if (packetClass == null) {
                    LOGGER.debug("Unknown packet: {}", className);
                    return;
                }
                if (handlers.containsKey(packetClass)) {
                    try {
                        var received = content.readObject(packetClass);
                        var handler = (PacketHandler<Packet>) handlers.get(packetClass);
                        var response = handler.handle(received);

                        if (query) {
                            if (response != null) {
                                var buf = DataBuf.empty();
                                PacketSerializer.serialize(response, buf);
                                e.binaryResponse(buf);
                            } else {
                                LOGGER.warn("Gave a response packet to a Packet that isn't a query packet! Handler: {}", handler.getClass().getName());
                            }
                        }
                    } catch (Throwable ex) {
                        LOGGER.error("Packet Handling Failed", ex);
                    }
                } else {
                    LOGGER.debug("No handler for {}", packetClass.getName());
                }
            } finally {
                if (content.accessible()) content.redoTransaction();
            }
        }
    }
}
