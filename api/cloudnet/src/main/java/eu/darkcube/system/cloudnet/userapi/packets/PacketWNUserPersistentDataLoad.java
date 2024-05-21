/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.userapi.packets;

import java.util.UUID;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.libs.com.google.gson.JsonObject;

public class PacketWNUserPersistentDataLoad extends Packet {
    private final UUID uniqueId;
    private final JsonObject data;

    public PacketWNUserPersistentDataLoad(UUID uniqueId, JsonObject data) {
        this.uniqueId = uniqueId;
        this.data = data;
    }

    public UUID uniqueId() {
        return uniqueId;
    }

    public JsonObject data() {
        return data;
    }

    public static class Result extends Packet {
    }
}
