/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data.packets;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.util.data.Key;

public class PacketNodeWrapperDataRemove extends Packet {
    private final Key key;
    private final Key data;

    public PacketNodeWrapperDataRemove(Key key, Key data) {
        this.key = key;
        this.data = data;
    }

    public Key data() {
        return data;
    }

    public Key key() {
        return key;
    }
}
