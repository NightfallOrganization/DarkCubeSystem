/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record SuspiciousStewEffects(@NotNull List<Effect> effects) {
    public SuspiciousStewEffects {
        effects = List.copyOf(effects);
    }

    public record Effect(@NotNull Key id, int durationTicks) {
    }
}
