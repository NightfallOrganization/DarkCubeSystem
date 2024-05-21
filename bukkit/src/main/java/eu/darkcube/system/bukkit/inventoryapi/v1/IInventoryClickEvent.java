/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.inventoryapi.v1;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class IInventoryClickEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final InventoryClickEvent bukkitEvent;
    private final User user;
    private final ItemBuilder builder;
    private final IInventory inventory;

    public IInventoryClickEvent(InventoryClickEvent bukkitEvent, IInventory inventory) {
        this.bukkitEvent = bukkitEvent;
        this.inventory = inventory;
        this.user = UserAPI.instance().user(bukkitEvent.getWhoClicked().getUniqueId());
        this.builder = bukkitEvent.getCurrentItem() == null ? null : ItemBuilder.item(bukkitEvent.getCurrentItem());
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return bukkitEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        bukkitEvent.setCancelled(cancel);
    }

    public IInventory inventory() {
        return inventory;
    }

    public InventoryClickEvent bukkitEvent() {
        return bukkitEvent;
    }

    public User user() {
        return user;
    }

    public ItemBuilder item() {
        return builder;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
