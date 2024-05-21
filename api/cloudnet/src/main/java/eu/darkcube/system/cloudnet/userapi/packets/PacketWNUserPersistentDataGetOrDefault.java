/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.userapi.packets;

import java.util.UUID;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

public class PacketWNUserPersistentDataGetOrDefault extends Packet {
    private final UUID uniqueId;
    private final Key entryKey;
    private final JsonElement data;

    public PacketWNUserPersistentDataGetOrDefault(UUID uniqueId, Key entryKey, JsonElement data) {
        this.uniqueId = uniqueId;
        this.entryKey = entryKey;
        this.data = data;
    }

    public UUID uniqueId() {
        return uniqueId;
    }

    public Key entryKey() {
        return entryKey;
    }

    public JsonElement data() {
        return data;
    }

    public static class Result extends Packet {
        private final JsonElement data;

        public Result(JsonElement data) {
            this.data = data;
        }

        public JsonElement data() {
            return data;
        }
    }
}
