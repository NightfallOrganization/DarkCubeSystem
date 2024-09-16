/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.inventory;

import static eu.darkcube.system.impl.server.inventory.InventoryAPIUtils.LOGGER;
import static eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport.adventureSupport;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.server.inventory.InventoryItemHandler;
import eu.darkcube.system.impl.server.inventory.TemplateInventoryImpl;
import eu.darkcube.system.impl.server.inventory.controller.PagedInventoryControllerImpl;
import eu.darkcube.system.impl.server.inventory.listener.TemplateWrapperListener;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerView;
import eu.darkcube.system.server.inventory.container.ContainerViewConfiguration;
import eu.darkcube.system.server.inventory.controller.PagedInventoryController;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class BukkitTemplateInventory extends BukkitInventory implements TemplateInventoryImpl<ItemStack> {
    public final @NotNull Player player;
    public final @NotNull User user;
    public final @NotNull InventoryItemHandler<ItemStack, Player> itemHandler;
    public final @NotNull AtomicInteger animationsStarted = new AtomicInteger();
    public final @NotNull List<@NotNull TemplateInventoryListener> templateListeners = new CopyOnWriteArrayList<>();
    public final @NotNull Map<TemplateInventoryListener, List<InventoryListener>> templateListenerMap = new HashMap<>();
    public final @NotNull Instant openInstant;
    public final @NotNull PagedInventoryControllerImpl pagedController;
    public InventoryView inventoryView;

    public BukkitTemplateInventory(@NotNull Component title, @NotNull BukkitInventoryType type, @NotNull BukkitInventoryTemplate template, @NotNull Player player) {
        super(title, type);
        this.player = player;
        this.openInstant = Instant.now();
        for (var listener : template.listeners()) {
            if (listener instanceof TemplateWrapperListener(var handle)) {
                this.addListener(handle);
            } else {
                this.addListener(listener);
            }
        }
        createInventory();
        this.user = UserAPI.instance().user(player.getUniqueId());
        for (var i = 0; i < this.templateListeners.size(); i++) {
            try {
                this.templateListeners.get(i).onInit(this, user);
            } catch (Throwable t) {
                LOGGER.error("Error during #onInit of {}", this.templateListeners.get(i).getClass().getName(), t);
            }
        }
        this.itemHandler = InventoryItemHandler.simple(user, player, this, template);
        this.pagedController = new PagedInventoryControllerImpl(this.itemHandler);
    }

    @Override
    protected boolean delayInventoryCreation() {
        return true;
    }

    @Override
    protected @NotNull Inventory createInventory(BukkitInventoryType type, InventoryType bukkitType, Component title) {
        if (bukkitType == InventoryType.ANVIL) {
            var view = BukkitInventoryAPIUtils.utils().createAnvil(player, this, adventureSupport().convert(title));
            this.inventoryView = view;

            return view.getTopInventory();
        }
        return super.createInventory(type, bukkitType, title);
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
            if (inventoryView == null) {
                inventoryView = player.openInventory(inventory);
            } else {
                player.openInventory(inventoryView);
            }
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
    protected boolean handleCustomClickTop(InventoryClickEvent event) {
        return InventoryVersionProviderImpl.provider.handleCustomClickTop(this, event);
    }

    @Override
    protected boolean handleCustomClickBottom(InventoryClickEvent event) {
        return InventoryVersionProviderImpl.provider.handleCustomClickBottom(this, event);
    }

    @Override
    protected void handleClick(int slot, @NotNull ItemStack itemStack, @NotNull ItemBuilder item) {
        this.itemHandler.handleClick(slot, itemStack, item);
    }

    @Override
    public @NotNull Instant openInstant() {
        return this.openInstant;
    }

    @Override
    public void scheduleSetItem(int slot, @NotNull Duration duration, @NotNull ItemStack item) {
        var millis = duration.toMillis();
        animationsStarted.incrementAndGet();
        if (millis == 0) {
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
            Bukkit.getScheduler().runTaskLater(DarkCubeSystemBukkit.systemPlugin(), () -> {
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
            }, millis / 50L);
        }
    }

    @Override
    public @Nullable ItemStack computeItem(@Nullable User user, @Nullable Object item) {
        return BukkitInventoryUtils.computeItem(user, item);
    }

    @Override
    public void onMainThread(@NotNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(DarkCubeSystemBukkit.systemPlugin(), runnable);
        }
    }

    @Override
    public void setAir(int slot) {
        scheduleSetItem(slot, Duration.ZERO, ItemStack.empty());
    }

    @Override
    public void returnItemToUser(ItemBuilder item) {
        var stack = item.<ItemStack>build();
        var failed = player.getInventory().addItem(stack);
        for (var value : failed.values()) {
            player.getOpenInventory().setItem(InventoryView.OUTSIDE, value);
        }
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
