/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.util.data;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.cloudnet.packetapi.PacketHandler;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeGetOrDefault;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class HandlerGetOrDefault implements PacketHandler<PacketWrapperNodeGetOrDefault> {
    @Override
    public @NotNull Packet handle(@NotNull PacketWrapperNodeGetOrDefault packet) throws Throwable {
        var data = SynchronizedPersistentDataStorages.storage(packet.table(), packet.storageKey()).get(packet.entryKey(), packet::defaultValue);
        return new PacketWrapperNodeGetOrDefault.Result(data);
    }
}
