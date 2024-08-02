/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.paged;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.impl.server.inventory.InventoryTemplateImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.paged.PageSlotSorter;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.inventory.paged.PagedTemplateSettings;

public class PagedTemplateSettingsImpl implements PagedTemplateSettings {
    private final InventoryTemplateImpl<?> template;
    // These are public for read-only access without extra memory allocation by duplicating to prevent modification
    // Access only allowed by internal code
    public final Map<Integer, int[]> specialPageSlots;
    public final PageButtonImpl previousButton;
    public final PageButtonImpl nextButton;
    public final PagedInventoryContentImpl content;
    public int[] pageSlots = new int[0];
    public @NotNull PageSlotSorter sorter;
    public boolean configured;

    public PagedTemplateSettingsImpl(InventoryTemplateImpl<?> template) {
        this.template = template;
        this.specialPageSlots = new HashMap<>();
        this.sorter = PageSlotSorter.Sorters.DEFAULT;
        this.content = new PagedInventoryContentImpl();
        this.previousButton = new PageButtonImpl();
        this.nextButton = new PageButtonImpl();
        this.configured = false;
    }

    private PagedTemplateSettingsImpl(InventoryTemplateImpl<?> template, Map<Integer, int[]> specialPageSlots, PageButtonImpl previousButton, PageButtonImpl nextButton, PagedInventoryContentImpl content, int[] pageSlots, @NotNull PageSlotSorter sorter, boolean configured) {
        this.template = template;
        this.specialPageSlots = specialPageSlots;
        this.previousButton = previousButton;
        this.nextButton = nextButton;
        this.content = content;
        this.pageSlots = pageSlots;
        this.sorter = sorter;
        this.configured = configured;
    }

    @Override
    public boolean isConfigured() {
        return configured;
    }

    @Override
    public @NotNull PagedTemplateSettingsImpl clone() {
        var template = this.template;
        var specialPageSlots = this.specialPageSlots();
        var previousButton = this.previousButton().clone();
        var nextButton = this.nextButton().clone();
        var pageSlots = this.pageSlots();
        var content = this.content.clone();
        var sorter = this.sorter;
        var configured = this.configured;
        return new PagedTemplateSettingsImpl(template, specialPageSlots, previousButton, nextButton, content, pageSlots, sorter, configured);
    }

    @Override
    public @NotNull PagedInventoryContent content() {
        return content;
    }

    @Override
    public int @NotNull [] pageSlots() {
        return pageSlots.clone();
    }

    @Override
    public void pageSlots(int @NotNull ... pageSlots) {
        this.configured = true;
        this.pageSlots = pageSlots.clone();
    }

    @Override
    public @NotNull PageSlotSorter sorter() {
        return sorter;
    }

    @Override
    public void sorter(@NotNull PageSlotSorter sorter) {
        this.configured = true;
        this.sorter = sorter;
    }

    @Override
    public void specialPageSlots(int @NotNull ... pageSlots) {
        this.configured = true;
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
}
