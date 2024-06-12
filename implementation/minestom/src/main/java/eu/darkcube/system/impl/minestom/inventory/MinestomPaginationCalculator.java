/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.system.impl.server.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.impl.server.inventory.paged.PagedInventoryContentImpl;
import eu.darkcube.system.impl.server.inventory.paged.PagedTemplateSettingsImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.item.ItemReferenceProvider;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContentProvider;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.AsyncExecutor;
import net.minestom.server.item.ItemStack;

public class MinestomPaginationCalculator {
    private final @NotNull PagedTemplateSettingsImpl pagination;
    private final @NotNull MinestomTemplateInventory inventory;
    private final @NotNull PagedInventoryContentImpl content;
    private final @NotNull MinestomInventoryTemplate template;
    private final @NotNull ItemReferenceProvider itemReferenceProvider = ItemReference::createFor;
    private final @NotNull Map<BigInteger, ItemStack> computedItems = new ConcurrentHashMap<>();
    private final int pageSize;
    private volatile int[] currentSlots = new int[0];
    private volatile @NotNull BigInteger viewPageIndex = BigInteger.ZERO;

    public MinestomPaginationCalculator(@NotNull MinestomTemplateInventory inventory, @NotNull MinestomInventoryTemplate template) {
        this.inventory = inventory;
        this.template = template;
        this.pagination = template.pagination().clone();
        this.content = this.pagination.content;
        this.pageSize = this.pagination.pageSlots.length;
        if (pageSize == 0) throw new IllegalArgumentException("Paged inventory MUST have pageSlots configured with at least 1 slot. Even if specialPageSlots are configured, pageSlots must be usable as fallback");
        var updater = new Updater();
        template.pagination().content.updater(updater);
        pagination.content.updater(updater);
    }

    private class Updater implements PagedInventoryContentImpl.Updater {
        @Override
        public void update(int index) {

        }

        @Override
        public void updatePage() {

        }

        @Override
        public void updateAll() {

        }

        @Override
        public void updateRemoveAt(int index) {

        }

        @Override
        public void updateInsertBefore(int index) {

        }

        @Override
        public void updateInsertAfter(int index) {

        }
    }

    public void onOpenInventory(@Nullable User user) {
        var contentSize = content.provider().size();
        var viewIndex = viewPageIndex.multiply(BigInteger.valueOf(pageSize));

        var sizeCalculation = calculateSize(user, viewIndex, contentSize);

        if (sizeCalculation.hasNext) {

        }

    }

    private record SizeCalculation(boolean hasPrevious, boolean hasNext, int pageSize) {
    }

    private SizeCalculation calculateSize(User user, BigInteger viewIndex, BigInteger contentSize) {
        var sizeUnknown = contentSize.equals(PagedInventoryContentProvider.SIZE_UNKNOWN);
        if (sizeUnknown) {
            return calculateUnknownSize(user, viewIndex);
        } else {
            return calculateKnownSize(user, contentSize, viewIndex);
        }
    }

    private SizeCalculation calculateKnownSize(User user, @NotNull BigInteger contentSize, @NotNull BigInteger viewIndex) {
        return calculateKnownSize(user, viewIndex, pageSize, contentSize);
    }

    private SizeCalculation calculateKnownSize(User user, BigInteger viewIndex, int length, BigInteger contentSize) {
        var items = content.provider().provideItem(viewIndex, length, itemReferenceProvider);
        calculate(user, items, viewIndex);
        var endIndex = viewIndex.add(BigInteger.valueOf(length));

        var hasPrevious = viewIndex.compareTo(BigInteger.ZERO) > 0;
        var hasNext = endIndex.compareTo(contentSize) < 0;

        return new SizeCalculation(hasPrevious, hasNext, length);
    }

    private SizeCalculation calculateUnknownSize(User user, @NotNull BigInteger viewIndex) {
        var items = content.provider().provideItem(viewIndex, 1, itemReferenceProvider);
        calculate(user, items, viewIndex);
        return null;
    }

    private void calculate(User user, ItemReference[] items, BigInteger viewIndex) {
        for (var itemRef : items) {
            var itemReference = (ItemReferenceImpl) itemRef;
            if (itemReference.isAsync()) {
                AsyncExecutor.cachedService().submit(() -> syncCompute(user, itemReference.item(), viewIndex));
            } else {
                syncCompute(user, itemReference.item(), viewIndex);
            }
        }
    }

    private void syncCompute(User user, Object object, BigInteger viewIndex) {
        var item = MinestomInventoryUtils.computeItem(user, object);
        setComputedItem(viewIndex, item);
    }

    private void setComputedItem(BigInteger index, ItemStack item) {
        computedItems.put(index, item);
    }

    private void addItems(@Nullable User user, @NotNull Object object, int index, boolean isAsync, @NotNull ItemStack[] itemArray) {
        if (object instanceof ItemReferenceImpl itemReference) {
            addItems(user, itemReference, index, isAsync, itemArray);
        } else {
            var itemStack = MinestomInventoryUtils.computeItem(user, object);
            synchronized (itemArray) {
                itemArray[index] = itemStack;
            }
        }
    }

    private void addItems(@Nullable User user, @NotNull ItemReferenceImpl reference, int index, boolean isAsync, ItemStack[] itemArray) {
        var ignoreAsync = true;
        // noinspection ConstantValue
        if (reference.isAsync() && (!isAsync || ignoreAsync)) {
            AsyncExecutor.cachedService().submit(() -> addItems(user, reference.item(), index, true, itemArray));
        } else {
            addItems(user, reference.item(), index, false, itemArray);
        }
    }
}
