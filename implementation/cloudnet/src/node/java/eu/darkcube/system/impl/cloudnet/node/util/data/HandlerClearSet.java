/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.util.data;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.cloudnet.packetapi.PacketHandler;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataClearSet;

class HandlerClearSet implements PacketHandler<PacketWrapperNodeDataClearSet> {
    @Override
    public Packet handle(PacketWrapperNodeDataClearSet packet) {
        SynchronizedPersistentDataStorages.storage(packet.table(), packet.storageKey()).loadFromJsonObject(packet.data());
        return new PacketWrapperNodeDataClearSet.Result();
    }
}
