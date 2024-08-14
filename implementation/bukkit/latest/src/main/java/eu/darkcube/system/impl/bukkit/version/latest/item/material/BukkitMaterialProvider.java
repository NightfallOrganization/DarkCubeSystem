/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.material;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.server.item.material.MaterialProvider;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;

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
            if (material.isLegacy()) continue; // We do not use legacy materials
            var m = new BukkitMaterialImpl(material);
            this.registry[i] = m;
            byKey.put(m.key().asString(), m);
        }
        this.registryByKey = Map.copyOf(byKey);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull Material of(@NotNull Object platformMaterial) {
        return switch (platformMaterial) {
            case org.bukkit.Material material when (!material.isLegacy()) -> this.registry[material.ordinal()];
            case org.bukkit.Material material -> of(Bukkit.getUnsafe().fromLegacy(material));
            case Material material -> material;
            case String string -> of(Key.key(string));
            case Key key -> Objects.requireNonNull(registryByKey.get(key.asString()));
            case net.kyori.adventure.key.Key key -> of(Key.key(key.namespace(), key.value()));
            case ResourceLocation location -> of(Key.key(location.getNamespace(), location.getPath()));
            default -> throw new IllegalArgumentException("Bad material input: " + platformMaterial);
        };
    }

    @Override
    public @NotNull Material spawner() {
        return of(org.bukkit.Material.SPAWNER);
    }

    @Override
    public @NotNull Material air() {
        return of(org.bukkit.Material.AIR);
    }
}
