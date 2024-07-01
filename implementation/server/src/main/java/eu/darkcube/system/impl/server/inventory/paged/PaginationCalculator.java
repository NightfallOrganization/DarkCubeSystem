/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.paged;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Logger;

import eu.darkcube.system.impl.server.inventory.SimpleItemHandler;
import eu.darkcube.system.impl.server.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.Cache;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.Caffeine;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.paged.PageButton;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContentProvider;
import eu.darkcube.system.userapi.User;

public class PaginationCalculator<PlatformItem, PlatformPlayer> {
    private static final Logger LOGGER = Logger.getLogger("InventoryAPI");
    private final @NotNull SimpleItemHandler<PlatformItem, PlatformPlayer> itemHandler;
    private final boolean enabled;
    private final PagedTemplateSettingsImpl pagination;
    private final PagedInventoryContentImpl content;
    private final int pageSize;
    private final BigInteger pageSizeBigInt;
    private final boolean async;
    private final @NotNull Cache<BigInteger, PlatformItem> itemCache;
    private final ButtonImpl prevButton;
    private final ButtonImpl nextButton;
    private final Object inventoryLock;
    private int[] viewSortedSlots;
    private @NotNull BigInteger viewPageIndex = BigInteger.ZERO;
    private int viewPageSize = 0;
    private boolean loadingPage = false;

    public PaginationCalculator(@NotNull SimpleItemHandler<PlatformItem, PlatformPlayer> itemHandler, Object inventoryLock) {
        this.itemHandler = itemHandler;
        this.inventoryLock = inventoryLock;
        this.pagination = itemHandler.template().pagination().clone();
        this.pageSize = this.pagination.pageSlots.length;
        this.pageSizeBigInt = BigInteger.valueOf(this.pageSize);
        this.enabled = this.pageSize != 0;
        this.content = this.pagination.content;
        this.async = this.pagination.content.isAsync();
        this.itemCache = Caffeine.newBuilder().build();
        this.prevButton = new ButtonImpl(this.pagination.previousButton());
        this.nextButton = new ButtonImpl(this.pagination.nextButton());

        if (pagination.isConfigured()) {
            if (!enabled) {
                throw new IllegalArgumentException("Paged inventory MUST have pageSlots configured with at least 1 slot. Even if specialPageSlots are configured, pageSlots must be usable as fallback");
            }
            this.prevButton.init();
            this.nextButton.init();

            var updater = new Updater();
            this.pagination.content.addUpdater(updater);
            itemHandler.template().pagination().content.addUpdater(updater);

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
                    LOGGER.severe("Overrode old item at priority " + PagedInventoryContent.PRIORITY + ", slot " + slot + ". Do not use this priority when the inventory is paged, or undefined display behaviour follows.");
                }
            }
        }
    }

    public void onOpen(@NotNull User user) {
        prevButton.load(user);
        nextButton.load(user);
        loadPage0(user, BigInteger.ZERO);
    }

    public void loadPage(@NotNull BigInteger page) {
        throw new UnsupportedOperationException();
    }

    private void loadPage0(@NotNull User user, @NotNull BigInteger page) {
        synchronized (inventoryLock) {
            this.loadingPage = true;
            var provider = content.provider();
            var contentSize = provider.size();
            var viewIndex = page.multiply(BigInteger.valueOf(pageSize));
            var unknownSize = contentSize.equals(PagedInventoryContentProvider.SIZE_UNKNOWN);
            var length = pageSize;
            if (unknownSize) {
                length++; // Include first element on next page. This is used to check if there is a next page.
            }

            var references = provider.provideItem(viewIndex, length, ItemReferenceImpl::new);

            // this is the last item visible in the page. Used for checking if there are more pages
            var pageLastIndex = viewIndex.add(BigInteger.valueOf(this.pageSize - 1));

            var cmp = contentSize.compareTo(viewIndex);
            if (cmp < 0) {
                // We are out of bounds. We do not clamp (yet) and instead the user has to manually go to previous pages
                // should be fine...
            }

            var hasNextPage = unknownSize ? references.length >= length : pageLastIndex.compareTo(contentSize) < 0;
            var hasPrevPage = BigInteger.ZERO.compareTo(viewIndex) < 0;

            this.viewPageIndex = page;
            this.viewPageSize = references.length;
            this.prevButton.hasPage = hasPrevPage;
            this.nextButton.hasPage = hasNextPage;
            this.viewSortedSlots = Arrays.copyOf(this.pagination.pageSlots, this.viewPageSize);
            this.pagination.sorter.sort(this.viewSortedSlots);

            for (var i = 0; i < references.length; i++) {
                var reference = (ItemReferenceImpl) references[i];
                var index = viewIndex.add(BigInteger.valueOf(i));
                if (reference.isAsync()) {
                    itemHandler.service().submit(() -> syncCompute(user, reference.item(), index));
                } else {
                    syncCompute(user, reference.item(), index);
                }
            }
            this.loadingPage = false;
            itemHandler.updateSlots(PagedInventoryContent.PRIORITY, pagination.pageSlots);
        }
    }

    private void syncCompute(User user, Object object, BigInteger viewIndex) {
        var item = this.itemHandler.inventory().computeItem(user, object);
        synchronized (inventoryLock) {
            itemCache.put(viewIndex, item);
            if (!loadingPage) {
                // TODO we might want to only update the slot for the specified item.
                //  Hard to figure out that slot though, maybe something for the future.
                itemHandler.updateSlots(PagedInventoryContent.PRIORITY, pagination.pageSlots);
            }
        }
    }

    /**
     * Get the item at the specified slot. Can return a content item or an arrow item.
     */
    private @Nullable Object getItem(@NotNull User user, int slot) {
        synchronized (inventoryLock) {
            var res = this.prevButton.getItem(user, slot);
            if (res != null) return res;
            res = this.nextButton.getItem(user, slot);
            if (res != null) return res;
            var viewPageSize = this.viewPageSize;
            var slots = pagination.specialPageSlots.get(viewPageSize);
            if (slots == null) {
                slots = viewSortedSlots;
            }
            for (var i = 0; i < slots.length; i++) {
                var s = slots[i];
                if (s == slot) {
                    var index = viewPageIndex.add(BigInteger.valueOf(i));
                    return itemCache.getIfPresent(index);
                }
            }
            return null;
        }
    }

    private class ButtonImpl {
        private final PageButtonImpl button;
        private boolean buttonConfigured = false;
        private PlatformItem item;
        private int[] slots;
        private PageButton.Visibility visibility;
        private boolean hasPage = false;

        public ButtonImpl(PageButtonImpl button) {
            this.button = button;
        }

        private void load(User user) {
            if (!enabled) return;
            if (!buttonConfigured) return;
            var reference = button.item();
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

    private static class Updater implements PagedInventoryContentImpl.Updater {
        @Override
        public void update(int index) {
            nyi();
        }

        @Override
        public void updatePage() {
            nyi();
        }

        @Override
        public void updateAll() {
            nyi();
        }

        @Override
        public void updateRemoveAt(int index) {
            nyi();
        }

        @Override
        public void updateInsertBefore(int index) {
            nyi();
        }

        @Override
        public void updateInsertAfter(int index) {
            nyi();
        }

        private void nyi() {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
    }
}
