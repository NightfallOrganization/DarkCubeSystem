/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.internal;

import eu.darkcube.system.packetapi.Packet;

public class PacketDeclareProtocolVersion extends Packet {
    private final String serverName;
    private final int[] protocolVersions;

    public PacketDeclareProtocolVersion(String serverName, int[] protocolVersions) {
        this.serverName = serverName;
        this.protocolVersions = protocolVersions;
    }

    public String serverName() {
        return serverName;
    }

    public int[] protocolVersions() {
        return protocolVersions;
    }
}
