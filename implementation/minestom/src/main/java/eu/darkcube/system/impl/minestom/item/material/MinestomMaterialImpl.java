/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.material;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.material.MinestomMaterial;
import net.minestom.server.item.Material;

public record MinestomMaterialImpl(@NotNull net.minestom.server.item.Material minestomType, @NotNull Key key) implements MinestomMaterial {
    public MinestomMaterialImpl(Material minestomType) {
        this(minestomType, Key.key(minestomType.key().asString()));
    }

    @Override
    public boolean isBlock() {
        return minestomType.isBlock();
    }
}
