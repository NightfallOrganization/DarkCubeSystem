/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.userapi;

import static eu.darkcube.system.util.data.CustomPersistentDataProvider.dataProvider;

import java.util.UUID;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.darkcube.system.cloudnet.packetapi.HandlerGroup;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.impl.cloudnet.node.userapi.handler.HandlerPlayerLogin;
import eu.darkcube.system.impl.cloudnet.node.userapi.handler.HandlerQueryName;
import eu.darkcube.system.impl.cloudnet.userapi.CloudNetUserAPI;
import eu.darkcube.system.impl.cloudnet.userapi.packets.PacketNWUpdateName;
import eu.darkcube.system.impl.cloudnet.userapi.packets.PacketWNPlayerLogin;
import eu.darkcube.system.impl.cloudnet.userapi.packets.PacketWNQueryName;
import eu.darkcube.system.impl.common.userapi.CommonUser;
import eu.darkcube.system.impl.common.userapi.CommonUserData;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

@Singleton
public class NodeUserAPI extends CloudNetUserAPI {

    private final HandlerGroup handlers = new HandlerGroup();

    @Inject
    public NodeUserAPI() {
        handlers.registerHandler(PacketWNPlayerLogin.class, new HandlerPlayerLogin(this));
        handlers.registerHandler(PacketWNQueryName.class, new HandlerQueryName(this));
    }

    public void updateName(UUID uniqueId, String playerName) {
        var user = user(uniqueId);
        user.userData().name(playerName);
        ((UserNodePersistentDataStorage) user.persistentData()).name(playerName);
        new PacketNWUpdateName(uniqueId, playerName).sendSync();
    }

    @Override
    protected CommonUser loadUser(UUID uniqueId) {
        var persistentData = (UserNodePersistentDataStorage) dataProvider().persistentData("userapi_users", Key.key("", uniqueId.toString()));
        var userData = new CommonUserData(uniqueId, persistentData.name(), persistentData);
        return new NodeUser(userData);
    }

    public void exit() {
        PacketAPI.instance().unregisterGroup(handlers);
    }

    public void init() {
        PacketAPI.instance().registerGroup(handlers);
    }
}
