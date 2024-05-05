/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.packetapi;

import java.util.HashMap;
import java.util.Map;

public class HandlerGroup {
    private final Map<Class<? extends Packet>, PacketHandler<?>> handlers;

    public HandlerGroup() {
        this.handlers = new HashMap<>();
    }

    public <T extends Packet> void registerHandler(Class<T> clazz, PacketHandler<T> handler) {
        handlers.put(clazz, handler);
    }

    public Map<Class<? extends Packet>, PacketHandler<?>> handlers() {
        return handlers;
    }
}
