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

public class PacketNWUserPersistentDataMerge extends Packet {
    private final UUID uuid;
    private final JsonObject data;

    public PacketNWUserPersistentDataMerge(UUID uuid, JsonObject data) {
        this.uuid = uuid;
        this.data = data;
    }

    public UUID uniqueId() {
        return uuid;
    }

    public JsonObject data() {
        return data;
    }
}
