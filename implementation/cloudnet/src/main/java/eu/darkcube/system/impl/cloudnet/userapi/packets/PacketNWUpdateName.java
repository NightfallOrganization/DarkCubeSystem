/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.userapi.packets;

import java.util.UUID;

import eu.darkcube.system.cloudnet.packetapi.Packet;

public class PacketNWUpdateName extends Packet {
    private final UUID uniqueId;
    private final String playerName;

    public PacketNWUpdateName(UUID uniqueId, String playerName) {
        this.uniqueId = uniqueId;
        this.playerName = playerName;
    }

    public UUID uniqueId() {
        return uniqueId;
    }

    public String playerName() {
        return playerName;
    }
}
