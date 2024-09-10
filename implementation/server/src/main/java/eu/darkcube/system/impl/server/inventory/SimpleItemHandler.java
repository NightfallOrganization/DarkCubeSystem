/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import eu.darkcube.system.impl.server.inventory.animated.AnimationHandler;
import eu.darkcube.system.impl.server.inventory.animated.ConfiguredAnimationHandler;
import eu.darkcube.system.impl.server.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.impl.server.inventory.paged.PaginationCalculator;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerListener;
import eu.darkcube.system.server.inventory.container.ContainerProvider;
import eu.darkcube.system.server.inventory.container.ContainerView;
import eu.darkcube.system.server.inventory.container.ContainerViewConfiguration;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.AsyncExecutor;

public class SimpleItemHandler<PlatformItem, PlatformPlayer> implements InventoryItemHandler<PlatformItem, PlatformPlayer> {
    private final @NotNull TemplateInventoryImpl<PlatformItem> inventory;
    private final @NotNull InventoryTemplateImpl<PlatformPlayer> template;
    private final @NotNull AnimationHandler<PlatformItem> animationHandler;
    private final int size;
    private final @Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] contents;
    private final int[] priorities;
    private final @Nullable SortedMap<Integer, ItemComputeTask<PlatformItem>> @NotNull [] tasks;
    private final @NotNull ExecutorService service = AsyncExecutor.virtualService();
    private final @NotNull List<ContainerView> containers = new CopyOnWriteArrayList<>();
    private final @NotNull List<ContainerView> containersInTransaction = new CopyOnWriteArrayList<>();
    private final @NotNull Map<ContainerView, ContainerListener> containerListeners = new HashMap<>();
    private final @NotNull Map<ContainerView, List<ItemEntry>> containersRemovedItems = new HashMap<>();
    private final @NotNull PaginationCalculator<PlatformItem, PlatformPlayer> paginationCalculator;
    private final User user;
    private final PlatformPlayer player;
    private boolean open = false;

    public SimpleItemHandler(@NotNull User user, @NotNull PlatformPlayer player, @NotNull TemplateInventoryImpl<PlatformItem> inventory, @NotNull InventoryTemplateImpl<PlatformPlayer> template) {
        this.user = user;
        this.player = player;
        this.inventory = inventory;
        this.template = template;
        this.size = inventory.size();
        this.priorities = new int[this.size];
        Arrays.fill(this.priorities, Integer.MIN_VALUE);
        this.animationHandler = useAnimations() ? new ConfiguredAnimationHandler<>(inventory, template.animation()) : AnimationHandler.noAnimation();
        this.contents = TemplateInventoryImpl.deepCopy(template.contents());
        this.tasks = new SortedMap[size];
        this.paginationCalculator = new PaginationCalculator<>(this, this);
        for (var containerFactory : this.template.containerFactories()) {
            this.addContainer(containerFactory.priority(), containerFactory.container(), containerFactory);
        }
    }

    private boolean useAnimations() {
        var animation = template.animation;
        if (!animation.hasAnimation()) return false;
        if (!animation.ignoreUserSettings()) {
            return user.settings().animations();
        }
        return true;
    }

    public ExecutorService service() {
        return service;
    }

    @Override
    public void updateSlots(int... slots) {
        updateSlots(false, slots);
    }

    public void updateSlots(boolean silent, int... slots) {
        synchronized (this) {
            for (var slot : slots) {
                var map = tasks[slot];
                if (map != null) {
                    for (var value : map.values()) {
                        value.cancel();
                    }
                    map.clear();
                }
            }
            for (var slot : slots) {
                updateSlot(silent, player, user, slot);
            }
        }
    }

    public void updateSlots(boolean silent, int priority, int... slots) {
        synchronized (this) {
            for (var slot : slots) {
                var map = tasks[slot];
                if (map != null) {
                    var task = map.remove(priority);
                    if (task != null) task.cancel();
                }
            }

            // this is inside synchronized block to increase performance.
            // updateSlot(...) also synchronizes, so we can synchronize here to only synchronize once
            // instead of for every slot
            for (var slot : slots) {
                updateSlot(silent, player, user, slot);
            }
        }
    }

    @Override
    public void updateSlots(int priority, int... slots) {
        updateSlots(false, priority, slots);
    }

    @Override
    public void startContainerTransaction(ContainerView containerView) {
        containersInTransaction.add(containerView);
    }

    @Override
    public void finishContainerTransaction(ContainerView containerView) {
        containersInTransaction.remove(containerView);
    }

    @Override
    public @NotNull ContainerView addContainer(int priority, @NotNull Container container, @NotNull ContainerViewConfiguration configuration) {
        synchronized (this) {
            var view = ContainerProvider.createView(this.inventory, container, priority);
            configuration.configureView(this.user, view);
            var l = this.inventory.templateListeners();
            for (var i = 0; i < l.size(); i++) {
                l.get(i).onContainerAdd(inventory, user, view);
            }
            this.containers.add(view);
            var entries = new ArrayList<ItemEntry>();
            var slots = view.slots();
            for (var i = 0; i < slots.length; i++) {
                var slot = slots[i];
                var item = container.getAt(i);
                var old = setItem(priority, slot, container(item == null ? ItemBuilder.item() : item));
                if (old != null) {
                    entries.add(new ItemEntry(priority, slot, old));
                }
            }
            if (!entries.isEmpty()) {
                containersRemovedItems.put(view, entries);
            }
            var listener = new ContainerListener() {
                @Override
                public void onItemAdded(int slot, @NotNull ItemBuilder item, int addAmount) {
                    var silent = containersInTransaction.contains(view);
                    setItem(silent, priority, slots[slot], container(item));
                }

                @Override
                public void onItemChanged(int slot, @NotNull ItemBuilder previousItem, @NotNull ItemBuilder newItem) {
                    var silent = containersInTransaction.contains(view);
                    setItem(silent, priority, slots[slot], container(newItem));
                }

                @Override
                public void onItemRemoved(int slot, @NotNull ItemBuilder previousItem, int removeAmount) {
                    var silent = containersInTransaction.contains(view);
                    var item = previousItem.amount() == removeAmount ? null : previousItem.clone().amount(previousItem.amount() - removeAmount);
                    setItem(silent, priority, slots[slot], container(item));
                }
            };
            container.addListener(listener);
            containerListeners.put(view, listener);
            return view;
        }
    }

    private ItemReferenceImpl container(@Nullable ItemBuilder item) {
        return new ItemReferenceImpl(item == null ? ItemBuilder.item() : item);
    }

    @Override
    public void removeContainer(@NotNull ContainerView containerView) {
        synchronized (this) {
            if (!this.containers.contains(containerView)) throw new IllegalStateException("View doesn't exist on this inventory!");
            if (open) {
                var l = this.inventory.templateListeners();
                for (var i = 0; i < l.size(); i++) {
                    l.get(i).onContainerRemove(inventory, user, containerView);
                }
            }
            var overwrittenEntries = containersRemovedItems.get(containerView);
            if (overwrittenEntries != null) {
                for (var entry : overwrittenEntries) {
                    setItem(entry.priority, entry.slot, entry.reference);
                }
            }
            var listener = containerListeners.remove(containerView);
            containerView.container().removeListener(listener);
            this.containers.remove(containerView);
        }
    }

    @Override
    public @NotNull List<ContainerView> containers() {
        return containers;
    }

    @Override
    public @Nullable Map.Entry<ContainerView, Integer> findContainer(int slot) {
        synchronized (this) {
            var slotPriority = priorities[slot];
            ContainerView selectedContainer = null;
            var selectedSlot = -1;
            for (var container : containers) {
                if (container.priority() < slotPriority) continue;
                var slots = container.slots();
                for (var i = 0; i < slots.length; i++) {
                    var s = slots[i];
                    if (slot != s) continue;
                    if (selectedContainer == null) {
                        selectedContainer = container;
                        selectedSlot = i;
                    } else if (selectedContainer.priority() < container.priority()) {
                        selectedContainer = container;
                        selectedSlot = i;
                    }
                    break;
                }
            }
            if (selectedContainer == null) return null;
            return Map.entry(selectedContainer, selectedSlot);
        }
    }

    @Override
    public @NotNull TemplateInventoryImpl<PlatformItem> inventory() {
        return inventory;
    }

    public @NotNull InventoryTemplateImpl<PlatformPlayer> template() {
        return template;
    }

    public @Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] contents() {
        return contents;
    }

    public ItemReferenceImpl setItem(int priority, int slot, ItemReferenceImpl reference) {
        return setItem(false, priority, slot, reference);
    }

    public ItemReferenceImpl setItem(boolean silent, int priority, int slot, ItemReferenceImpl reference) {
        var c = contents[slot];
        if (c == null) {
            c = new TreeMap<>(Comparator.reverseOrder());
            contents[slot] = c;
        }

        var old = c.put(priority, reference);
        if (open) {
            updateSlots(silent, priority, new int[]{slot});
        }
        return old;
    }

    public @NotNull PaginationCalculator<PlatformItem, PlatformPlayer> paginationCalculator() {
        return paginationCalculator;
    }

    @Override
    public void doOpen() {
        open = true;
        this.paginationCalculator.onOpen(user);
        for (var slot = 0; slot < size; slot++) {
            setItem(false, player, user, slot);
        }
    }

    @Override
    public void doClose() {
        this.paginationCalculator.onClose(user);
        open = false;
        for (var container : List.copyOf(this.containers)) {
            if (container.dropItemsOnClose()) {
                var items = container.container().clearItemsOnClose(user);
                for (var item : items) {
                    inventory.returnItemToUser(item);
                }
            }
            removeContainer(container);
        }
    }

    @Override
    public void handleClick(int slot, @NotNull PlatformItem itemStack, @NotNull ItemBuilder item) {
        this.paginationCalculator.handleClick(slot, itemStack, item);
    }

    private void updateSlot(boolean silent, @NotNull PlatformPlayer player, @NotNull User user, int slot) {
        setItem(silent, player, user, slot);
    }

    /**
     * Sets the item on the slot to the wanted item. Might not be instantaneously, because of animation settings.
     */
    private void setItem(boolean silent, @NotNull PlatformPlayer player, @NotNull User user, int slot) {
        var item = calculateItem(player, user, slot, silent);
        if (item == null) {
            inventory.onMainThread(() -> {
                synchronized (this) {
                    if (!silent) {
                        inventory.setAir(slot);
                    }
                    priorities[slot] = Integer.MIN_VALUE;
                }
            });
            return;
        }
        inventory.onMainThread(() -> {
            synchronized (this) {
                if (!silent) {
                    animationHandler.setItem(inventory, slot, item.getKey());
                }
                priorities[slot] = item.getValue();
            }
        });
    }

    /**
     * Calculates the item that should go into a specified slot
     *
     * @return the item, or null if air
     */
    private synchronized @Nullable Map.Entry<PlatformItem, Integer> calculateItem(@NotNull PlatformPlayer player, @NotNull User user, int slot, boolean silent) {
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
            var priority = contentEntry.getKey();
            var taskItem = calculateItem(player, user, slot, silent, priority, itemReference, taskMap);
            if (taskItem != null) {
                return Map.entry(taskItem, priority);
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
    private @Nullable PlatformItem calculateItem(@NotNull PlatformPlayer player, @NotNull User user, int slot, boolean silent, int priority, @NotNull ItemReferenceImpl itemReference, SortedMap<Integer, ItemComputeTask<PlatformItem>> taskMap) {
        ensureTaskStarted(taskMap, player, priority, user, itemReference, slot, silent);

        var task = taskMap.get(priority);
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

    private void ensureTaskStarted(@NotNull SortedMap<Integer, ItemComputeTask<PlatformItem>> taskMap, @NotNull PlatformPlayer player, int taskKey, @NotNull User user, @NotNull ItemReferenceImpl itemReference, int slot, boolean silent) {
        if (taskMap.containsKey(taskKey)) return;

        var task = new ItemComputeTask<>(inventory, itemReference.item(), user);
        var prev = taskMap.put(taskKey, task);
        if (prev != null) new IllegalStateException("Somehow overrode task").printStackTrace();
        var started = tryStartTask(itemReference, task);

        if (started) { // should always be true rn
            task.future.thenRun(() -> updateSlot(silent, player, user, slot));
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

    public record ItemEntry(int priority, int slot, ItemReferenceImpl reference) {
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

        public @NotNull TemplateInventoryImpl<PlatformItem> inventory() {
            return inventory;
        }

        public @Nullable Object item() {
            return item;
        }

        public @Nullable User user() {
            return user;
        }
    }
}
