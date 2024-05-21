/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import eu.darkcube.system.provider.Provider;
import eu.darkcube.system.server.version.ServerVersion;
import net.minestom.server.MinecraftServer;

public class MinestomVersion implements ServerVersion {
    private static final VarHandle PROTOCOL_VERSION;

    static {
        try {
            PROTOCOL_VERSION = MethodHandles.publicLookup().findStaticVarHandle(MinecraftServer.class, "PROTOCOL_VERSION", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final Provider provider;

    public MinestomVersion() {
        this.provider = Provider.newProvider();
    }

    @Override public int protocolVersion() {
        return (int) PROTOCOL_VERSION.get();
    }

    @Override public Provider provider() {
        return provider;
    }
}
