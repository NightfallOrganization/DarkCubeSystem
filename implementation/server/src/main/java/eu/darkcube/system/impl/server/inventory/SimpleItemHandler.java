/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import eu.darkcube.system.impl.server.inventory.animated.AnimationHandler;
import eu.darkcube.system.impl.server.inventory.animated.ConfiguredAnimationHandler;
import eu.darkcube.system.impl.server.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.impl.server.inventory.paged.PaginationCalculator;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.AsyncExecutor;

public class SimpleItemHandler<PlatformItem, PlatformPlayer> implements InventoryItemHandler<PlatformItem, PlatformPlayer> {
    private final @NotNull TemplateInventoryImpl<PlatformItem> inventory;
    private final @NotNull InventoryTemplateImpl<PlatformPlayer> template;
    private final @NotNull AbstractInventory<PlatformItem> abstractInventory;
    private final @NotNull AnimationHandler<PlatformItem> animationHandler;
    private final int size;
    private final @Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] contents;
    private final @Nullable SortedMap<Integer, ItemComputeTask<PlatformItem>> @NotNull [] tasks;
    private final ExecutorService service = AsyncExecutor.virtualService();
    private final @NotNull PaginationCalculator<PlatformItem, PlatformPlayer> paginationCalculator;
    private User user;
    private PlatformPlayer player;

    public SimpleItemHandler(@NotNull TemplateInventoryImpl<PlatformItem> inventory, @NotNull InventoryTemplateImpl<PlatformPlayer> template) {
        this.inventory = inventory;
        this.template = template;
        this.abstractInventory = (AbstractInventory<PlatformItem>) inventory;
        this.size = abstractInventory.size;
        this.animationHandler = template.animation().hasAnimation() ? new ConfiguredAnimationHandler<>(inventory, template.animation()) : AnimationHandler.noAnimation();
        this.contents = TemplateInventoryImpl.deepCopy(template.contents());
        this.tasks = new SortedMap[size];
        this.paginationCalculator = new PaginationCalculator<>(this, this);
    }

    public ExecutorService service() {
        return service;
    }

    public void updateSlots(int priority, int... slots) {
        synchronized (this) {
            for (var slot : slots) {
                var map = tasks[slot];
                if (map != null) {
                    var task = map.remove(priority);
                    task.cancel();
                }
            }

            // this is inside synchronized block to increase performance.
            // updateSlot(...) also synchronizes, so we can synchronize here to only synchronize once
            // instead of for every slot
            for (var slot : slots) {
                updateSlot(player, user, slot);
            }
        }
    }

    @Override
    public @NotNull TemplateInventoryImpl<PlatformItem> inventory() {
        return inventory;
    }

    public @NotNull InventoryTemplateImpl<PlatformPlayer> template() {
        return template;
    }

    public @NotNull AbstractInventory<PlatformItem> abstractInventory() {
        return abstractInventory;
    }

    public @Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] contents() {
        return contents;
    }

    @Override
    public void doOpen(@NotNull PlatformPlayer player, @NotNull User user) {
        this.user = user;
        this.player = player;
        this.paginationCalculator.onOpen(user);
        for (var slot = 0; slot < size; slot++) {
            setItem(player, user, slot);
        }
    }

    @Override
    public void doClose(@NotNull PlatformPlayer platformPlayer, @NotNull User user) {
        this.paginationCalculator.onClose(user);
    }

    @Override
    public void handleClick(int slot, @NotNull PlatformItem itemStack, @NotNull ItemBuilder item) {
        this.paginationCalculator.handleClick(slot, itemStack, item);
    }

    private void updateSlot(@NotNull PlatformPlayer player, @NotNull User user, int slot) {
        setItem(player, user, slot);
    }

    /**
     * Sets the item on the slot to the wanted item. Might not be instantaneously, because of animation settings.
     */
    private void setItem(@NotNull PlatformPlayer player, @NotNull User user, int slot) {
        var item = calculateItem(player, user, slot);
        if (item == null) {
            inventory.onMainThread(() -> inventory.setAir(slot));
            return;
        }
        inventory.onMainThread(() -> animationHandler.setItem(inventory, slot, item));
    }

    /**
     * Calculates the item that should go into a specified slot
     *
     * @return the item, or null if air
     */
    private synchronized @Nullable PlatformItem calculateItem(@NotNull PlatformPlayer player, @NotNull User user, int slot) {
        var contentMap = contents[slot];
        if (contentMap == null) {
            return null;
        }
        var taskMap = tasks[slot];
        if (taskMap == null) {
            taskMap = new TreeMap<>();
            tasks[slot] = taskMap;
        }

        // try find the first task that is finished or sync. If it is sync, run it and use that task.
        // start all async tasks to calculate their values. when an async task finishes, recalculate the slot item
        // highest priorities first, map is in reverse

        for (var contentEntry : contentMap.sequencedEntrySet()) {
            var itemReference = contentEntry.getValue();
            var taskKey = contentEntry.getKey();
            var taskItem = calculateItem(player, user, slot, taskKey, itemReference, taskMap);
            if (taskItem != null) {
                return taskItem;
            }
        }
        // no item specified
        return null;
    }

    /**
     * Calculates the item for the specified task.
     *
     * @return the item for the specified task, null if task is async and item is not calculated yet.
     */
    private @Nullable PlatformItem calculateItem(@NotNull PlatformPlayer player, @NotNull User user, int slot, int taskKey, @NotNull ItemReferenceImpl itemReference, SortedMap<Integer, ItemComputeTask<PlatformItem>> taskMap) {
        ensureTaskStarted(taskMap, player, taskKey, user, itemReference, slot);

        var task = taskMap.get(taskKey);
        if (task.future.isDone()) {
            if (task.future.isCompletedExceptionally()) {
                task.future.exceptionNow().printStackTrace();
                return null;
            } else {
                return task.future.resultNow();
            }
        }
        return null;
    }

    private void ensureTaskStarted(@NotNull SortedMap<Integer, ItemComputeTask<PlatformItem>> taskMap, @NotNull PlatformPlayer player, int taskKey, @NotNull User user, @NotNull ItemReferenceImpl itemReference, int slot) {
        if (taskMap.containsKey(taskKey)) return;

        var task = new ItemComputeTask<>(inventory, itemReference.item(), user);
        var prev = taskMap.put(taskKey, task);
        if (prev != null) new IllegalStateException("Somehow overrode task").printStackTrace();
        var started = tryStartTask(itemReference, task);

        if (started) { // should always be true rn
            task.future.thenRun(() -> updateSlot(player, user, slot));
        } else {
            throw new IllegalStateException();
        }
    }

    private boolean tryStartTask(ItemReferenceImpl reference, ItemComputeTask<PlatformItem> task) {
        if (!task.casStarted()) return false;
        var async = reference.isAsync();
        if (async) {
            service.submit(task);
        } else {
            task.run();
        }
        return true;
    }

    public static class ItemComputeTask<PlatformItem> implements Runnable {
        private final @NotNull AtomicBoolean started = new AtomicBoolean();
        private final @NotNull CompletableFuture<@Nullable PlatformItem> future = new CompletableFuture<>();
        private final @NotNull TemplateInventoryImpl<PlatformItem> inventory;
        private final @Nullable Object item;
        private final @Nullable User user;

        private ItemComputeTask(@NotNull TemplateInventoryImpl<PlatformItem> inventory, @Nullable Object item, @Nullable User user) {
            this.inventory = inventory;
            this.item = item;
            this.user = user;
        }

        @Override
        public void run() {
            var itemStack = inventory.computeItem(user, item);
            future.complete(itemStack);
        }

        /**
         * Makes best efforts to cancel this task to release resources.
         */
        public void cancel() {
            // we do nothing here, at least nothing yet.
            // this is just a best effort
        }

        public boolean casStarted() {
            return started.compareAndSet(false, true);
        }

        public @NotNull AtomicBoolean started() {
            return started;
        }

        public @NotNull CompletableFuture<@Nullable PlatformItem> future() {
            return future;
        }

        public @NotNull TemplateInventoryImpl<PlatformItem> inventory() {
            return inventory;
        }

        public @NotNull Object item() {
            return item;
        }

        public @Nullable User user() {
            return user;
        }
    }
}
