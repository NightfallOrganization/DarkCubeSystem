/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.userapi;

import java.util.UUID;

import eu.darkcube.system.cloudnet.userapi.packets.PacketWNQueryUser;
import eu.darkcube.system.impl.cloudnet.userapi.CloudNetUser;
import eu.darkcube.system.impl.common.userapi.CommonUserData;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.util.AdventureSupport;

public class WrapperUser extends CloudNetUser {
    public WrapperUser(UUID uniqueId) {
        super(query(uniqueId));
    }

    @Override
    public Audience audience() {
        return AdventureSupport.adventureSupport().audienceProvider().player(uniqueId());
    }

    private static CommonUserData query(UUID uniqueId) {
        var result = new PacketWNQueryUser(uniqueId).sendQuery(PacketWNQueryUser.Result.class);
        var persistentData = new CommonUserRemotePersistentDataStorage(uniqueId, result.getData());
        return new CommonUserData(uniqueId, result.getName(), persistentData);
    }
}
