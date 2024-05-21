/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.userapi.packets;

import java.util.UUID;

import eu.darkcube.system.cloudnet.packetapi.Packet;

public class PacketWNPlayerLogin extends Packet {
    private final UUID playerId;
    private final String playerName;

    public PacketWNPlayerLogin(UUID playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public UUID playerId() {
        return playerId;
    }

    public String playerName() {
        return playerName;
    }

    public static class Response extends Packet {
    }
}
