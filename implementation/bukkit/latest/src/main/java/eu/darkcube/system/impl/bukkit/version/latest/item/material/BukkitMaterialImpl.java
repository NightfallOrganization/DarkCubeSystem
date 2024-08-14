/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.material;

import eu.darkcube.system.bukkit.item.material.BukkitMaterial;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import org.bukkit.Material;

public record BukkitMaterialImpl(Material bukkitType, Key key) implements BukkitMaterial {
    public BukkitMaterialImpl(Material bukkitType) {
        this(bukkitType, Key.key(bukkitType.key().asString()));
    }

    @Override
    public boolean isBlock() {
        return bukkitType.isBlock();
    }
}
