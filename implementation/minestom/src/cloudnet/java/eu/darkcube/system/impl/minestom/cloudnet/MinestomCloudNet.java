/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.cloudnet;

import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.cloudnet.packetapi.PacketHandler;
import eu.darkcube.system.cloudnet.packets.PacketDeclareProtocolVersion;
import eu.darkcube.system.cloudnet.packets.PacketRequestProtocolVersionDeclaration;
import eu.darkcube.system.impl.minestom.DarkCubeSystemMinestomExtension;
import eu.darkcube.system.server.cloudnet.DarkCubeServerCloudNet;
import eu.darkcube.system.server.version.ServerVersion;
import eu.darkcube.system.util.GameState;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;

public class MinestomCloudNet extends DarkCubeSystemMinestomExtension {
    private final PacketHandler<PacketRequestProtocolVersionDeclaration> versionDeclarationHandler = packet -> declareVersion();
    private final ServiceListener listener = new ServiceListener();

    @Override
    public void preInitialize() {
        super.preInitialize();
        InjectionLayer.boot().instance(EventManager.class).registerListener(listener);
    }

    @Override
    public void initialize() {
        super.initialize();

        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, event -> {
            if (!DarkCubeServerCloudNet.autoConfigure()) return;
            DarkCubeServerCloudNet.playingPlayers().decrementAndGet();
            InjectionLayer.boot().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        });
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            if (!DarkCubeServerCloudNet.autoConfigure()) return;
            DarkCubeServerCloudNet.playingPlayers().incrementAndGet();
            InjectionLayer.boot().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        });
    }

    @Override
    public void postInitialize() {
        super.postInitialize();
        PacketAPI.instance().registerHandler(PacketRequestProtocolVersionDeclaration.class, versionDeclarationHandler);
        declareVersion();
        if (DarkCubeServerCloudNet.autoConfigure()) {
            DarkCubeServerCloudNet.gameState(GameState.INGAME);
            InjectionLayer.ext().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        }
    }

    @Override
    public void terminate() {
        super.terminate();
        InjectionLayer.boot().instance(EventManager.class).unregisterListener(listener);
        PacketAPI.instance().unregisterHandler(versionDeclarationHandler);
    }

    private Packet declareVersion() {
        var componentInfo = InjectionLayer.boot().instance(ComponentInfo.class);
        var version = ServerVersion.version().protocolVersion();
        new PacketDeclareProtocolVersion(componentInfo.componentName(), new int[]{version}).sendAsync();
        return null;
    }
}
