/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.userapi;

import eu.darkcube.system.cloudnet.packetapi.HandlerGroup;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.impl.cloudnet.userapi.CloudNetUserAPI;
import eu.darkcube.system.impl.cloudnet.userapi.packets.PacketNWUpdateName;

public class WrapperRemoteUserPacketHandler {

    private final HandlerGroup handlerGroup = new HandlerGroup();

    public WrapperRemoteUserPacketHandler(CloudNetUserAPI api) {
        this.handlerGroup.registerHandler(PacketNWUpdateName.class, packet -> {
            var user = api.userCache().getIfPresent(packet.uniqueId());
            if (user != null) {
                user.userData().name(packet.playerName());
            }
            return null;
        });
    }

    public void registerHandlers() {
        PacketAPI.instance().registerGroup(handlerGroup);
    }

    public void unregisterHandlers() {
        PacketAPI.instance().unregisterGroup(handlerGroup);
    }
}
