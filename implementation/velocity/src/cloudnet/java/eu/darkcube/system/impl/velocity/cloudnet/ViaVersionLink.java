/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.velocity.cloudnet;

import java.util.Arrays;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.viaversion.viaversion.api.Via;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.cloudnet.packets.PacketDeclareProtocolVersion;
import eu.darkcube.system.impl.cloudnet.userapi.packets.PacketWNPlayerLogin;
import eu.darkcube.system.link.Link;
import org.slf4j.Logger;

public class ViaVersionLink extends Link {
    private final Logger logger;
    private final EventManager eventManager;
    private final VelocityCloudNet plugin;

    public ViaVersionLink(Logger logger, EventManager eventManager, VelocityCloudNet plugin) throws Throwable {
        this.logger = logger;
        this.eventManager = eventManager;
        this.plugin = plugin;
    }

    @Override
    protected void link() {
        logger.info("Enabled viaversion link");
        Via.proxyPlatform().protocolDetectorService();
        eventManager.register(plugin, this);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        PacketAPI.instance().registerHandler(PacketDeclareProtocolVersion.class, packet -> {
            logger.info("Server {} has protocol versions {}", packet.serverName(), Arrays.toString(packet.protocolVersions()));
            Via.proxyPlatform().protocolDetectorService().setProtocolVersions(packet.serverName(), packet.protocolVersions());
            return null;
        });
    }

    @Subscribe
    public void handle(LoginEvent event) {
        new PacketWNPlayerLogin(event.getPlayer().getUniqueId(), event.getPlayer().getUsername()).sendQuery(PacketWNPlayerLogin.Response.class);
    }

    @Override
    protected void unlink() {
        eventManager.unregisterListener(plugin, this);
    }
}
