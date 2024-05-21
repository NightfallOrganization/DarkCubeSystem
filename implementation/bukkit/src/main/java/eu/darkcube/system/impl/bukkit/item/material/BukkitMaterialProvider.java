/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.item.material;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.server.item.material.MaterialProvider;

public class BukkitMaterialProvider implements MaterialProvider {
    // use an array instead of Map or something else for optimal performance
    private final Material[] registry;
    private final Material spawner;

    public BukkitMaterialProvider() {
        var materials = org.bukkit.Material.values();
        Material spawner = null;
        this.registry = new Material[materials.length];
        for (var i = 0; i < materials.length; i++) {
            var material = materials[i];
            this.registry[i] = new BukkitMaterialImpl(material);
            if (material.name().equals("SPAWNER") || material.name().equals("MOB_SPAWNER")) {
                spawner = this.registry[i];
            }
        }
        this.spawner = spawner;
    }

    @Override public @NotNull Material of(@NotNull Object platformMaterial) {
        if (platformMaterial instanceof org.bukkit.Material bukkitMaterial) {
            return this.registry[bukkitMaterial.ordinal()];
        } else if (platformMaterial instanceof Material material) { // no conversion required
            return material;
        }
        throw new IllegalArgumentException("Bad material input: " + platformMaterial);
    }

    @Override public @NotNull Material spawner() throws UnsupportedOperationException {
        if (spawner == null) throw new UnsupportedOperationException();
        return spawner;
    }

    @Override public @NotNull Material air() {
        return of(org.bukkit.Material.AIR);
    }
}
