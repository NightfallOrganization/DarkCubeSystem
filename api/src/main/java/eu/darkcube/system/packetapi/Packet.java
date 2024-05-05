/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.packetapi;

import eu.cloudnetservice.common.concurrent.Task;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;

public abstract class Packet {

    public final void send() {
        PacketAPI.instance().sendPacket(this);
    }

    public final void sendAsync() {
        PacketAPI.instance().sendPacketAsync(this);
    }

    public final void send(ServiceInfoSnapshot snapshot) {
        PacketAPI.instance().sendPacket(this, snapshot);
    }

    public final void sendAsync(ServiceInfoSnapshot snapshot) {
        PacketAPI.instance().sendPacketAsync(this, snapshot);
    }

    public final <T extends Packet> T sendQuery(Class<T> responseClass) {
        return PacketAPI.instance().sendPacketQuery(this, responseClass);
    }

    public final <T extends Packet> Task<T> sendQueryAsync(Class<T> responseClass) {
        return PacketAPI.instance().sendPacketQueryAsync(this, responseClass);
    }

    public final <T extends Packet> T cast(Class<T> clazz) {
        return clazz.cast(this);
    }

    public final <T extends Packet> boolean instanceOf(Class<T> clazz) {
        return clazz.isInstance(this);
    }
}
