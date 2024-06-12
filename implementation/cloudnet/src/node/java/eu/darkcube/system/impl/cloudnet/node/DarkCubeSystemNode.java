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
import eu.cloudnetservice.node.command.CommandProvider;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.impl.cloudnet.ModuleImplementation;
import eu.darkcube.system.impl.cloudnet.node.command.CommandDarkCubeSystem;
import eu.darkcube.system.impl.cloudnet.node.userapi.NodeUserAPI;
import eu.darkcube.system.impl.cloudnet.node.util.data.NodeCustomPersistentDataProvider;
import eu.darkcube.system.impl.cloudnet.node.util.data.SynchronizedPersistentDataStorages;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;

@Singleton
public class DarkCubeSystemNode implements ModuleImplementation {
    private final NodeListener listener;
    private final NodeUserAPI userAPI;
    private final EventManager eventManager;
    private final CommandProvider commandProvider;

    @Inject
    public DarkCubeSystemNode(CommandProvider commandProvider, NodeListener listener, NodeUserAPI userAPI, EventManager eventManager) {
        this.commandProvider = commandProvider;
        this.listener = listener;
        this.userAPI = userAPI;
        this.eventManager = eventManager;
        InternalProvider.instance().register(UserAPI.class, userAPI);
        InternalProvider.instance().register(CustomPersistentDataProvider.class, new NodeCustomPersistentDataProvider());
    }

    @Override
    public void start() {
        commandProvider.register(CommandDarkCubeSystem.class);
        PacketAPI.init();
        userAPI.init();
        eventManager.registerListener(userAPI);
        eventManager.registerListener(listener);
        SynchronizedPersistentDataStorages.init();
    }

    @Override
    public void stop() {
        commandProvider.unregister("darkcubesystem");
        eventManager.unregisterListener(userAPI);
        eventManager.unregisterListener(listener);
        userAPI.exit();
        SynchronizedPersistentDataStorages.exit();
    }
}
