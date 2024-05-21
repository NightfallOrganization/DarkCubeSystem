/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.item;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.item.ItemTemplate;

public class ItemTemplateImpl implements ItemTemplate {
    private final Map<Integer, ItemReferenceImpl> contents = new HashMap<>();

    @Override
    public @NotNull ItemReference setItem(@NotNull Object item, int... slots) {
        var reference = new ItemReferenceImpl(item);
        for (var slot : slots) {
            contents.put(slot, reference);
        }
        return reference;
    }

    @Override
    public @NotNull @Unmodifiable Map<Integer, ItemReferenceImpl> contents() {
        return Map.copyOf(contents);
    }
}
