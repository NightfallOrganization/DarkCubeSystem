/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import static eu.darkcube.system.impl.server.inventory.InventoryAPIUtils.LOGGER;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.system.impl.server.inventory.InventoryItemHandler;
import eu.darkcube.system.impl.server.inventory.TemplateInventoryImpl;
import eu.darkcube.system.impl.server.inventory.controller.PagedInventoryControllerImpl;
import eu.darkcube.system.impl.server.inventory.listener.TemplateWrapperListener;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerView;
import eu.darkcube.system.server.inventory.container.ContainerViewConfiguration;
import eu.darkcube.system.server.inventory.controller.PagedInventoryController;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;

public class MinestomTemplateInventory extends MinestomInventory implements TemplateInventoryImpl<ItemStack> {
    private final @NotNull Player player;
    private final @NotNull User user;
    private final @NotNull InventoryItemHandler<ItemStack, Player> itemHandler;
    private final @NotNull AtomicInteger animationsStarted = new AtomicInteger();
    private final @NotNull List<@NotNull TemplateInventoryListener> templateListeners = new CopyOnWriteArrayList<>();
    private final @NotNull Map<TemplateInventoryListener, List<InventoryListener>> templateListenerMap = new HashMap<>();
    private final @NotNull Instant openInstant;
    private final @NotNull PagedInventoryControllerImpl pagedController;

    public MinestomTemplateInventory(@NotNull Component title, @NotNull MinestomInventoryType type, @NotNull MinestomInventoryTemplate template, @NotNull Player player) {
        super(title, type);
        this.player = player;
        for (var listener : template.listeners()) {
            if (listener instanceof TemplateWrapperListener(var handle)) {
                this.addListener(handle);
            } else {
                this.addListener(listener);
            }
        }
        this.user = UserAPI.instance().user(player.getUuid());
        this.itemHandler = InventoryItemHandler.simple(user, player, this, template);
        this.openInstant = Instant.now(); // This inventory gets opened right after creation
        this.pagedController = new PagedInventoryControllerImpl(this.itemHandler);
        for (var i = 0; i < this.templateListeners.size(); i++) {
            try {
                this.templateListeners.get(i).onInit(this, user);
            } catch (Throwable t) {
                LOGGER.error("Error during #onInit of {}", this.templateListeners.get(i).getClass().getName(), t);
            }
        }
    }

    @Override
    protected void doOpen(@NotNull Player player) {
        if (player != this.player) {
            // Can't open the inventory for someone else than the original player
            return;
        }
        this.itemHandler.doOpen();
        for (var i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).onPreOpen(this, user);
            } catch (Throwable t) {
                LOGGER.error("Error during #onPreOpen of {}", listeners.get(i).getClass().getName(), t);
            }
        }
        opened.add(user);
        onMainThread(() -> {
            player.openInventory(inventory);
            for (var i = 0; i < listeners.size(); i++) {
                try {
                    listeners.get(i).onOpen(this, user);
                } catch (Throwable t) {
                    LOGGER.error("Error during #onOpen of {}", listeners.get(i).getClass().getName(), t);
                }
            }
        });
    }

    @Override
    protected void unregister() {
        super.unregister();
        this.itemHandler.doClose();
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

        animationsStarted.incrementAndGet();
        if (millis == 0) { // immediate
            setItem(slot, item);
            if (animationsStarted.decrementAndGet() == 0) {
                for (var i = 0; i < templateListeners.size(); i++) {
                    try {
                        templateListeners.get(i).onOpenAnimationFinished(this);
                    } catch (Throwable t) {
                        LOGGER.error("Error during #onOpenAnimationFinished of {}", templateListeners.get(i).getClass().getName(), t);
                    }
                }
            }
        } else {
            MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                setItem(slot, item);
                if (animationsStarted.decrementAndGet() == 0) {
                    for (var i = 0; i < templateListeners.size(); i++) {
                        try {
                            templateListeners.get(i).onOpenAnimationFinished(this);
                        } catch (Throwable t) {
                            LOGGER.error("Error during #onOpenAnimationFinished of {}", templateListeners.get(i).getClass().getName(), t);
                        }
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
    public void returnItemToUser(ItemBuilder item) {
        player.dropItem(item.build());
    }

    @Override
    public @NotNull PagedInventoryController pagedController() {
        return pagedController;
    }

    @Override
    public void updateSlotsAtPriority(int priority, int @NotNull ... slots) {
        this.itemHandler.updateSlots(priority, slots);
    }

    @Override
    public void updateSlots(int @NotNull ... slots) {
        this.itemHandler.updateSlots(slots);
    }

    @Override
    public @NotNull ContainerView addContainer(int priority, @NotNull Container container, @NotNull ContainerViewConfiguration configuration) {
        return this.itemHandler.addContainer(priority, container, configuration);
    }

    @Override
    public void removeContainer(@NotNull ContainerView view) {
        this.itemHandler.removeContainer(view);
    }

    @Override
    public void addListener(@NotNull InventoryListener listener) {
        if (listener instanceof TemplateWrapperListener(var handle)) {
            this.templateListeners.add(handle);
        }
        super.addListener(listener);
    }

    @Override
    public void removeListener(@NotNull InventoryListener listener) {
        if (listener instanceof TemplateWrapperListener(var handle)) {
            this.templateListeners.remove(handle);
        }
        super.removeListener(listener);
    }

    @Override
    public void addListener(@NotNull TemplateInventoryListener listener) {
        var list = this.templateListenerMap.computeIfAbsent(listener, _ -> new ArrayList<>(1));
        var wrapper = new TemplateWrapperListener(listener);
        list.add(wrapper);
        addListener(wrapper);
    }

    @Override
    public void removeListener(@NotNull TemplateInventoryListener listener) {
        var wrappers = this.templateListenerMap.get(listener);
        if (wrappers == null) return;
        var wrapper = wrappers.removeLast();
        removeListener(wrapper);
        if (wrappers.isEmpty()) {
            this.templateListenerMap.remove(listener);
        }
    }

    @Override
    public @NotNull List<@NotNull TemplateInventoryListener> templateListeners() {
        return templateListeners;
    }
}
