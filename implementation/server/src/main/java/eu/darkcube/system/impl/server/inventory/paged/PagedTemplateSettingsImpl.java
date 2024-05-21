/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.paged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.darkcube.system.impl.server.inventory.InventoryTemplateImpl;
import eu.darkcube.system.impl.server.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.paged.PageSlotSorter;
import eu.darkcube.system.server.inventory.paged.PagedTemplateSettings;

public class PagedTemplateSettingsImpl implements PagedTemplateSettings {
    private final InventoryTemplateImpl<?> template;
    private final List<ItemReferenceImpl> items;
    private final Map<Integer, int[]> specialPageSlots;
    private final PageButtonImpl previousButton = new PageButtonImpl();
    private final PageButtonImpl nextButton = new PageButtonImpl();
    private int[] pageSlots = new int[0];
    private @NotNull PageSlotSorter sorter;

    public PagedTemplateSettingsImpl(InventoryTemplateImpl<?> template) {
        this.template = template;
        this.specialPageSlots = new HashMap<>();
        this.items = new ArrayList<>();
        this.sorter = PageSlotSorter.Sorters.DEFAULT;
    }

    @Override
    public @NotNull ItemReference addItem(@NotNull Object item) {
        var reference = new ItemReferenceImpl(item);
        items.add(reference);
        return reference;
    }

    @Override
    public int @NotNull [] pageSlots() {
        return pageSlots.clone();
    }

    @Override
    public void pageSlots(int @NotNull [] pageSlots) {
        this.pageSlots = pageSlots.clone();
    }

    @Override
    public @NotNull PageSlotSorter sorter() {
        return sorter;
    }

    @Override
    public void sorter(@NotNull PageSlotSorter sorter) {
        this.sorter = sorter;
    }

    @Override
    public void specialPageSlots(int @NotNull [] pageSlots) {
        this.specialPageSlots.put(pageSlots.length, pageSlots.clone());
    }

    @Override
    public boolean removeSpecialPageSlots(int size) {
        return this.specialPageSlots.remove(size) != null;
    }

    @Override
    public @NotNull @Unmodifiable Map<Integer, int[]> specialPageSlots() {
        var map = new HashMap<Integer, int[]>();
        this.specialPageSlots.entrySet().stream().map(entry -> Map.entry(entry.getKey(), entry.getValue().clone())).forEach(entry -> map.put(entry.getKey(), entry.getValue()));
        return Map.copyOf(map);
    }

    @Override
    public @NotNull PageButtonImpl nextButton() {
        return nextButton;
    }

    @Override
    public @NotNull PageButtonImpl previousButton() {
        return previousButton;
    }

    @Override
    public @NotNull InventoryTemplateImpl<?> inventoryTemplate() {
        return template;
    }

    public List<ItemReferenceImpl> items() {
        return items;
    }
}
