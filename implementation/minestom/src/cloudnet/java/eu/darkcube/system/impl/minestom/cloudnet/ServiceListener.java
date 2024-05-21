/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.cloudnet;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.wrapper.event.ServiceInfoPropertiesConfigureEvent;
import eu.darkcube.system.cloudnet.DarkCubeServiceProperty;
import eu.darkcube.system.server.cloudnet.DarkCubeServerCloudNet;

public class ServiceListener {
    @EventListener
    public void handle(ServiceInfoPropertiesConfigureEvent event) {
        event.propertyHolder().writeProperty(DarkCubeServiceProperty.GAME_STATE, DarkCubeServerCloudNet.gameState()).writeProperty(DarkCubeServiceProperty.PLAYING_PLAYERS, DarkCubeServerCloudNet.playingPlayers().get()).writeProperty(DarkCubeServiceProperty.SPECTATING_PLAYERS, DarkCubeServerCloudNet.spectatingPlayers().get()).writeProperty(DarkCubeServiceProperty.MAX_PLAYING_PLAYERS, DarkCubeServerCloudNet.maxPlayingPlayers().get()).writeProperty(DarkCubeServiceProperty.DISPLAY_NAME, DarkCubeServerCloudNet.displayName()).writeProperty(DarkCubeServiceProperty.AUTOCONFIGURED, DarkCubeServerCloudNet.autoConfigure()).writeProperty(DarkCubeServiceProperty.EXTRA, DarkCubeServerCloudNet.extra());
    }
}
