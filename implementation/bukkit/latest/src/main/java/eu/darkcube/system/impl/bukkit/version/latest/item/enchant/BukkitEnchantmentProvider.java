/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.enchant;

import static eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport.adventureSupport;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.item.enchant.Enchantment;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Registry;

public class BukkitEnchantmentProvider implements EnchantmentProvider {
    private Registry<org.bukkit.enchantments.Enchantment> registry;
    private Map<org.bukkit.enchantments.Enchantment, Enchantment> enchantments;
    private List<Enchantment> enchantmentList;

    private void tryLoad() {
        if (enchantments != null) return;
        synchronized (this) {
            if (enchantments != null) return;
            enchantments = new HashMap<>();
            registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            for (var enchantment : registry) {
                var e = new BukkitEnchantmentImpl(enchantment, adventureSupport().convert(enchantment.key()));
                enchantments.put(enchantment, e);
            }
            enchantmentList = List.copyOf(enchantments.values());
        }
    }

    @NotNull
    @Override
    public Enchantment of(@NotNull Object platformObject) {
        tryLoad();
        return switch (platformObject) {
            case Enchantment enchantment -> enchantment;
            case org.bukkit.enchantments.Enchantment enchantment -> enchantments.get(enchantment);
            case Key key -> of(net.kyori.adventure.key.Key.key(key.namespace(), key.value()));
            case net.kyori.adventure.key.Key key -> of(registry.getOrThrow(key));
            default -> throw new IllegalArgumentException("Not a valid enchantment: " + platformObject);
        };
    }

    @Override
    public @NotNull @Unmodifiable Collection<Enchantment> enchantments() {
        tryLoad();
        return enchantmentList;
    }
}
