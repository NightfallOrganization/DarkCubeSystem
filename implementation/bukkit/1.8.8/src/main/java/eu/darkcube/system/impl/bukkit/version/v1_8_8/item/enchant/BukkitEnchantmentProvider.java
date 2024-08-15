/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.enchant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.enchant.Enchantment;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import org.bukkit.craftbukkit.v1_8_R3.enchantments.CraftEnchantment;

public class BukkitEnchantmentProvider implements EnchantmentProvider {
    private Map<net.minecraft.server.v1_8_R3.Enchantment, MinecraftKey> inverseMap;
    private Map<Integer, Enchantment> enchantments;
    private Map<String, Enchantment> byKey;

    private void tryLoad() {
        if (enchantments != null) return;
        synchronized (this) {
            if (enchantments != null) return;
            this.enchantments = new HashMap<>();
            this.byKey = new HashMap<>();
            this.inverseMap = new HashMap<>();
            for (var key : net.minecraft.server.v1_8_R3.Enchantment.getEffects()) {
                inverseMap.put(net.minecraft.server.v1_8_R3.Enchantment.getByName(key.toString()), key);
            }
            for (var bukkitEnchantment : org.bukkit.enchantments.Enchantment.values()) {
                var nms = CraftEnchantment.getRaw(bukkitEnchantment);
                var id = bukkitEnchantment.hashCode();
                var name = inverseMap.get(nms).toString();
                var key = Key.key(name);

                var enchantment = new BukkitEnchantmentImpl(bukkitEnchantment, key);
                byKey.put(key.asString(), enchantment);
                enchantments.put(id, enchantment);
            }
        }
    }

    @NotNull
    @Override
    public Enchantment of(@NotNull Object platformObject) {
        tryLoad();
        if (platformObject == null) {
            throw new NullPointerException("Enchantment input was null");
        }
        return switch (platformObject) {
            case Enchantment enchantment -> enchantment;
            case org.bukkit.enchantments.Enchantment enchantment -> Objects.requireNonNull(enchantments.get(enchantment.hashCode()));
            case net.minecraft.server.v1_8_R3.Enchantment enchantment -> of(inverseMap.get(enchantment));
            case Key key -> Objects.requireNonNull(byKey.get(key.asString()), key.asString());
            case MinecraftKey key -> of(Key.key(key.toString()));
            default -> throw new IllegalArgumentException("Not a valid enchantment: " + platformObject);
        };
    }
}
