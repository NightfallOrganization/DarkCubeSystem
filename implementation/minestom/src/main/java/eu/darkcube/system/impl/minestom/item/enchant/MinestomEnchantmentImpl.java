/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.enchant;

import java.util.Objects;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.enchant.MinestomEnchantment;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.enchant.Enchantment;

public record MinestomEnchantmentImpl(@NotNull Enchantment minestomType, @NotNull Key key) implements MinestomEnchantment {
    public MinestomEnchantmentImpl(@NotNull Enchantment minestomType) {
        this(minestomType, Key.key(Objects.requireNonNull(MinecraftServer.getEnchantmentRegistry().getKey(minestomType)).key().asString()));
    }

    @Override
    public int maxLevel() {
        return minestomType.maxLevel();
    }
}
