/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.util.data.packets;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

public class PacketWrapperNodeDataClearSet extends Packet {
    private final String table;
    private final Key storageKey;
    private final JsonObject data;

    public PacketWrapperNodeDataClearSet(String table, Key storageKey, JsonObject data) {
        this.table = table;
        this.storageKey = storageKey;
        this.data = data;
    }

    public String table() {
        return table;
    }

    public JsonObject data() {
        return data;
    }

    public Key storageKey() {
        return storageKey;
    }

    public static class Result extends Packet {

    }
}
