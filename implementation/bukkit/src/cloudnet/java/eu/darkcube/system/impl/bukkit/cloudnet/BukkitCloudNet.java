/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.cloudnet;

import java.util.Arrays;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.bukkit.provider.via.ViaSupport;
import eu.darkcube.system.bukkit.version.BukkitVersion;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.cloudnet.packets.PacketDeclareProtocolVersion;
import eu.darkcube.system.cloudnet.packets.PacketRequestProtocolVersionDeclaration;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.bukkit.cloudnet.link.luckperms.LuckPermsLink;
import eu.darkcube.system.server.cloudnet.DarkCubeServerCloudNet;
import eu.darkcube.system.server.version.ServerVersion;
import eu.darkcube.system.util.GameState;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class BukkitCloudNet extends DarkCubeSystemBukkit {
    private final Listener listener = new Listener();

    public BukkitCloudNet() {
        PacketAPI.instance().classLoader(getClassLoader());
    }

    @Override
    public void onLoad() {
        linkManager.addLink(() -> new LuckPermsLink(this));
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Runnable run = () -> {
            PacketAPI.instance().registerHandler(PacketRequestProtocolVersionDeclaration.class, packet -> {
                declareVersion();
                return null;
            });
            declareVersion();
        };
        if (BukkitVersion.version().provider().service(ViaSupport.class).supported()) {
            Bukkit.getScheduler().runTaskLater(this, run, 5);
        } else {
            run.run();
        }
        var eventManager = InjectionLayer.ext().instance(EventManager.class);
        eventManager.registerListener(listener);
        InjectionLayer.ext().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();

        Bukkit.getPluginManager().registerEvents(listener, this);
        Bukkit.getScheduler().runTask(this, () -> {
            if (DarkCubeServerCloudNet.autoConfigure()) {
                DarkCubeServerCloudNet.gameState(GameState.INGAME);
                InjectionLayer.ext().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
            }
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        InjectionLayer.ext().instance(EventManager.class).unregisterListener(listener);
        HandlerList.unregisterAll(listener);
    }

    public void declareVersion() {
        var via = BukkitVersion.version().provider().service(ViaSupport.class);
        var supportedVersions = new int[0];
        if (via.supported()) {
            var supported = via.supported() ? via.supportedVersions() : new ProtocolVersion[0];
            supportedVersions = Arrays.stream(supported).mapToInt(ProtocolVersion::getVersion).toArray();
        }
        if (supportedVersions.length == 0) {
            supportedVersions = new int[]{ServerVersion.version().protocolVersion()};
        }

        new PacketDeclareProtocolVersion(InjectionLayer.boot().instance(ComponentInfo.class).componentName(), supportedVersions).sendAsync();
    }
}
