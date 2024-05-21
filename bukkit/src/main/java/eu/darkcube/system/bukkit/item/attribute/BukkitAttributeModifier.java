/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.item.attribute;

import eu.darkcube.system.server.item.attribute.AttributeModifier;

public interface BukkitAttributeModifier extends AttributeModifier {
    <T> T bukkitType();
}
