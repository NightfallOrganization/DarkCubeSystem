/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.userapi;

import eu.darkcube.system.impl.cloudnet.userapi.CloudNetUser;
import eu.darkcube.system.impl.common.userapi.CommonUserData;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class NodeUser extends CloudNetUser {
    public NodeUser(CommonUserData userData) {
        super(userData);
    }

    @Override
    public @NotNull Audience audience() {
        return Audience.empty(); // TODO implement an audience for sending data from the node
    }
}
