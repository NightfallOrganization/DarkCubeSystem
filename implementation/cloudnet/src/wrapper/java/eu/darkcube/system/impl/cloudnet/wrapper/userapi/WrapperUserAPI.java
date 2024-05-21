/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.userapi;

import java.util.UUID;

import eu.darkcube.system.impl.cloudnet.userapi.CloudNetUserAPI;
import eu.darkcube.system.impl.common.userapi.CommonUser;

public class WrapperUserAPI extends CloudNetUserAPI {
    private final CommonRemoteUserPacketHandler packetHandler;

    public WrapperUserAPI() {
        this.packetHandler = new CommonRemoteUserPacketHandler(this);
        this.packetHandler.registerHandlers();
    }

    @Override
    public void close() {
        this.packetHandler.unregisterHandlers();
        super.close();
    }

    @Override
    protected CommonUser loadUser(UUID uniqueId) {
        return new WrapperUser(uniqueId);
    }
}
