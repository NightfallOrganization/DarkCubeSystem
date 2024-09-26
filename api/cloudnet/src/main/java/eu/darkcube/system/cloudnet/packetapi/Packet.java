/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.packetapi;

import java.util.concurrent.CompletableFuture;

import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public abstract class Packet {

    public final void send() {
        PacketAPI.instance().sendPacket(this);
    }

    public final void sendSync() {
        PacketAPI.instance().sendPacketSync(this);
    }

    public void sendEmptyQuery() {
        PacketAPI.instance().sendPacketEmptyQuery(this);
    }

    public final void sendAsync() {
        PacketAPI.instance().sendPacketAsync(this);
    }

    public final void send(@NotNull ServiceInfoSnapshot snapshot) {
        PacketAPI.instance().sendPacket(this, snapshot);
    }

    public final void sendAsync(@NotNull ServiceInfoSnapshot snapshot) {
        PacketAPI.instance().sendPacketAsync(this, snapshot);
    }

    public final <T extends Packet> T sendQuery(@NotNull Class<T> responseClass) {
        return PacketAPI.instance().sendPacketQuery(this, responseClass);
    }

    public final <T extends Packet> CompletableFuture<T> sendQueryAsync(@NotNull Class<T> responseClass) {
        return PacketAPI.instance().sendPacketQueryAsync(this, responseClass);
    }

    public final <T extends Packet> T cast(@NotNull Class<T> clazz) {
        return clazz.cast(this);
    }

    public final <T extends Packet> boolean instanceOf(@NotNull Class<T> clazz) {
        return clazz.isInstance(this);
    }

}
