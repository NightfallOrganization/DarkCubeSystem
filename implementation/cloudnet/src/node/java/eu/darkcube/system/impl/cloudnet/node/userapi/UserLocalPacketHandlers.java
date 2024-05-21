/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.userapi;

import eu.darkcube.system.cloudnet.packetapi.HandlerGroup;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.cloudnet.userapi.packets.PacketWNQueryUser;
import eu.darkcube.system.cloudnet.userapi.packets.PacketWNUserPersistentDataGetOrDefault;
import eu.darkcube.system.cloudnet.userapi.packets.PacketWNUserPersistentDataLoad;
import eu.darkcube.system.cloudnet.userapi.packets.PacketWNUserPersistentDataRemove;
import eu.darkcube.system.cloudnet.userapi.packets.PacketWNUserPersistentDataSet;
import eu.darkcube.system.impl.cloudnet.userapi.CloudNetUserAPI;
import eu.darkcube.system.impl.cloudnet.userapi.CommonPersistentDataStorage;

public class UserLocalPacketHandlers {
    private final HandlerGroup handlerGroup = new HandlerGroup();

    public UserLocalPacketHandlers(CloudNetUserAPI api) {
        this.handlerGroup.registerHandler(PacketWNQueryUser.class, packet -> {
            var user = api.userCache().get(packet.uniqueId());
            return new PacketWNQueryUser.Result(user.name(), user.persistentData().storeToJsonObject());
        });
        this.handlerGroup.registerHandler(PacketWNUserPersistentDataSet.class, packet -> {
            var user = api.userCache().get(packet.uniqueId());
            ((CommonPersistentDataStorage) user.userData().persistentData()).set(packet.key(), packet.data());
            return null;
        });
        this.handlerGroup.registerHandler(PacketWNUserPersistentDataRemove.class, packet -> {
            var user = api.userCache().get(packet.uniqueId());
            ((CommonPersistentDataStorage) user.userData().persistentData()).remove(packet.key());
            return null;
        });
        this.handlerGroup.registerHandler(PacketWNUserPersistentDataLoad.class, packet -> {
            var user = api.userCache().get(packet.uniqueId());
            user.persistentData().loadFromJsonObject(packet.data());
            return null;
        });
        this.handlerGroup.registerHandler(PacketWNUserPersistentDataGetOrDefault.class, packet -> {
            var user = api.userCache().get(packet.uniqueId());
            var data = ((CommonPersistentDataStorage) user.persistentData()).getOrDefault(packet.entryKey(), packet.data());
            return new PacketWNUserPersistentDataGetOrDefault.Result(data);
        });
    }

    public HandlerGroup handlerGroup() {
        return handlerGroup;
    }

    public void registerHandlers() {
        PacketAPI.instance().registerGroup(handlerGroup);
    }

    public void unregisterHandlers() {
        PacketAPI.instance().unregisterGroup(handlerGroup);
    }
}
