/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record FireworkList(byte flightDuration, @NotNull List<FireworkExplosion> explosions) {
    public FireworkList {
        explosions = List.copyOf(explosions);
    }
}
