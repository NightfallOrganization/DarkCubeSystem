/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record ArmorTrim(@NotNull Key material, @NotNull Key pattern, boolean showInTooltip) {
    public @NotNull ArmorTrim withTooltip(boolean showInTooltip) {
        return new ArmorTrim(material, pattern, showInTooltip);
    }
}
