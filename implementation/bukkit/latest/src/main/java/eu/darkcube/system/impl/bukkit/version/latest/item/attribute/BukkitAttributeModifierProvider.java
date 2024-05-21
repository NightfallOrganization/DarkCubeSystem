/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.attribute;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.attribute.AttributeModifier;
import eu.darkcube.system.server.item.attribute.AttributeModifierProvider;

public class BukkitAttributeModifierProvider implements AttributeModifierProvider {
    @Override public @NotNull AttributeModifier of(@NotNull Object platformAttributeModifier) {
        if (platformAttributeModifier instanceof AttributeModifier attributeModifier) return attributeModifier;
        if (platformAttributeModifier instanceof org.bukkit.attribute.AttributeModifier attributeModifier)
            return new BukkitAttributeModifierImpl(attributeModifier);
        throw new IllegalArgumentException("Invalid Attribute Modifier: " + platformAttributeModifier);
    }
}
