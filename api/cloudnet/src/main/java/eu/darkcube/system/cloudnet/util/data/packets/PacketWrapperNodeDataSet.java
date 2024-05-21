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

public class PacketWrapperNodeDataSet extends Packet {
    private final Key storageKey;
    private final Key entryKey;
    private final JsonElement data;

    public PacketWrapperNodeDataSet(Key storageKey, Key entryKey, JsonElement data) {
        this.storageKey = storageKey;
        this.entryKey = entryKey;
        this.data = data;
    }

    public JsonElement data() {
        return data;
    }

    public Key entryKey() {
        return entryKey;
    }

    public Key storageKey() {
        return storageKey;
    }

    public static class Result extends Packet {
        private final boolean confirmed;

        public Result(boolean confirmed) {
            this.confirmed = confirmed;
        }

        public boolean confirmed() {
            return confirmed;
        }
    }
}
