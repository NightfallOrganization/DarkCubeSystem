/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.packetapi;

public class PacketException extends RuntimeException {

    private static final long serialVersionUID = -8183059478661095719L;

    private Packet packet;

    public PacketException(Packet packet) {
        this.packet = packet;
    }

    @Override
    public String getMessage() {
        return packet.getClass().getName();
    }

    public Packet getPacket() {
        return packet;
    }
}
