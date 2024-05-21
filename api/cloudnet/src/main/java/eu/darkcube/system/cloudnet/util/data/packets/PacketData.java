/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.util.data.packets;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.libs.com.google.gson.JsonObject;

public class PacketData extends Packet {
    private final JsonObject data;

    public PacketData(JsonObject data) {
        this.data = data;
    }

    public JsonObject data() {
        return data;
    }
}
