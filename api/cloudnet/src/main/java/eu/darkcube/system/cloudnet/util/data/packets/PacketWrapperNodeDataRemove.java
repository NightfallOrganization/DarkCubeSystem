/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.util.data.packets;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

public class PacketWrapperNodeDataRemove extends Packet {
    private final String table;
    private final Key storageKey;
    private final Key entryKey;

    public PacketWrapperNodeDataRemove(String table, Key storageKey, Key entryKey) {
        this.table = table;
        this.storageKey = storageKey;
        this.entryKey = entryKey;
    }

    public String table() {
        return table;
    }

    public Key entryKey() {
        return entryKey;
    }

    public Key storageKey() {
        return storageKey;
    }

    public static class Result extends Packet {
        private final JsonElement removed;

        public Result(JsonElement removed) {
            this.removed = removed;
        }

        public JsonElement removed() {
            return removed;
        }
    }
}
