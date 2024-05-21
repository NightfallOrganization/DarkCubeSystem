/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.util.data;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.cloudnet.packetapi.PacketHandler;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataSet;

class HandlerSet implements PacketHandler<PacketWrapperNodeDataSet> {
    @Override
    public Packet handle(PacketWrapperNodeDataSet packet) {
        SynchronizedPersistentDataStorages.storage(packet.storageKey()).set(packet.entryKey(), packet.data());
        return null;
    }
}
