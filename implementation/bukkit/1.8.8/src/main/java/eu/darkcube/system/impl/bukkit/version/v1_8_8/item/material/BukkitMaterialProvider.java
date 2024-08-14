/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.material;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.server.item.material.MaterialProvider;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;

public class BukkitMaterialProvider implements MaterialProvider {
    // use an array instead of Map or something else for optimal performance
    private final Material[] registry;
    // we have to use a map here
    private final Map<String, Material> registryByKey;

    public BukkitMaterialProvider() {
        var materials = org.bukkit.Material.values();
        this.registry = new Material[materials.length];
        var byKey = new HashMap<String, Material>();
        for (var i = 0; i < materials.length; i++) {
            var material = materials[i];
            var item = CraftMagicNumbers.getItem(material);
            var mcKey = Item.REGISTRY.c(item);
            var key = Key.key(mcKey.toString());
            var m = new BukkitMaterialImpl(material, key);
            this.registry[i] = m;
            byKey.put(key.asString(), m);
        }
        this.registryByKey = Map.copyOf(byKey);
    }

    @Override
    public @NotNull Material of(@NotNull Object platformMaterial) {
        return switch (platformMaterial) {
            case org.bukkit.Material material -> this.registry[material.ordinal()];
            case Material material -> material;
            case String string -> of(Key.key(string));
            case Key key -> registryByKey.get(key.asString());
            case MinecraftKey key -> of(key.toString());
            default -> throw new IllegalArgumentException("Bad material input: " + platformMaterial);
        };
    }

    @Override
    public @NotNull Material spawner() {
        return of(org.bukkit.Material.MOB_SPAWNER);
    }

    @Override
    public @NotNull Material air() {
        return of(org.bukkit.Material.AIR);
    }
}
