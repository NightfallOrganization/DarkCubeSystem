/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.driver.event.EventManager;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.impl.cloudnet.ModuleImplementation;
import eu.darkcube.system.impl.cloudnet.node.userapi.NodeUserAPI;
import eu.darkcube.system.impl.cloudnet.node.util.data.NodePluginPersistentDataProvider;
import eu.darkcube.system.impl.cloudnet.node.util.data.SynchronizedPersistentDataStorages;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.PluginPersistentDataProvider;

@Singleton
public class DarkCubeSystemNode implements ModuleImplementation {
    private final NodeListener listener;
    private final NodeUserAPI userAPI;
    private final EventManager eventManager;

    @Inject
    public DarkCubeSystemNode(NodeListener listener, NodeUserAPI userAPI, EventManager eventManager) {
        this.listener = listener;
        this.userAPI = userAPI;
        this.eventManager = eventManager;
        InternalProvider.instance().register(UserAPI.class, userAPI);
        InternalProvider.instance().register(PluginPersistentDataProvider.class, new NodePluginPersistentDataProvider());
    }

    @Override
    public void start() {
        PacketAPI.init();
        userAPI.init();
        eventManager.registerListener(userAPI);
        eventManager.registerListener(listener);
        SynchronizedPersistentDataStorages.init();
    }

    @Override
    public void stop() {
        eventManager.unregisterListener(userAPI);
        eventManager.unregisterListener(listener);
        userAPI.exit();
        SynchronizedPersistentDataStorages.exit();
    }
}