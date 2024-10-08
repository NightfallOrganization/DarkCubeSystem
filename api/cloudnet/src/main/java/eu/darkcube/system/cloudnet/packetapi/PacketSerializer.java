/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.packetapi;

import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class PacketSerializer {

    public static void serialize(@NotNull Packet packet, DataBuf.Mutable buf) {
        buf.writeString(packet.getClass().getName()).writeObject(packet);
    }

    public static Packet readPacket(DataBuf buf, ClassLoader classLoader) {
        var name = buf.readString();
        var cls = getClass(name, classLoader);
        if (cls == null) throw new NoClassDefFoundError("Missing query response class: " + name);
        return buf.readObject(cls);
    }

    public static @Nullable Class<? extends Packet> getClass(DataBuf buf, ClassLoader classLoader) {
        return getClass(buf.readString(), classLoader);
    }

    public static @Nullable Class<? extends Packet> getClass(String name, ClassLoader classLoader) {
        try {
            return (Class<? extends Packet>) Class.forName(name, true, classLoader);
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }
}
