/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.userapi.packets;

import java.util.UUID;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.libs.com.google.gson.JsonObject;

public class PacketWNQueryUser extends Packet {

    private final UUID uuid;

    public PacketWNQueryUser(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID uniqueId() {
        return uuid;
    }

    public static class Result extends Packet {
        private final String name;
        private final JsonObject data;

        public Result(String name, JsonObject data) {
            this.name = name;
            this.data = data;
        }

        public JsonObject getData() {
            return data;
        }

        public String getName() {
            return name;
        }
    }
}
