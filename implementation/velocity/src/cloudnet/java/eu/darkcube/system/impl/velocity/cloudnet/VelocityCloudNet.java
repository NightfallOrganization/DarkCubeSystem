/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.velocity.cloudnet;

import static eu.darkcube.system.impl.velocity.BuildConstants.*;

import javax.inject.Inject;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import eu.darkcube.system.cloudnet.packets.PacketRequestProtocolVersionDeclaration;
import eu.darkcube.system.link.LinkManager;
import org.slf4j.Logger;

@Plugin(id = ID, name = NAME, authors = {AUTHOR_DASBABYPIXEL}, version = VERSION, dependencies = @Dependency(id = "viaversion", optional = true))
public class VelocityCloudNet {
    private final LinkManager linkManager;
    private final Logger logger;
    private final EventManager eventManager;

    @Inject
    public VelocityCloudNet(Logger logger, EventManager eventManager) {
        this.linkManager = new LinkManager();
        this.logger = logger;
        this.eventManager = eventManager;
    }

    @Subscribe
    public void handle(ProxyInitializeEvent event) {
        this.linkManager.addLink(() -> new ViaVersionLink(logger, eventManager, this));

        linkManager.enableLinks();
        new PacketRequestProtocolVersionDeclaration().sendAsync();
    }
}
