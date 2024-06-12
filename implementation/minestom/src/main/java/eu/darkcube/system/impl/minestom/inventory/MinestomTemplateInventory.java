/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.system.impl.server.inventory.TemplateInventoryImpl;
import eu.darkcube.system.impl.server.inventory.animated.AnimationHandler;
import eu.darkcube.system.impl.server.inventory.animated.ConfiguredAnimationHandler;
import eu.darkcube.system.impl.server.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.AsyncExecutor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;

public class MinestomTemplateInventory extends MinestomInventory implements TemplateInventoryImpl<ItemStack> {
    private final @Nullable SortedMap<Integer, Task> @NotNull [] tasks;
    private final @Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] contents;
    private final @Nullable Player player;
    private final @NotNull AnimationHandler<ItemStack> animationHandler;
    private final @NotNull AtomicInteger animationsStarted = new AtomicInteger();
    private final @NotNull Instant openInstant;
    private final @NotNull MinestomPaginationCalculator pagination;

    public MinestomTemplateInventory(@Nullable Component title, @NotNull MinestomInventoryType type, @NotNull MinestomInventoryTemplate template, @Nullable Player player) {
        super(title, type);
        this.player = player;
        for (var listener : template.listeners()) {
            this.addListener(listener);
        }
        this.contents = deepCopy(template.contents());
        this.tasks = new SortedMap[size];
        this.animationHandler = template.animation().hasAnimation() ? new ConfiguredAnimationHandler<>(this, template.animation()) : AnimationHandler.noAnimation();
        this.pagination = new MinestomPaginationCalculator(this, template);
        this.openInstant = Instant.now(); // This inventory gets opened right after creation
    }

    @Override
    protected void doOpen(@NotNull Player player) {
        if (player != this.player) {
            // Can't open the inventory for someone else than the original player
            return;
        }
        var user = UserAPI.instance().user(this.player.getUuid());
        pagination.onOpenInventory(user);
        setItems(user);
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onPreOpen(this, user);
        }
        opened.add(user);
        MinecraftServer.getSchedulerManager().scheduleNextProcess(() -> {
            player.openInventory(inventory);
            for (var i = 0; i < listeners.size(); i++) {
                listeners.get(i).onOpen(this, user);
            }
        });
    }

    /**
     * Sets the items in the inventory.
     * Starts the animation, etc.
     */
    protected synchronized void setItems(@NotNull User user) {
        if (player == null) return;
        for (var slot = 0; slot < size; slot++) {
            setItem(player, user, slot);
        }
    }

    @Override
    public @NotNull Instant openInstant() {
        return openInstant;
    }

    @Override
    public void scheduleSetItem(int slot, @NotNull Duration duration, @NotNull ItemStack item) {
        var millis = duration.toMillis();

        if (millis == 0) { // immediate
            setItem(slot, item);
        } else {
            animationsStarted.incrementAndGet();
            MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                setItem(slot, item);
                if (animationsStarted.decrementAndGet() == 0) {
                    for (var i = 0; i < listeners.size(); i++) {
                        listeners.get(i).onOpenAnimationFinished(this);
                    }
                }
            }, TaskSchedule.duration(duration), TaskSchedule.stop(), ExecutionType.TICK_END);
        }
    }

    private static class Task implements Runnable {
        private final @NotNull AtomicBoolean started = new AtomicBoolean();
        private final @NotNull CompletableFuture<@NotNull ItemStack> future = new CompletableFuture<>();
        private final @NotNull Object item;
        private final @Nullable User user;

        public Task(@Nullable User user, @NotNull Object item) {
            this.user = user;
            this.item = item;
        }

        @Override
        public void run() {
            var itemStack = MinestomInventoryUtils.computeItem(user, item);
            future.complete(itemStack);
        }

        public boolean casStarted() {
            return started.compareAndSet(false, true);
        }
    }

    private synchronized void setItem(@NotNull Player player, @NotNull User user, int slot) {
        var contentMap = contents[slot];
        if (contentMap == null) {
            setItem(slot, ItemStack.AIR);
            return;
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
            if (!setItem(player, user, slot, taskKey, itemReference, taskMap)) {
                break;
            }
        }
    }

    private boolean setItem(@NotNull Player player, @NotNull User user, int slot, int taskKey, @NotNull ItemReferenceImpl itemReference, SortedMap<Integer, Task> taskMap) {

        ensureTaskStarted(taskMap, player, taskKey, user, itemReference, slot);

        var task = taskMap.get(taskKey);
        if (task.future.isDone()) {
            if (task.future.isCompletedExceptionally()) {
                task.future.exceptionNow().printStackTrace();
            } else {
                var itemStack = task.future.resultNow();
                MinecraftServer.getSchedulerManager().scheduleNextProcess(() -> animationHandler.setItem(this, slot, itemStack));
            }
            // found a future, we can safely stop starting tasks
            return false;
        }
        return true;
    }

    private void ensureTaskStarted(@NotNull SortedMap<Integer, Task> taskMap, @NotNull Player player, int taskKey, @NotNull User user, @NotNull ItemReferenceImpl itemReference, int slot) {
        var createTask = !taskMap.containsKey(taskKey);

        if (createTask) {
            var task = new Task(user, itemReference.item());
            var prev = taskMap.put(taskKey, task);
            if (prev != null) new IllegalStateException("Somehow overrode task").printStackTrace();
            var started = tryStartTask(itemReference, task);

            if (started) { // should always be true rn
                task.future.thenRun(() -> setItem(player, user, slot));
            }
        }
    }

    private boolean tryStartTask(ItemReferenceImpl reference, Task task) {
        if (!task.casStarted()) return false;
        var async = reference.isAsync();
        if (async) {
            AsyncExecutor.cachedService().submit(task);
        } else {
            task.run();
        }
        return true;
    }

    private static @Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] deepCopy(@Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] maps) {
        var result = new SortedMap[maps.length];
        for (var i = 0; i < result.length; i++) {
            var data = maps[i];
            if (data == null) continue;
            var map = new TreeMap<Integer, ItemReferenceImpl>(Comparator.reverseOrder());
            result[i] = map;
            for (var entry : data.entrySet()) {
                map.put(entry.getKey(), entry.getValue().clone());
            }
        }
        return result;
    }
}
