/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.enchant;

import java.util.Collection;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

@ApiStatus.Internal
public interface EnchantmentProvider {
    @NotNull
    Enchantment of(@NotNull Object platformObject);

    @NotNull
    @Unmodifiable
    Collection<Enchantment> enchantments();
}
