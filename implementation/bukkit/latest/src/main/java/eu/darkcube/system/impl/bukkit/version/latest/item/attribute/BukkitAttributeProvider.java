/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.attribute;

import eu.darkcube.system.impl.common.EnumConverter;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeProvider;

public class BukkitAttributeProvider implements AttributeProvider {
    private final Attribute[] attributes = EnumConverter.convert(org.bukkit.attribute.Attribute.class, Attribute.class, BukkitAttribute::new);

    @Override public @NotNull Attribute of(@NotNull Object platformAttribute) {
        if (platformAttribute instanceof Attribute attribute) return attribute;
        if (platformAttribute instanceof org.bukkit.attribute.Attribute attribute) return this.attributes[attribute.ordinal()];
        throw new IllegalArgumentException("Invalid attribute: " + platformAttribute);
    }
}
