/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.userapi;

import java.util.UUID;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.impl.cloudnet.node.userapi.handler.HandlerPlayerLogin;
import eu.darkcube.system.impl.cloudnet.userapi.CloudNetUserAPI;
import eu.darkcube.system.impl.cloudnet.userapi.packets.PacketNWUpdateName;
import eu.darkcube.system.impl.cloudnet.userapi.packets.PacketWNPlayerLogin;
import eu.darkcube.system.impl.common.userapi.CommonUser;
import eu.darkcube.system.impl.common.userapi.CommonUserData;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonObject;

@Singleton
public class NodeUserAPI extends CloudNetUserAPI {
    private final Gson gson = new Gson();
    private final Database database;
    private final UserLocalPacketHandlers packetHandlers = new UserLocalPacketHandlers(this);
    private final NodeDataSaver dataSaver;

    @Inject
    public NodeUserAPI(DatabaseProvider databaseProvider) {
        this.database = databaseProvider.database("userapi_users");
        this.dataSaver = new NodeDataSaver(this.database);
        this.packetHandlers.handlerGroup().registerHandler(PacketWNPlayerLogin.class, new HandlerPlayerLogin(this));
    }

    public void updateName(UUID uniqueId, String playerName) {
        var user = user(uniqueId);
        user.userData().name(playerName);
        ((UserLocalPersistentDataStorage) user.persistentData()).name(playerName);
        new PacketNWUpdateName(uniqueId, playerName).sendSync();
    }

    @Override
    protected CommonUser loadUser(UUID uniqueId) {
        var data = this.database.get(uniqueId.toString());
        if (data == null) data = Document.newJsonDocument();
        var name = data.getString("name");
        if (name == null) {
            name = uniqueId.toString().substring(0, 16);
        }
        var persistentData = new UserLocalPersistentDataStorage(uniqueId, name, gson.fromJson(data.readDocument("persistentData").serializeToString(), JsonObject.class));
        persistentData.addUpdateNotifier(dataSaver.saveNotifier());
        var userData = new CommonUserData(uniqueId, name, persistentData);
        return new NodeUser(userData);
    }

    public void exit() {
        packetHandlers.unregisterHandlers();
        dataSaver.exit();
    }

    public void init() {
        dataSaver.init();
        packetHandlers.registerHandlers();
    }
}
