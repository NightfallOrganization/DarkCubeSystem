/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data.packets;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.util.data.Key;

public class PacketWrapperNodeQuery extends Packet {

    private final Key key;

    public PacketWrapperNodeQuery(Key key) {
        this.key = key;
    }

    public Key key() {
        return key;
    }
}
