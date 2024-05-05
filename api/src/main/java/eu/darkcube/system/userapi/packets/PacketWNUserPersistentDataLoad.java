/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.packets;

import java.util.UUID;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.packetapi.Packet;

public class PacketWNUserPersistentDataLoad extends Packet {
    private final UUID uniqueId;
    private final Document data;

    public PacketWNUserPersistentDataLoad(UUID uniqueId, Document data) {
        this.uniqueId = uniqueId;
        this.data = data;
    }

    public UUID uniqueId() {
        return uniqueId;
    }

    public Document data() {
        return data;
    }
}
