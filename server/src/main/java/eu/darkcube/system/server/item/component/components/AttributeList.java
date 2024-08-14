/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.attribute.AttributeModifier;

public record AttributeList(@NotNull List<AttributeModifier> modifiers, boolean showInTooltip) {
    public static final AttributeList EMPTY = new AttributeList(List.of(), true);

    public AttributeList {
        modifiers = List.copyOf(modifiers);
    }

    public AttributeList withTooltip(boolean showInTooltip) {
        return new AttributeList(modifiers, showInTooltip);
    }
}
