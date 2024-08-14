/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.enchant;

import static eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport.adventureSupport;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.enchant.Enchantment;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Registry;

public class BukkitEnchantmentProvider implements EnchantmentProvider {
    private static final Registry<org.bukkit.enchantments.Enchantment> REGISTRY = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Map<org.bukkit.enchantments.Enchantment, Enchantment> enchantments = new HashMap<>();

    public BukkitEnchantmentProvider() {
        for (var enchantment : REGISTRY) {
            enchantments.put(enchantment, new BukkitEnchantmentImpl(enchantment, adventureSupport().convert(enchantment.key())));
        }
    }

    @NotNull
    @Override
    public Enchantment of(@NotNull Object platformObject) {
        return switch (platformObject) {
            case Enchantment enchantment -> enchantment;
            case org.bukkit.enchantments.Enchantment enchantment -> enchantments.get(enchantment);
            case Key key -> of(net.kyori.adventure.key.Key.key(key.namespace(), key.value()));
            case net.kyori.adventure.key.Key key -> of(REGISTRY.getOrThrow(key));
            default -> throw new IllegalArgumentException("Not a valid enchantment: " + platformObject);
        };
    }
}
