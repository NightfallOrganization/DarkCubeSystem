/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.enchant;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
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
    private static final VarHandle NMS_NAME;
    private final Map<Integer, Enchantment> enchantments;
    private final Map<String, Enchantment> byKey;

    public BukkitEnchantmentProvider() {
        this.enchantments = new HashMap<>();
        this.byKey = new HashMap<>();
        for (var bukkitEnchantment : org.bukkit.enchantments.Enchantment.values()) {
            var nms = CraftEnchantment.getRaw(bukkitEnchantment);
            var id = bukkitEnchantment.hashCode();
            var name = (String) NMS_NAME.get(nms);
            var key = Key.key(name);

            var enchantment = new BukkitEnchantmentImpl(bukkitEnchantment, key);
            byKey.put(key.asString(), enchantment);
            enchantments.put(id, enchantment);
        }
    }

    @NotNull
    @Override
    public Enchantment of(@NotNull Object platformObject) {
        return switch (platformObject) {
            case Enchantment enchantment -> enchantment;
            case org.bukkit.enchantments.Enchantment enchantment -> Objects.requireNonNull(enchantments.get(enchantment.hashCode()));
            case net.minecraft.server.v1_8_R3.Enchantment enchantment -> of(Key.key((String) NMS_NAME.get(enchantment)));
            case Key key -> Objects.requireNonNull(byKey.get(key.asString()));
            case MinecraftKey key -> of(Key.key(key.toString()));
            default -> throw new IllegalArgumentException("Not a valid enchantment: " + platformObject);
        };
    }

    static {
        try {
            var lookup = MethodHandles.lookup();
            NMS_NAME = lookup.findVarHandle(net.minecraft.server.v1_8_R3.Enchantment.class, "name", String.class);
        } catch (Throwable t) {
            throw new Error(t);
        }
    }
}
