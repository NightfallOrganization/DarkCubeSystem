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

import eu.darkcube.system.impl.minestom.adventure.AdventureUtils;
import eu.darkcube.system.impl.server.inventory.AbstractInventory;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.ContainerInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.Click.Info;
import net.minestom.server.inventory.click.Click.Info.DropSlot;
import net.minestom.server.inventory.click.Click.Info.HotbarSwap;
import net.minestom.server.inventory.click.Click.Info.Left;
import net.minestom.server.inventory.click.Click.Info.LeftShift;
import net.minestom.server.inventory.click.Click.Info.Right;
import net.minestom.server.inventory.click.Click.Info.RightShift;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.inventory.PlayerInventoryUtils;

public class MinestomInventory extends AbstractInventory<ItemStack> {
    protected final AtomicInteger openCount = new AtomicInteger(0);
    protected final Inventory inventory;
    protected final EventNode<Event> node = EventNode.all("inventory");
    private volatile boolean modified = false;
    private volatile net.minestom.server.timer.Task updateScheduler;

    public MinestomInventory(@Nullable Component title, @NotNull MinestomInventoryType type) {
        super(title, type, type.minestomType().getSize());
        this.inventory = new ServerInventory(type.minestomType(), AdventureUtils.convert(title), this);
        this.node.addListener(InventoryCloseEvent.class, this::handleClose);
        this.node.addListener(builder(InventoryClickEvent.class).ignoreCancelled(false).handler(this::handleClick).build());
    }

    @Override
    protected final void setItem0(int slot, @NotNull ItemStack item) {
        inventory.setItemStack(slot, item);
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onSlotUpdate(this, slot);
        }
        modified = true;
    }

    @Override
    protected final ItemStack getItem0(int slot) {
        return inventory.getItemStack(slot);
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
                    listeners.get(i).onUpdate(this);
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
            listeners.get(i).onPreOpen(this, user);
        }
        opened.add(user);
        player.openInventory(inventory);
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onOpen(this, user);
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

    private void handleClick(InventoryClickEvent event) {
        var minestomInventory = event.getInventory();
        if (!(minestomInventory instanceof ServerInventory serverInventory)) return;
        var inventory = serverInventory.inventory;
        if (inventory != this) return;
        event.setCancelled(true);
        var user = UserAPI.instance().user(event.getPlayer().getUuid());
        var info = event.getClickInfo();
        LOGGER.debug("Clicked inventory with info: {}", info);
        var slot = switch (info) {
            case Left left -> left.slot();
            case Right right -> right.slot();
            case LeftShift(var s) -> s;
            case RightShift(var s) -> s;
            case Info.Double(var s) -> s;
            case HotbarSwap(var ignored, var s) -> s;
            case DropSlot(var s, var ignored) -> s;
            default -> {
                LOGGER.error("Click not supported by InventoryAPI: {}", event.getClickInfo());
                yield -1;
            }
        };
        if (info instanceof Info.Double)
            // Do not handle double clicks, they are also sent as a Left click, so they are duplicate.
            return;

        var itemStack = itemStack(slot, event.getInventory(), event.getPlayerInventory());
        var item = itemStack.isAir() ? ItemBuilder.item() : ItemBuilder.item(itemStack);
        handleClick(slot, itemStack, item);
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onClick(this, user, slot, item);
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
            listeners.get(i).onClose(this, user);
        }
        if (openCount.decrementAndGet() == 0) {
            unregister();
        }
    }

    private static ItemStack itemStack(int slot, Inventory clickedInventory, Inventory playerInventory) {
        if (slot < 0) {
            return ItemStack.AIR;
        }
        if (slot >= clickedInventory.getSize()) {
            var converted = PlayerInventoryUtils.protocolToMinestom(slot, clickedInventory.getSize());
            return playerInventory.getItemStack(converted);
        } else {
            return clickedInventory.getItemStack(slot);
        }
    }

    public static class ServerInventory extends ContainerInventory {

        private final MinestomInventory inventory;

        public ServerInventory(@NotNull InventoryType inventoryType, @NotNull net.kyori.adventure.text.Component title, MinestomInventory inventory) {
            super(inventoryType, title);
            this.inventory = inventory;
        }
    }
}
