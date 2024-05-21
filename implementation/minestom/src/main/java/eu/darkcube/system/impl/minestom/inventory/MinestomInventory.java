/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

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
import net.minestom.server.inventory.click.Click.Info.Left;
import net.minestom.server.inventory.click.Click.Info.Right;
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
        this.node.addListener(InventoryClickEvent.class, this::handleClick);
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
        var user = UserAPI.instance().user(event.getPlayer().getUuid());
        var slot = switch (event.getClickInfo()) {
            case Left left -> left.slot();
            case Right right -> right.slot();
            default -> -1;
        };
        var itemStack = itemStack(slot, event.getInventory(), event.getPlayerInventory());
        var item = itemStack.isAir() ? ItemBuilder.item() : ItemBuilder.item(itemStack);
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onClick(this, user, slot, item);
        }
        if (openCount.decrementAndGet() == 0) {
            unregister();
        }
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
    }

    private static ItemStack itemStack(int slot, Inventory clickedInventory, Inventory playerInventory) {
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
