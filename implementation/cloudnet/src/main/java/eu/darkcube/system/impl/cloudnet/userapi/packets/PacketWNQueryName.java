/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.userapi.packets;

import java.util.UUID;

import eu.darkcube.system.cloudnet.packetapi.Packet;

public class PacketWNQueryName extends Packet {
    private final UUID uniqueID;

    public PacketWNQueryName(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public UUID uniqueID() {
        return uniqueID;
    }

    public static class Response extends Packet {
        private final String name;

        public Response(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }
    }
}
