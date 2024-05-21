/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.userapi.packets;

import java.util.UUID;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

public class PacketWNUserPersistentDataSet extends Packet {
    private final UUID uuid;
    private final Key key;
    private final JsonElement data;

    public PacketWNUserPersistentDataSet(UUID uuid, Key key, JsonElement data) {
        this.uuid = uuid;
        this.key = key;
        this.data = data;
    }

    public Key key() {
        return key;
    }

    public JsonElement data() {
        return this.data;
    }

    public UUID uniqueId() {
        return this.uuid;
    }

    public static class Result extends Packet {
    }
}
