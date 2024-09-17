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
import eu.darkcube.system.impl.cloudnet.userapi.packets.PacketWNQueryName;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class HandlerQueryName implements PacketHandler<PacketWNQueryName> {
    private final NodeUserAPI userAPI;

    public HandlerQueryName(NodeUserAPI userAPI) {
        this.userAPI = userAPI;
    }

    @Override
    public @NotNull Packet handle(@NotNull PacketWNQueryName packet) {
        var name = userAPI.user(packet.uniqueID()).name();
        return new PacketWNQueryName.Response(name);
    }
}
