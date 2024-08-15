/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.enchant;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.enchant.Enchantment;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.DynamicRegistry;

public class MinestomEnchantmentProvider implements EnchantmentProvider {
    private final Map<net.minestom.server.item.enchant.Enchantment, Enchantment> enchantments = new ConcurrentHashMap<>();

    @Override
    public @NotNull Enchantment of(@NotNull Object platformObject) {
        return switch (platformObject) {
            case Enchantment enchantment -> enchantment;
            case net.minestom.server.item.enchant.Enchantment enchantment -> enchantments.computeIfAbsent(enchantment, MinestomEnchantmentImpl::new);
            case Key key -> of(DynamicRegistry.Key.of(key.asString()));
            case net.kyori.adventure.key.Key key -> of(Key.key(key.namespace(), key.value()));
            case DynamicRegistry.Key<?> unsafeKey -> {
                var key = (DynamicRegistry.Key<net.minestom.server.item.enchant.Enchantment>) unsafeKey;
                var enchantment = MinecraftServer.getEnchantmentRegistry().get(key);
                yield of(Objects.requireNonNull(enchantment));
            }
            default -> throw new IllegalArgumentException("Invalid Enchantment: " + platformObject);
        };
    }
}
