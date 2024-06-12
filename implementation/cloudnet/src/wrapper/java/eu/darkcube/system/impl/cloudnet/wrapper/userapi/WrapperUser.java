/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.userapi;

import java.util.UUID;

import eu.darkcube.system.impl.cloudnet.userapi.CloudNetUser;
import eu.darkcube.system.impl.cloudnet.userapi.packets.PacketWNQueryName;
import eu.darkcube.system.impl.common.userapi.CommonUserData;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;

public class WrapperUser extends CloudNetUser {
    public WrapperUser(UUID uniqueId) {
        super(query(uniqueId));
    }

    @Override
    public @NotNull Audience audience() {
        return AdventureSupport.adventureSupport().audienceProvider().player(uniqueId());
    }

    private static CommonUserData query(UUID uniqueId) {
        var persistentData = CustomPersistentDataProvider.dataProvider().persistentData("userapi_users", Key.key("", uniqueId.toString()));
        var response = new PacketWNQueryName(uniqueId).sendQuery(PacketWNQueryName.Response.class);
        // var result = new PacketWNQueryUser(uniqueId).sendQuery(PacketWNQueryUser.Result.class);
        // var persistentData = new CommonUserRemotePersistentDataStorage(uniqueId, result.getData());
        return new CommonUserData(uniqueId, response.name(), persistentData);
    }
}
