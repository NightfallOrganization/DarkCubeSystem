/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.paged;

import static eu.darkcube.system.impl.server.inventory.InventoryAPIUtils.LOGGER;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import eu.darkcube.system.impl.server.inventory.SimpleItemHandler;
import eu.darkcube.system.impl.server.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.Cache;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.Caffeine;
import eu.darkcube.system.libs.org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.paged.PageButton;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContentProvider;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public class PaginationCalculator<PlatformItem, PlatformPlayer> {
    private static final int PREVIEW_RANGE = 1;
    private static final int UNLOAD_RANGE = PREVIEW_RANGE + 1; // Unload everything as soon as it leaves preview.
    private final @NotNull SimpleItemHandler<PlatformItem, PlatformPlayer> itemHandler;
    private final boolean enabled;
    private final PagedTemplateSettingsImpl pagination;
    private final PagedInventoryContentImpl content;
    private final int pageSize;
    private final BigInteger pageSizeBigInt;
    private final boolean async;
    private final ButtonImpl prevButton;
    private final ButtonImpl nextButton;
    private final Cache<BigInteger, PageCache<PlatformItem>> pageCache;
    private final Object inventoryLock;
    private final PagedInventoryContentProvider provider;
    private final @Nullable BigInteger @NotNull [] loadedPages = new BigInteger[1 + UNLOAD_RANGE * 2];
    private final int loadedPageIdx = UNLOAD_RANGE;
    private final @Nullable Updater updater;
    private int[] viewSortedSlots;
    private BigInteger expectedSize;
    private BigInteger pageFirstIndex;
    private BigInteger pageLastIndex;
    private boolean unknownSize;
    private @MonotonicNonNull User user;
    private int viewPageSize = 0;
    private boolean loadingPage = false;

    public PaginationCalculator(@NotNull SimpleItemHandler<PlatformItem, PlatformPlayer> itemHandler, Object inventoryLock) {
        this.itemHandler = itemHandler;
        this.inventoryLock = inventoryLock;
        this.pagination = itemHandler.template().pagination().clone();
        this.pageSize = this.pagination.pageSlots.length;
        this.pageSizeBigInt = BigInteger.valueOf(this.pageSize);
        this.pageCache = Caffeine.newBuilder().build();
        this.enabled = this.pageSize != 0;
        this.content = this.pagination.content;
        this.async = this.pagination.content.isAsync();
        this.prevButton = new ButtonImpl(this.pagination.previousButton());
        this.nextButton = new ButtonImpl(this.pagination.nextButton());
        this.provider = this.content.provider();
        var configured = this.pagination.isConfigured();
        this.updater = configured ? new Updater() : null;

        if (configured) {
            if (!enabled) {
                throw new IllegalArgumentException("Paged inventory MUST have pageSlots configured with at least 1 slot. Even if specialPageSlots are configured, pageSlots must be usable as fallback");
            }
            this.prevButton.init();
            this.nextButton.init();

            this.pagination.content.addUpdater(this.updater);
            itemHandler.template().pagination().content.addUpdater(this.updater);

            var contents = itemHandler.contents();
            for (var slot = 0; slot < contents.length; slot++) {
                var contentMap = contents[slot];
                if (contentMap == null) {
                    contentMap = new TreeMap<>(Comparator.reverseOrder());
                    contents[slot] = contentMap;
                }

                final var finalSlot = slot;
                var old = contentMap.put(PagedInventoryContent.PRIORITY, new ItemReferenceImpl((Function<User, Object>) user -> getItem(user, finalSlot), this.async));
                if (old != null) {
                    LOGGER.error("Overrode old item at priority " + PagedInventoryContent.PRIORITY + ", slot {}. Do not use this priority when the inventory is paged, or undefined display behaviour follows.", slot);
                }
            }
        }
    }

    public PagedTemplateSettingsImpl pagination() {
        return pagination;
    }

    public void onOpen(@NotNull User user) {
        if (!this.pagination.isConfigured()) return;
        this.prevButton.load(user);
        this.nextButton.load(user);
        loadPage0(user, BigInteger.ZERO);
    }

    public void onClose(@NotNull User user) {
        if (this.updater != null) {
            this.itemHandler.template().pagination().content.removeUpdater(this.updater);
        }
    }

    public void loadPage(@NotNull BigInteger page) {
        loadPage0(this.user, page);
    }

    public void loadPreviousPage() {
        incrementPageBy(BigInteger.valueOf(-1L));
    }

    public void loadNextPage() {
        incrementPageBy(BigInteger.ONE);
    }

    private void incrementPageBy(BigInteger count) {
        synchronized (this.inventoryLock) {
            var currentPage = this.loadedPages[this.loadedPageIdx];
            var requestedPage = currentPage == null ? BigInteger.ZERO : currentPage.add(count);
            loadPage0(this.user, requestedPage);
        }
    }

    private void loadPage0(@NotNull User user, @NotNull BigInteger page) {
        synchronized (this.inventoryLock) {
            this.user = user;
            this.loadingPage = true;
            updateInformation(page);
            this.loadingPage = false;
            this.itemHandler.updateSlots(PagedInventoryContent.PRIORITY, this.pagination.pageSlots);
        }
    }

    /**
     * Updates the loadedPages array. Calls {@link #unloadPages(BigInteger...)} and {@link #loadPages(BigInteger...)} for all changes.
     */
    private BigInteger[] updateLoadedPages(@NotNull BigInteger page) {
        var offset = UNLOAD_RANGE - PREVIEW_RANGE;
        var previewPages = new ArrayList<BigInteger>(PREVIEW_RANGE * 2 + 1);
        for (var i = -PREVIEW_RANGE; i <= PREVIEW_RANGE; i++) {
            var p = page.add(BigInteger.valueOf(i));
            if (p.compareTo(BigInteger.ZERO) >= 0) {
                previewPages.add(p);
            } else {
                offset++;
            }
        }

        var unloadPages = new ArrayList<BigInteger>();
        for (var i = 0; i < this.loadedPages.length; i++) {
            var loadedPage = this.loadedPages[i];
            if (loadedPage == null) continue;
            if (loadedPage.subtract(page).abs().compareTo(BigInteger.valueOf(UNLOAD_RANGE)) <= 0) continue;
            // if (previewPages.contains(loadedPage)) continue;
            // all pages that reach this stage must be unloaded
            unloadPages.add(loadedPage);
            this.loadedPages[i] = null;
        }

        unloadPages(unloadPages.toArray(BigInteger[]::new));

        var loadPages = new ArrayList<BigInteger>();
        for (var i = 0; i < previewPages.size(); i++) {
            var newPage = previewPages.get(i);
            var foundExistingPage = moveTo(newPage, i + offset);

            if (!foundExistingPage) {
                insertAt(newPage, i + offset);
                loadPages.add(newPage);
            }
        }

        return loadPages(loadPages.toArray(BigInteger[]::new));
    }

    private void recalculateAll() {
        synchronized (this.inventoryLock) {
            var currentPage = this.loadedPages[this.loadedPageIdx];
            var unloadPages = new ArrayList<BigInteger>();
            for (var i = 0; i < this.loadedPages.length; i++) {
                var loadedPage = this.loadedPages[i];
                if (loadedPage != null) {
                    this.loadedPages[i] = null;
                    unloadPages.add(loadedPage);
                }
            }
            unloadPages(unloadPages.toArray(BigInteger[]::new));
            loadPage0(user, currentPage == null ? BigInteger.ZERO : currentPage);
        }
    }

    private void insertAt(BigInteger page, int index) {
        if (this.loadedPages[index] == null) {
            this.loadedPages[index] = page;
            return;
        }
        var toMove = this.loadedPages[index];
        for (var i = 0; i < this.loadedPages.length; i++) {
            if (loadedPages[i] == null) {
                this.loadedPages[i] = toMove;
                this.loadedPages[index] = page;
                return;
            }
        }
        throw new IllegalStateException("Failed to insert " + page + " at " + index + " in " + Arrays.toString(this.loadedPages));
    }

    private boolean moveTo(@NotNull BigInteger page, int index) {
        for (var i = 0; i < this.loadedPages.length; i++) {
            var loadedPage = this.loadedPages[i];
            if (loadedPage != null) {
                if (page.equals(loadedPage)) {
                    swap(this.loadedPages, index, i);
                    return true;
                }
            }
        }
        return false;
    }

    private static <T> void swap(T[] array, int i, int j) {
        var old = array[i];
        array[i] = array[j];
        array[j] = old;
    }

    private BigInteger[] loadPages(@NotNull BigInteger @NotNull ... pages) {
        for (var pageIdx : pages) {
            var page = new PageCache<PlatformItem>(this.pageSize);
            this.pageCache.put(pageIdx, page);
        }
        return pages;
    }

    private void unloadPages(@NotNull BigInteger @NotNull ... pages) {
        for (var page : pages) {
            this.pageCache.invalidate(page);
        }
    }

    private void updateInformation(@NotNull BigInteger page) {
        this.expectedSize = this.provider.size();
        this.pageFirstIndex = page.multiply(this.pageSizeBigInt);
        this.pageLastIndex = this.pageFirstIndex.add(BigInteger.valueOf(this.pageSize - 1));
        this.unknownSize = this.expectedSize.equals(PagedInventoryContentProvider.SIZE_UNKNOWN);
        var pagesToLoadItems = updateLoadedPages(page);

        var computationResult = provideItems(page, pagesToLoadItems);
        var entryMap = computationResult.entries();

        for (var entryEntry : entryMap.entrySet()) {
            var entryPage = entryEntry.getKey();
            var entry = entryEntry.getValue();
            computeItems(entryPage, entry.items(), entry.fromIndex(), entry.toIndex());
        }
        var pageCache = this.pageCache.getIfPresent(page);
        if (pageCache == null) {
            throw new IllegalStateException("PageCache can't be null here");
        }
        var nextPageCache = this.pageCache.getIfPresent(page.add(BigInteger.ONE));

        // var hasNextPage = this.unknownSize ? references.length >= length : this.pageLastIndex.compareTo(this.expectedSize) < 0;
        // var hasNextPage = pageCache.currentItemCount == this.pageSize;
        var hasNextPage = this.unknownSize ? (nextPageCache != null && nextPageCache.currentItemCount > 0) : this.pageLastIndex.compareTo(this.expectedSize.subtract(BigInteger.ONE)) < 0;
        var hasPrevPage = BigInteger.ZERO.compareTo(this.pageFirstIndex) < 0;

        this.viewPageSize = pageCache.currentItemCount;
        this.prevButton.hasPage(hasPrevPage);
        this.nextButton.hasPage(hasNextPage);
        this.viewSortedSlots = Arrays.copyOf(this.pagination.pageSlots, this.viewPageSize);
        this.pagination.sorter.sort(this.viewSortedSlots);
    }

    private ComputationResult provideItems(BigInteger currentPage, BigInteger[] pages) {
        Arrays.sort(pages);
        var calculations = new HashMap<BigInteger, Integer>();
        BigInteger calculationToExtend = null;
        BigInteger currentCalculationStart = null;
        var currentCalculationLength = 1;
        BigInteger lastPage = null;
        for (var page : pages) {
            if (currentCalculationStart == null) {
                currentCalculationStart = page;
            }

            if (lastPage != null) {
                if (lastPage.add(BigInteger.ONE).equals(page)) {
                    currentCalculationLength++;
                } else {
                    if (lastPage.equals(currentPage)) {
                        calculationToExtend = currentCalculationStart;
                    }
                    calculations.put(currentCalculationStart, currentCalculationLength);
                    currentCalculationStart = page;
                    currentCalculationLength = 1;
                }
            }
            lastPage = page;
        }
        if (currentCalculationStart != null) {
            if (lastPage.equals(currentPage)) {
                calculationToExtend = currentCalculationStart;
            }
            calculations.put(currentCalculationStart, currentCalculationLength);
        }

        var computations = new HashMap<BigInteger, ComputationResult.Entry>();
        for (var entry : calculations.entrySet()) {
            var start = entry.getKey();
            var pageLength = entry.getValue();
            var length = this.pageSize * pageLength;
            var extend = start.equals(calculationToExtend);
            if (extend) {
                length++;
            }
            var startIndex = start.multiply(this.pageSizeBigInt);
            var items = this.provider.provideItem(startIndex, length, ItemReferenceImpl::new);

            for (var i = 0; i < pageLength; i++) {
                var p = start.add(BigInteger.valueOf(i));
                var fromIndex = this.pageSize * i;
                var toIndex = fromIndex + this.pageSize;
                var extendEntry = extend && i + 1 == pageLength;
                computations.put(p, new ComputationResult.Entry(items, fromIndex, toIndex, extendEntry));
            }
        }
        return new ComputationResult(computations);
    }

    public @NotNull BigInteger currentPage() {
        synchronized (this.inventoryLock) {
            var currentPage = this.loadedPages[this.loadedPageIdx];
            if (currentPage != null) return currentPage;
            return BigInteger.valueOf(-1L);
        }
    }

    private record ComputationResult(Map<BigInteger, Entry> entries) {
        private record Entry(ItemReference[] items, int fromIndex, int toIndex, boolean extend) {
        }
    }

    private void computeItems(BigInteger page, ItemReference[] itemReferences, int fromIndex, int toIndex) {
        var viewIndex = page.multiply(this.pageSizeBigInt);
        var pageCache = this.pageCache.getIfPresent(page);
        if (pageCache == null) {
            pageCache = new PageCache<>(this.pageSize);
            this.pageCache.put(page, pageCache);
        }
        pageCache.currentItemCount = 0;
        for (var i = fromIndex; i < toIndex; i++) {
            if (itemReferences.length <= i) break;

            var reference = (ItemReferenceImpl) itemReferences[i];
            var index = viewIndex.add(BigInteger.valueOf(i));
            var pageIndex = index.mod(this.pageSizeBigInt).intValueExact();
            pageCache.currentItemCount++;
            if (reference.isAsync()) {
                var finalPageCache = pageCache;
                itemHandler.service().submit(() -> syncCompute(finalPageCache, user, reference.item(), pageIndex));
            } else {
                syncCompute(pageCache, user, reference.item(), pageIndex);
            }
        }
    }

    private void syncCompute(PageCache<PlatformItem> pageCache, User user, Object object, int pageIndex) {
        var item = this.itemHandler.inventory().computeItem(user, object);
        pageCache.forceCache(pageIndex, item);
        // var itemUpdated = pageCache.cache(pageIndex, item);
        // if (itemUpdated) {
        synchronized (this.inventoryLock) {
            if (!this.loadingPage) {
                // TODO we might want to only update the slot for the specified item.
                //  Hard to figure out that slot though, maybe something for the future.
                this.itemHandler.updateSlots(PagedInventoryContent.PRIORITY, pagination.pageSlots);
            }
        }
        // }
    }

    /**
     * Get the item at the specified slot. Can return a content item or an arrow item.
     */
    private @Nullable Object getItem(@NotNull User user, int slot) {
        synchronized (this.inventoryLock) {
            var res = this.prevButton.getItem(user, slot);
            if (res != null) return res;
            res = this.nextButton.getItem(user, slot);
            if (res != null) return res;
            var viewPageSize = this.viewPageSize;
            var slots = this.pagination.specialPageSlots.get(viewPageSize);
            if (slots == null) {
                slots = this.viewSortedSlots;
            }
            var loadedPage = this.loadedPages[this.loadedPageIdx];
            if (loadedPage != null) {
                for (var i = 0; i < slots.length; i++) {
                    var s = slots[i];
                    if (s == slot) {
                        @Nullable var page = this.pageCache.getIfPresent(loadedPage);
                        if (page != null) {
                            return page.cache(i);
                        } else {
                            return null;
                        }
                    }
                }
            }
            return null;
        }
    }

    public void handleClick(int slot, @NotNull PlatformItem itemStack, @NotNull ItemBuilder item) {
        if (!this.pagination.isConfigured()) return;
        LOGGER.debug("Page click event - slot: {}", slot);
        this.prevButton.handleClick(slot, itemStack, item);
        this.nextButton.handleClick(slot, itemStack, item);
    }

    public PagedInventoryContentImpl.Updater updater() {
        return updater;
    }

    private static class PageCache<PlatformItem> {
        private static final VarHandle ELEMENT = MethodHandles.arrayElementVarHandle(Object[].class);
        private final Object[] items;
        private int currentItemCount = 0;

        public PageCache(int pageSize) {
            this.items = new Object[pageSize];
        }

        private PlatformItem cache(int pageIndex) {
            return (PlatformItem) items[pageIndex];
        }

        private boolean cache(int pageIndex, PlatformItem item) {
            return ELEMENT.compareAndSet(this.items, pageIndex, null, item);
        }

        private void forceCache(int pageIndex, PlatformItem item) {
            ELEMENT.setVolatile(this.items, pageIndex, item);
        }
    }

    private class ButtonImpl {
        private final PageButtonImpl button;
        private boolean buttonConfigured = false;
        private PlatformItem item;
        private int[] slots;
        private PageButton.Visibility visibility;
        /**
         * If there is a page this button can change to
         */
        private boolean hasPage = false;

        public ButtonImpl(PageButtonImpl button) {
            this.button = button;
        }

        private void handleClick(int slot, @NotNull PlatformItem itemStack, @NotNull ItemBuilder item) {
            if (!this.hasPage) return;
            if (!this.containsSlot(slot)) return;
            if (this == prevButton) {
                loadPreviousPage();
            } else if (this == nextButton) {
                loadNextPage();
            } else {
                throw new IllegalStateException();
            }
        }

        private void hasPage(boolean hasPage) {
            if (this.hasPage == hasPage) return;
            this.hasPage = hasPage;
            itemHandler.updateSlots(PagedInventoryContent.PRIORITY, this.slots);
        }

        private boolean containsSlot(int slot) {
            for (var s : this.slots) {
                if (slot == s) {
                    return true;
                }
            }
            return false;
        }

        private void load(User user) {
            if (!enabled) return;
            if (!this.buttonConfigured) return;
            var reference = this.button.item();
            if (reference == null) throw new IllegalStateException("Button not configured");
            this.item = itemHandler.inventory().computeItem(user, reference.item());
            if (this.item == null) throw new IllegalStateException("Button item not configured");
        }

        private void init() {
            if (!enabled) return;
            this.slots = this.button.slots();
            if (this.slots.length == 0) return;
            this.buttonConfigured = true;
            this.visibility = this.button.visibility();
        }

        private @Nullable Object getItem(@NotNull User user, int slot) {
            if (!this.buttonConfigured) return null;
            for (var s : slots) {
                if (s == slot) {
                    if (visibility == PageButton.Visibility.ALWAYS) {
                        return item;
                    } else if (visibility == PageButton.Visibility.WHEN_USABLE && hasPage) {
                        return item;
                    }
                    break;
                }
            }
            return null;
        }
    }

    private class Updater implements PagedInventoryContentImpl.Updater {
        @Override
        public void update(BigInteger index) {
            updateAll();
        }

        @Override
        public void updatePage() {
            updateAll();
        }

        @Override
        public void updateAll() {
            recalculateAll();
        }

        @Override
        public void updateRemoveAt(BigInteger index) {
            updateAll();
        }

        @Override
        public void updateInsertBefore(BigInteger index) {
            updateAll();
        }

        @Override
        public void updateInsertAfter(BigInteger index) {
            updateAll();
        }
    }
}
