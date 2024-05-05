/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data.packets;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.packetapi.Packet;

public class PacketData extends Packet {
    private final Document data;

    public PacketData(Document data) {
        this.data = data;
    }

    public Document data() {
        return data;
    }
}
