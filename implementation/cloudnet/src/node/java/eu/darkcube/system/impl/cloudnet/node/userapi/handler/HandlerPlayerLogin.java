/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.userapi.handler;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.cloudnet.packetapi.PacketHandler;
import eu.darkcube.system.impl.cloudnet.node.userapi.NodeUserAPI;
import eu.darkcube.system.impl.cloudnet.userapi.packets.PacketWNPlayerLogin;

public class HandlerPlayerLogin implements PacketHandler<PacketWNPlayerLogin> {
    private final NodeUserAPI userAPI;

    public HandlerPlayerLogin(NodeUserAPI userAPI) {
        this.userAPI = userAPI;
    }

    @Override
    public Packet handle(PacketWNPlayerLogin packet) throws Throwable {
        userAPI.updateName(packet.playerId(), packet.playerName());
        return new PacketWNPlayerLogin.Response();
    }
}
