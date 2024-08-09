/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.system.impl.server.inventory.InventoryItemHandler;
import eu.darkcube.system.impl.server.inventory.TemplateInventoryImpl;
import eu.darkcube.system.impl.server.inventory.controller.PagedInventoryControllerImpl;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.server.inventory.controller.PagedInventoryController;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;

public class MinestomTemplateInventory extends MinestomInventory implements TemplateInventoryImpl<ItemStack> {
    private final @Nullable Player player;
    private final @NotNull InventoryItemHandler<ItemStack, Player> itemHandler;
    private final @NotNull AtomicInteger animationsStarted = new AtomicInteger();
    private final @NotNull Instant openInstant;
    private final @NotNull PagedInventoryControllerImpl pagedController;

    public MinestomTemplateInventory(@NotNull Component title, @NotNull MinestomInventoryType type, @NotNull MinestomInventoryTemplate template, @Nullable Player player) {
        super(title, type);
        this.player = player;
        for (var listener : template.listeners()) {
            this.addListener(listener);
        }
        this.itemHandler = InventoryItemHandler.simple(this, template);
        this.openInstant = Instant.now(); // This inventory gets opened right after creation
        this.pagedController = new PagedInventoryControllerImpl(this.itemHandler);
    }

    @Override
    protected void doOpen(@NotNull Player player) {
        if (player != this.player) {
            // Can't open the inventory for someone else than the original player
            return;
        }
        var user = UserAPI.instance().user(player.getUuid());
        this.itemHandler.doOpen(player, user);
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onPreOpen(this, user);
        }
        opened.add(user);
        onMainThread(() -> {
            player.openInventory(inventory);
            for (var i = 0; i < listeners.size(); i++) {
                listeners.get(i).onOpen(this, user);
            }
        });
    }

    @Override
    protected void unregister() {
        super.unregister();
        if (this.player == null) {
            // What the heck?
            return;
        }
        var user = UserAPI.instance().user(player.getUuid());
        this.itemHandler.doClose(player, user);
    }

    @Override
    protected void handleClick(int slot, @NotNull ItemStack itemStack, @NotNull ItemBuilder item) {
        this.itemHandler.handleClick(slot, itemStack, item);
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

    @Override
    public @Nullable ItemStack computeItem(@Nullable User user, @Nullable Object item) {
        return MinestomInventoryUtils.computeItem(user, item);
    }

    @Override
    public void onMainThread(@NotNull Runnable runnable) {
        MinecraftServer.getSchedulerManager().scheduleNextProcess(runnable);
    }

    @Override
    public void setAir(int slot) {
        setItem(slot, ItemStack.AIR);
    }

    @Override
    public PagedInventoryController pagedController() {
        return pagedController;
    }

    @Override
    public void updateSlotsAtPriority(int priority, int... slots) {
        this.itemHandler.updateSlots(priority, slots);
    }

    @Override
    public void updateSlots(int... slots) {
        this.itemHandler.updateSlots(slots);
    }
}
