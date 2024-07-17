/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.inventoryapi.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import com.google.common.util.concurrent.AtomicDouble;
import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AsyncPagedInventory extends AnimatedInventory {

    private static final Logger LOGGER = LoggerFactory.getLogger("AsyncPagedInventory");
    private static final Key META_KEY_ARROW_TYPE = Key.key("inventory_api", "paged_inventory_arrow_type");
    protected final AtomicInteger page = new AtomicInteger(1);
    protected final ConcurrentMap<Integer, ItemStack> items = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Integer, ItemStack> pageItems = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Integer, ItemStack> fallbackItems = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Integer, ItemStack> staticItems = new ConcurrentHashMap<>();
    protected final ConcurrentMap<PageArrow, Integer[]> arrowSlots = new ConcurrentHashMap<>();
    protected final ConcurrentMap<PageArrow, ItemStack> arrowItem = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Integer, Double> updateAtSort = new ConcurrentHashMap<>();
    protected final Set<PageArrow> enabledArrows = Collections.synchronizedSet(new HashSet<>());
    protected final Queue<Integer> updateSlots = new ConcurrentLinkedQueue<>();
    protected final AtomicDouble currentSort = new AtomicDouble();
    protected final PagedListener listener;

    protected final double[] sort;
    protected final int[] slots;
    protected final int[] pageSlots;
    protected final int pageSize;

    public AsyncPagedInventory(InventoryType inventoryType, Component title, int size, BooleanSupplier instant, int[] pageSlots, int startSlot) {
        super(inventoryType, title, size, instant);
        this.slots = new int[size];
        for (var i = 0; i < this.slots.length; i++) {
            this.slots[i] = i;
        }

        this.pageSlots = pageSlots;
        this.sort = new double[this.slots.length];
        for (var i = 0; i < this.sort.length; i++) {
            this.sort[i] = IInventory.distance(this.slots[i], startSlot);
        }
        this.pageSize = this.pageSlots.length;

        this.updateSorts();

        this.insertDefaultItems();
        this.listener = new PagedListener();
        Bukkit.getPluginManager().registerEvents(this.listener, DarkCubePlugin.systemPlugin());
        this.startAnimation();
    }

    protected static int[] box(int r1, int i1, int r2, int i2) {
        final var fr1 = Math.min(r1, r2);
        final var fr2 = Math.max(r1, r2);
        final var fi1 = Math.min(i1, i2);
        final var fi2 = Math.max(i1, i2);
        var res = new int[(fr2 - fr1 + 1) * (fi2 - fi1 + 1)];
        var index = 0;
        for (var r = fr1; r <= fr2; r++) {
            for (var i = fi1; i <= fi2; i++, index++) {
                res[index] = IInventory.slot(r, i);
            }
        }
        return res;
    }

    protected void updateSorts() {
        for (var i = 0; i < this.slots.length; i++) {
            var slot = this.slots[i];
            var sort = this.sort[i];
            this.updateAtSort.put(slot, sort);
        }
    }

    @Override
    protected void tick() {
        this.currentSort.addAndGet(1);
        List<Integer> updateDone = new ArrayList<>();
        for (int slot : this.updateAtSort.keySet()) {
            double sort = this.updateAtSort.get(slot);
            if (this.shouldDisplaySort(sort)) {
                this.updateSlots.offer(slot);
                updateDone.add(slot);
            }
        }
        updateDone.forEach(this.updateAtSort::remove);
        Integer slot;
        while ((slot = this.updateSlots.poll()) != null) {
            var item = this.getItem(slot);
            this.informations.add(new AnimationInformation(slot, item));
        }
        this.postTick(!updateDone.isEmpty());
    }

    @Override
    protected void asyncOfferAnimations(Collection<AnimationInformation> informations) {
        Map<Integer, ItemStack> items = new HashMap<>();
        this.fillItems(items);

        final var page = this.getPage();
        final var skip = (page - 1) * this.pageSize;
        for (var e : this.items.entrySet()) {
            int slot = e.getKey();
            slot -= skip;
            if (slot >= this.pageSlots.length || slot < 0) {
                // Slot is out of page range
                continue;
            }
            this.updateSlots.add(this.pageSlots[slot]);
        }
        this.items.clear();

        this.items.putAll(items);
        this.calculatePageItems();
    }

    @Override
    protected void destroy() {
        super.destroy();
        HandlerList.unregisterAll(this.listener);
        this.items.clear();
        this.updateSlots.clear();
    }

    protected abstract void postTick(boolean changedInformation);

    protected void calculatePageItems() {
        Set<Integer> update = new HashSet<>(this.pageItems.keySet());
        this.pageItems.clear();
        final var page = this.getPage();
        final var skip = (page - 1) * this.pageSize;
        for (var e : this.items.entrySet()) {
            int slot = e.getKey();
            slot -= skip;
            if (slot >= this.pageSlots.length || slot < 0) {
                // Slot is out of page range
                continue;
            }
            this.pageItems.put(this.pageSlots[slot], e.getValue());
        }
        this.calculateArrows();
        update.addAll(this.pageItems.keySet());
        this.updateSlots.addAll(update);
    }

    protected void calculateArrows() {
        for (var arrow : new HashSet<>(this.enabledArrows)) {
            this.hideArrow(arrow);
        }
        final var page = this.getPage();
        final var maxSlot = this.items.keySet().stream().mapToInt(i -> i).max().orElse(0);
        final var maxPage = maxSlot / this.pageSize + 1;
        if (maxPage > page) {
            this.showArrow(PageArrow.NEXT);
        }
        if (page > 1) {
            this.showArrow(PageArrow.PREVIOUS);
        }
    }

    public void showArrow(PageArrow arrow) {
        this.enabledArrows.add(arrow);
        this.updateSlots.addAll(Arrays.asList(this.arrowSlots.get(arrow)));
    }

    public void hideArrow(PageArrow arrow) {
        this.enabledArrows.remove(arrow);
        this.updateSlots.addAll(Arrays.asList(this.arrowSlots.get(arrow)));
    }

    public boolean isArrowVisible(PageArrow arrow) {
        return this.enabledArrows.contains(arrow);
    }

    protected abstract void insertDefaultItems();

    public ItemStack getItem(int slot) {
        var sort = this.sort[slot];
        if (!this.shouldDisplaySort(sort)) {
            return null;
        }
        if (Arrays.asList(this.arrowSlots.getOrDefault(PageArrow.PREVIOUS, new Integer[0])).contains(slot) && this.isArrowVisible(PageArrow.PREVIOUS)) {
            return this.addArrowMeta(this.arrowItem.get(PageArrow.PREVIOUS), PageArrow.PREVIOUS);
        }
        if (Arrays.asList(this.arrowSlots.getOrDefault(PageArrow.NEXT, new Integer[0])).contains(slot) && this.isArrowVisible(PageArrow.NEXT)) {
            return this.addArrowMeta(this.arrowItem.get(PageArrow.NEXT), PageArrow.NEXT);
        }
        return this.pageItems.getOrDefault(slot, this.staticItems.getOrDefault(slot, this.fallbackItems.getOrDefault(slot, null)));
    }

    private ItemStack addArrowMeta(ItemStack arrowItem, PageArrow arrow) {
        if (arrowItem == null || !arrowItem.hasItemMeta()) {
            LOGGER.error("Broken inventory: No arrow item {}", this);
            return arrowItem;
        }
        return ItemBuilder.item(arrowItem).persistentDataStorage().iset(AsyncPagedInventory.META_KEY_ARROW_TYPE, PersistentDataTypes.STRING, arrow.name()).builder().build();
    }

    private PageArrow getArrowType(ItemStack arrowItem) {
        Map<String, PageArrow> arrowNames = new HashMap<>();
        Arrays.stream(PageArrow.values()).forEach(a -> arrowNames.put(a.name(), a));
        return arrowNames.get(ItemBuilder.item(arrowItem).persistentDataStorage().get(AsyncPagedInventory.META_KEY_ARROW_TYPE, PersistentDataTypes.STRING));
    }

    private boolean isArrowItem(ItemStack arrowItem) {
        var arrowNames = Arrays.stream(PageArrow.values()).map(Enum::name).toList();
        var b = ItemBuilder.item(arrowItem);
        return b.persistentDataStorage().has(AsyncPagedInventory.META_KEY_ARROW_TYPE) && arrowNames.contains(b.persistentDataStorage().get(AsyncPagedInventory.META_KEY_ARROW_TYPE, PersistentDataTypes.STRING));
    }

    public boolean shouldDisplaySort(double sort) {
        return this.isInstant() || sort <= this.currentSort.get();
    }

    protected abstract void fillItems(Map<Integer, ItemStack> items);

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPage() {
        return this.page.get();
    }

    public void setPage(int page) {
        this.page.set(page);
        this.calculatePageItems();
    }

    protected final void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        this.handlePageLogic(event);
    }

    protected void handlePageLogic(InventoryClickEvent event) {
        var item = event.getCurrentItem();
        if (this.isArrowItem(item)) {
            var arrow = this.getArrowType(item);
            if (arrow == PageArrow.PREVIOUS) {
                this.setPage(this.getPage() - 1);
            } else if (arrow == PageArrow.NEXT) {
                this.setPage(this.getPage() + 1);
            }
        }
    }

    protected class PagedListener implements Listener {

        @EventHandler
        public void handle(InventoryClickEvent event) {
            var human = event.getWhoClicked();
            if (isOpened(human)) {
                if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
                    handleClick(event);
                }
            }
        }

        @EventHandler
        public void handle(InventoryDragEvent event) {
            if (isOpened(event.getWhoClicked())) {
                event.setCancelled(true);
            }
        }

    }

}
