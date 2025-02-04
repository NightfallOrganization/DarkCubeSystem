/*
 * Copyright (c) 2024-2025. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.velocity.cloudnet;

import static eu.darkcube.system.impl.velocity.BuildConstants.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.UuidUtils;
import eu.darkcube.system.cloudnet.packets.PacketRequestProtocolVersionDeclaration;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.Cache;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.Caffeine;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.link.LinkManager;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Plugin(id = ID, name = NAME, authors = {AUTHOR_DASBABYPIXEL}, version = VERSION, dependencies = @Dependency(id = "viaversion", optional = true))
public class VelocityCloudNet {
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityCloudNet.class);
    private static final HttpClient httpClient = HttpClient.newBuilder().build();
    private static final String QUERY_UUID_BY_NAME = "https://api.minecraftservices.com/minecraft/profile/lookup/name/%s";
    private static final String QUERY_PROFILE_BY_UUID = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final Gson GSON = new GsonBuilder().create();
    private static final Cache<String, UUID> UUID_CACHE = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
    private static final Cache<UUID, JsonObject> PROFILE_CACHE = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
    private final LinkManager linkManager;
    private final ProxyServer proxy;
    private final Logger logger;
    private final EventManager eventManager;

    @Inject
    public VelocityCloudNet(ProxyServer proxy, Logger logger, EventManager eventManager) {
        this.proxy = proxy;
        this.linkManager = new LinkManager();
        this.logger = logger;
        this.eventManager = eventManager;
    }

    private static void loadUUID(String name) {
        var url = QUERY_UUID_BY_NAME.formatted(name);
        var request = HttpRequest.newBuilder().uri(URI.create(url)).GET().timeout(Duration.ofSeconds(5)).build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                var json = GSON.fromJson(response.body(), JsonObject.class);
                var idString = json.get("id").getAsString();
                var id = UuidUtils.fromUndashed(idString);
                UUID_CACHE.put(name, id);
                loadProfile(id);
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to load UUID", t);
        }
    }

    private static void loadProfile(UUID uuid) {
        var url = QUERY_PROFILE_BY_UUID.formatted(uuid.toString());
        var request = HttpRequest.newBuilder().uri(URI.create(url)).GET().timeout(Duration.ofSeconds(5)).build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                var json = GSON.fromJson(response.body(), JsonObject.class);
                PROFILE_CACHE.put(uuid, json);
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to load profile", t);
        }
    }

    @Subscribe
    public void handle(PreLoginEvent event) {
        if (proxy.getConfiguration().isOnlineMode()) return;
        loadUUID(event.getUsername());
    }

    @Subscribe
    public void handle(GameProfileRequestEvent event) {
        if (proxy.getConfiguration().isOnlineMode()) return;
        var id = UUID_CACHE.getIfPresent(event.getUsername());
        if (id == null) return;
        event.setGameProfile(event.getGameProfile().withId(id));
        var profile = PROFILE_CACHE.getIfPresent(id);
        if (profile == null) return;
        if (profile.has("properties")) {
            var properties = new ArrayList<GameProfile.Property>();
            var propertiesArray = profile.get("properties").getAsJsonArray();
            for (var propertyElement : propertiesArray) {
                var json = propertyElement.getAsJsonObject();
                var name = json.get("name").getAsString();
                var value = json.get("value").getAsString();
                var signature = json.get("signature").getAsString();
                properties.add(new GameProfile.Property(name, value, signature));
            }
            if (!properties.isEmpty()) {
                event.setGameProfile(event.getGameProfile().withProperties(properties));
            }
        }
    }

    @Subscribe
    public void handle(ProxyInitializeEvent event) {
        this.linkManager.addLink(() -> new ViaVersionLink(logger, eventManager, this));

        linkManager.enableLinks();
        new PacketRequestProtocolVersionDeclaration().sendAsync();
    }
}
