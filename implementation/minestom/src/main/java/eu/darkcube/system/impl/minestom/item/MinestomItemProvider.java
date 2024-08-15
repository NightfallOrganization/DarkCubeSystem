/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item;

import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.item.material.MinestomMaterial;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.ItemProvider;
import eu.darkcube.system.server.item.material.Material;
import net.minestom.server.item.ItemStack;

public class MinestomItemProvider implements ItemProvider {
    @Override
    public @NotNull ItemBuilder item(@Nullable Material material) {
        return new MinestomItemBuilderImpl(ItemStack.of(((MinestomMaterial) (material == null ? Material.air() : material)).minestomType()));
    }

    @Override
    public @NotNull ItemBuilder item(@NotNull JsonElement json) {
        return MinestomItemBuilderImpl.deserialize(json);
    }

    @Override
    public @NotNull ItemBuilder item(@NotNull Object object) {
        return switch (object) {
            case Material material -> item(material);
            case net.minestom.server.item.Material material -> item(Material.of(material));
            case ItemStack item -> item(item);
            case ItemBuilder builder -> builder.clone();
            default -> throw new IllegalArgumentException("Invalid item input: " + object);
        };
    }

    public @NotNull ItemBuilder item(@NotNull ItemStack item) {
        return new MinestomItemBuilderImpl(item);
    }
}
