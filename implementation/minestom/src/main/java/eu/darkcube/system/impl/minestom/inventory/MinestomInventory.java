/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import static eu.darkcube.system.impl.server.inventory.InventoryAPIUtils.LOGGER;
import static net.minestom.server.event.EventListener.builder;

import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.system.impl.server.inventory.AbstractInventory;
import eu.darkcube.system.impl.server.inventory.InventoryCapabilitiesImpl;
import eu.darkcube.system.impl.server.inventory.listener.ClickDataImpl;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport;
import eu.darkcube.system.server.inventory.InventoryCapabilities;
import eu.darkcube.system.server.inventory.listener.ClickData;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;

public class MinestomInventory extends AbstractInventory<ItemStack> {
    protected final AtomicInteger openCount = new AtomicInteger(0);
    protected final Inventory inventory;
    protected final EventNode<Event> node = EventNode.all("inventory");
    private volatile boolean modified = false;
    private volatile net.minestom.server.timer.Task updateScheduler;

    public MinestomInventory(@NotNull Component title, @NotNull MinestomInventoryType type) {
        super(title, type, type.minestomType().getSize());
        this.inventory = new ServerInventory(type.minestomType(), MinestomAdventureSupport.adventureSupport().convert(title), this);
        this.node.addListener(InventoryCloseEvent.class, this::handleClose);
        this.node.addListener(builder(InventoryPreClickEvent.class).ignoreCancelled(false).handler(this::handleClick).build());
    }

    @Override
    protected final void setItem0(int slot, @NotNull ItemStack item) {
        inventory.setItemStack(slot, item);
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
    protected final ItemStack getItem0(int slot) {
        return inventory.getItemStack(slot);
    }

    @Override
    public @NotNull InventoryCapabilities capabilities() {
        return InventoryCapabilitiesImpl.NO_CAPABILITIES;
    }

    @Override
    public final void open(@Nullable Object player) {
        var minestomPlayer = MinestomInventoryUtils.player(player);
        if (minestomPlayer == null) {
            // player not online, do not open
            return;
        }
        if (openCount.getAndIncrement() == 0) {
            register();
        }
        doOpen(minestomPlayer);
    }

    protected void register() {
        MinecraftServer.getGlobalEventHandler().addChild(node);
        updateScheduler = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
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
        }, TaskSchedule.immediate(), TaskSchedule.nextTick(), ExecutionType.TICK_END);
    }

    protected void unregister() {
        MinecraftServer.getGlobalEventHandler().removeChild(node);
        updateScheduler.cancel();
    }

    protected void doOpen(@NotNull Player player) {
        var user = UserAPI.instance().user(player.getUuid());
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
            if (player instanceof Player minestomPlayer) {
                player = UserAPI.instance().user(minestomPlayer.getUuid());
            } else {
                break;
            }
        }
        var user = (User) player;
        return opened.contains(user);
    }

    private void handleClick(InventoryPreClickEvent event) {
        var minestomInventory = event.getInventory();
        if (!(minestomInventory instanceof ServerInventory serverInventory)) return;
        var inventory = serverInventory.inventory;
        if (inventory != this) return;
        event.setCancelled(true);
        var user = UserAPI.instance().user(event.getPlayer().getUuid());
        var clickType = event.getClickType();
        LOGGER.debug("Clicked inventory with info: {}", clickType);
        ClickData clickData;
        var slot = switch (clickType) {
            case LEFT_CLICK -> {
                clickData = new ClickDataImpl(false, true, false);
                yield event.getSlot();
            }
            case START_SHIFT_CLICK -> {
                clickData = new ClickDataImpl(false, true, true);
                yield event.getSlot();
            }
            case RIGHT_CLICK -> {
                clickData = new ClickDataImpl(true, false, false);
                yield event.getSlot();
            }
            case START_DOUBLE_CLICK, DROP, CHANGE_HELD -> {
                clickData = new ClickDataImpl(false, false, false);
                yield event.getSlot();
            }
            default -> {
                clickData = new ClickDataImpl(false, false, false);
                LOGGER.error("Click not supported by InventoryAPI: {}", clickType);
                yield -1;
            }
        };
        if (clickType == ClickType.START_DOUBLE_CLICK)
            // Do not handle double clicks, they are also sent as a Left click, so they are duplicate.
            return;

        // var itemStack = itemStack(slot, event.getInventory(), event.getPlayerInventory());
        var itemStack = event.getClickedItem();
        var item = itemStack.isAir() ? ItemBuilder.item() : ItemBuilder.item(itemStack);
        handleClick(slot, itemStack, item);
        for (var i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).onClick(this, user, slot, item, clickData);
            } catch (Throwable t) {
                LOGGER.error("Error during #onClick of {}", listeners.get(i).getClass().getName(), t);
            }
        }
    }

    protected void handleClick(int slot, @NotNull ItemStack itemStack, @NotNull ItemBuilder item) {
    }

    private void handleClose(InventoryCloseEvent event) {
        var minestomInventory = event.getInventory();
        if (!(minestomInventory instanceof ServerInventory serverInventory)) return;
        var inventory = serverInventory.inventory;
        if (inventory != this) return;
        var user = UserAPI.instance().user(event.getPlayer().getUuid());
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

    // private static ItemStack itemStack(int slot, Inventory clickedInventory, Inventory playerInventory) {
    //     if (slot < 0) {
    //         return ItemStack.AIR;
    //     }
    //     if (slot >= clickedInventory.getSize()) {
    //         var converted = PlayerInventoryUtils.protocolToMinestom(slot, clickedInventory.getSize());
    //         return playerInventory.getItemStack(converted);
    //     } else {
    //         return clickedInventory.getItemStack(slot);
    //     }
    // }

    public static class ServerInventory extends Inventory {

        private final MinestomInventory inventory;

        public ServerInventory(@NotNull InventoryType inventoryType, @NotNull net.kyori.adventure.text.Component title, MinestomInventory inventory) {
            super(inventoryType, title);
            this.inventory = inventory;
        }
    }
}
