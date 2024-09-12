/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.inventory;

import static eu.darkcube.system.impl.server.inventory.InventoryAPIUtils.LOGGER;
import static eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport.adventureSupport;

import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.server.inventory.AbstractInventory;
import eu.darkcube.system.impl.server.inventory.listener.ClickDataImpl;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class BukkitInventory extends AbstractInventory<ItemStack> {
    protected final AtomicInteger openCount = new AtomicInteger(0);
    protected final Inventory inventory;
    private final Holder holder = new Holder();
    private final InventoryListener listener = new InventoryListener();
    private volatile boolean modified = false;
    private volatile BukkitTask updateScheduler;

    public BukkitInventory(@Nullable Component title, @NotNull BukkitInventoryType type) {
        super(title, type, type.size());
        this.inventory = createInventory(type, title);
    }

    private Inventory createInventory(BukkitInventoryType type, Component title) {
        if (type instanceof ChestInventoryType(var size)) {
            return Bukkit.createInventory(holder, size, adventureSupport().convert(title));
        }
        return Bukkit.createInventory(holder, type.bukkitType(), adventureSupport().convert(title));
    }

    @Override
    protected void setItem0(int slot, @NotNull ItemStack item) {
        if (!Bukkit.isPrimaryThread()) {
            LOGGER.error("Access to inventory from outside primary thread: {}", Thread.currentThread().getName(), new Exception());
        }
        inventory.setItem(slot, item);
        for (var i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).onSlotUpdate(this, slot);
            } catch (Throwable t) {
                LOGGER.error("Error during #onSlotUpdate of {}", listeners.get(i).getClass().getName(), t);
            }
        }
        modified = true;
    }

    @Override
    protected ItemStack getItem0(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public final void open(@Nullable Object player) {
        var bukkitPlayer = BukkitInventoryUtils.player(player);
        if (bukkitPlayer == null) {
            // player not online, do not open
            return;
        }
        if (openCount.getAndIncrement() == 0) {
            register();
        }
        doOpen(bukkitPlayer);
    }

    protected void register() {
        Bukkit.getPluginManager().registerEvents(this.listener, DarkCubeSystemBukkit.systemPlugin());
        updateScheduler = Bukkit.getScheduler().runTaskTimer(DarkCubeSystemBukkit.systemPlugin(), () -> {
            if (modified) {
                modified = false;
                for (var i = 0; i < listeners.size(); i++) {
                    try {
                        listeners.get(i).onUpdate(this);
                    } catch (Throwable t) {
                        LOGGER.error("Error during #onUpdate of {}", listeners.get(i).getClass().getName(), t);
                    }
                }
            }
        }, 0, 1);
    }

    protected void unregister() {
        HandlerList.unregisterAll(this.listener);
        updateScheduler.cancel();
    }

    protected void doOpen(@NotNull Player player) {
        var user = UserAPI.instance().user(player.getUniqueId());
        for (var i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).onPreOpen(this, user);
            } catch (Throwable t) {
                LOGGER.error("Error during #onPreOpen of {}", listeners.get(i).getClass().getName(), t);
            }
        }
        opened.add(user);
        player.openInventory(inventory);
        for (var i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).onOpen(this, user);
            } catch (Throwable t) {
                LOGGER.error("Error during #onOpen of {}", listeners.get(i).getClass().getName(), t);
            }
        }
    }

    @Override
    public final boolean opened(@NotNull Object player) {
        while (true) {
            if (player instanceof Player bukkitPlayer) {
                player = UserAPI.instance().user(bukkitPlayer.getUniqueId());
            } else {
                break;
            }
        }
        var user = (User) player;
        return opened.contains(user);
    }

    private void handleClick(InventoryClickEvent event) {
        var bukkitInventory = event.getClickedInventory();
        if (bukkitInventory == null) return;

        if (bukkitInventory.getHolder() instanceof Holder holder) {
            if (holder != this.holder) {
                // our inventory isn't open
                return;
            }
            // They clicked into this inventory
            handleClickTop(event);
        } else {
            // They might still try to shift items into this inventory
            // or sth else :)
            var topInventory = event.getView().getTopInventory();
            if (topInventory != this.inventory) {
                // our inventory isn't open
                return;
            }
            handleClickBottom(event);
        }
    }

    private void handleClickBottom(InventoryClickEvent event) {
        if (handleCustomClickBottom(event)) {
            return;
        }
        var clickType = event.getClick();
        var cancel = false;
        switch (clickType) {
            case LEFT, CREATIVE, CONTROL_DROP, DROP, RIGHT, WINDOW_BORDER_LEFT, WINDOW_BORDER_RIGHT, NUMBER_KEY, MIDDLE -> {
            }
            case SHIFT_LEFT, SHIFT_RIGHT, DOUBLE_CLICK, SWAP_OFFHAND -> cancel = true;
            default -> {
                LOGGER.error("Click on bottom inventory not supported by InventoryAPI: {}", clickType);
                cancel = true;
            }
        }
        if (cancel) {
            event.setCancelled(true);
        }
    }

    private void handleClickTop(InventoryClickEvent event) {
        if (handleCustomClickTop(event)) {
            return;
        }
        var clickType = event.getClick();
        event.setCancelled(true);
        var user = UserAPI.instance().user(event.getWhoClicked().getUniqueId());
        var slot = switch (clickType) {
            case LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT, DROP, CONTROL_DROP, NUMBER_KEY -> event.getSlot();
            case WINDOW_BORDER_LEFT, WINDOW_BORDER_RIGHT -> -1;
            case MIDDLE -> -1; // Middle mouse ignored
            case DOUBLE_CLICK -> 0; // TODO test this
            default -> {
                LOGGER.error("Click on top inventory not supported by InventoryAPI: {}", clickType);
                yield -1;
            }
        };
        if (slot == -1) return;
        var itemStack = event.getCurrentItem();
        var item = itemStack == null || itemStack.getType().isAir() ? ItemBuilder.item() : ItemBuilder.item(itemStack);
        var clickData = new ClickDataImpl(clickType.isRightClick(), clickType.isLeftClick(), clickType.isShiftClick());
        handleClick(slot, itemStack == null ? new ItemStack(Material.AIR) : itemStack, item);
        for (var i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).onClick(this, user, slot, item, clickData);
            } catch (Throwable t) {
                LOGGER.error("Error during #onClick of {}", listeners.get(i).getClass().getName(), t);
            }
        }
    }

    private void handleDrag(InventoryDragEvent event) {
        var view = event.getView();
        for (var slot : event.getRawSlots()) {
            var inventory = view.getInventory(slot);
            if (inventory == this.inventory) {
                event.setCancelled(true);
                break;
            }
        }
    }

    protected boolean handleCustomClickTop(InventoryClickEvent event) {
        return false;
    }

    protected boolean handleCustomClickBottom(InventoryClickEvent event) {
        return false;
    }

    protected void handleClick(int slot, @NotNull ItemStack itemStack, @NotNull ItemBuilder item) {
    }

    private void handleClose(InventoryCloseEvent event) {
        var bukkitInventory = event.getInventory();
        if (!(bukkitInventory.getHolder() instanceof Holder holder)) return;
        if (holder != this.holder) return;
        var user = UserAPI.instance().user(event.getPlayer().getUniqueId());
        for (var i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).onClose(this, user);
            } catch (Throwable t) {
                LOGGER.error("Error during #onClose of {}", listeners.get(i).getClass().getName(), t);
            }
        }
        if (openCount.decrementAndGet() == 0) {
            unregister();
        }
    }

    private class Holder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }

    private class InventoryListener implements Listener {
        @EventHandler
        public void handle(InventoryCloseEvent event) {
            handleClose(event);
        }

        @EventHandler
        public void handle(InventoryClickEvent event) {
            handleClick(event);
        }

        @EventHandler
        public void handle(InventoryDragEvent event) {
            handleDrag(event);
        }
    }
}
