/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.HashMap;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.component.components.EnchantmentList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record EnchantmentsMapper() implements Mapper<EnchantmentList, ItemEnchantments> {
    private static final RegistryOps<Tag> OPS = RegistryOps.create(NbtOps.INSTANCE, MinecraftServer.getServer().registryAccess());
    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantmentsMapper.class);

    @Override
    public ItemEnchantments apply(EnchantmentList mapping) {
        var mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        var getter = OPS.getter(Registries.ENCHANTMENT).orElseThrow();
        mutable.showInTooltip = mapping.showInTooltip();
        for (var entry : mapping.getEnchantments().entrySet()) {
            var key = entry.getKey().key();
            var id = ResourceLocation.parse(key.asString());
            var level = entry.getValue();
            var enchantment = getter.get(ResourceKey.create(Registries.ENCHANTMENT, id)).orElse(null);
            if (enchantment == null) {
                LOGGER.error("Failed to look up enchantment {}", id);
                continue;
            }
            mutable.set(enchantment, level);
        }
        return mutable.toImmutable();
    }

    @Override
    public EnchantmentList load(ItemEnchantments mapping) {
        var enchantments = new HashMap<Key, Integer>();
        for (var entry : mapping.entrySet()) {
            var holder = entry.getKey();
            var level = entry.getIntValue();
            var key = Key.key(holder.unwrapKey().orElseThrow().location().toString());
            enchantments.put(key, level);
        }
        return new EnchantmentList(enchantments, mapping.showInTooltip);
    }
}
