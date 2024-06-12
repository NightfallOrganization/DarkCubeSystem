/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.util.data.packets;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

public class PacketWrapperNodeGetOrDefault extends Packet {
    private final String table;
    private final Key storageKey;
    private final Key entryKey;
    private final JsonElement defaultValue;

    public PacketWrapperNodeGetOrDefault(String table, Key storageKey, Key entryKey, JsonElement defaultValue) {
        this.table = table;
        this.storageKey = storageKey;
        this.entryKey = entryKey;
        this.defaultValue = defaultValue;
    }

    public String table() {
        return table;
    }

    public Key storageKey() {
        return storageKey;
    }

    public Key entryKey() {
        return entryKey;
    }

    public JsonElement defaultValue() {
        return defaultValue;
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
