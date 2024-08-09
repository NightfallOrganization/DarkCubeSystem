/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.standalone;

import java.io.IOException;
import java.sql.SQLException;

import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.standalone.userapi.StandaloneUserAPI;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;

public class BukkitStandalone extends DarkCubeSystemBukkit {
    static {
        var provider = new BukkitDataProvider();
        try {
            provider.init();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        InternalProvider.instance().register(CustomPersistentDataProvider.class, provider);
        InternalProvider.instance().register(UserAPI.class, new StandaloneUserAPI());
    }
}
