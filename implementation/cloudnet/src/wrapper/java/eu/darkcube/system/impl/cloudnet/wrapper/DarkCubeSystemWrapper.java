/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.impl.cloudnet.ModuleImplementation;
import eu.darkcube.system.impl.cloudnet.wrapper.userapi.WrapperUserAPI;
import eu.darkcube.system.impl.cloudnet.wrapper.util.data.WrapperPluginPersistentDataProvider;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.PluginPersistentDataProvider;

@Singleton
public class DarkCubeSystemWrapper implements ModuleImplementation {
    private final WrapperUserAPI userAPI;

    @Inject
    public DarkCubeSystemWrapper(WrapperUserAPI userAPI) {
        this.userAPI = userAPI;
        InternalProvider.instance().register(UserAPI.class, userAPI);
        InternalProvider.instance().register(PluginPersistentDataProvider.class, new WrapperPluginPersistentDataProvider());
    }

    @Override
    public void start() {
        PacketAPI.init();
    }

    @Override
    public void stop() {
        userAPI.close();
    }
}