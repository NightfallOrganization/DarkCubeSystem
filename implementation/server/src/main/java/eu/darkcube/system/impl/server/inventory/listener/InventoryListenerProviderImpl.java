/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.container.ContainerView;
import eu.darkcube.system.server.inventory.listener.ClickData;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.inventory.listener.InventoryListenerProvider;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public class InventoryListenerProviderImpl implements InventoryListenerProvider {
    @Override
    public @NotNull InventoryListener ofStateful(@NotNull Supplier<@NotNull InventoryListener> supplier) {
        return new InventoryListener() {
            private final Map<Inventory, InventoryListener> listeners = new ConcurrentHashMap<>();

            @Override
            public void onPreOpen(@NotNull Inventory inventory, @NotNull User user) {
                var listener = supplier.get();
                listeners.put(inventory, listener);
                listener.onPreOpen(inventory, user);
            }

            @Override
            public void onClose(@NotNull Inventory inventory, @NotNull User user) {
                var listener = listeners.remove(inventory);
                if (listener == null) throw new NullPointerException("Failed to find stateful listener");
                listener.onClose(inventory, user);
            }

            @Override
            public void onOpen(@NotNull Inventory inventory, @NotNull User user) {
                var listener = listeners.get(inventory);
                if (listener == null) throw new NullPointerException("Failed to find stateful listener");
                listener.onOpen(inventory, user);
            }

            @Override
            public void onUpdate(@NotNull Inventory inventory) {
                var listener = listeners.get(inventory);
                if (listener == null) return;
                listener.onUpdate(inventory);
            }

            @Override
            public void onSlotUpdate(@NotNull Inventory inventory, int slot) {
                var listener = listeners.get(inventory);
                if (listener == null) return;
                listener.onSlotUpdate(inventory, slot);
            }

            @Override
            public void onClick(@NotNull Inventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                var listener = listeners.get(inventory);
                if (listener == null) throw new NullPointerException("Failed to find stateful listener");
                listener.onClick(inventory, user, slot, item, clickData);
            }
        };
    }

    @Override
    public @NotNull TemplateInventoryListener ofStatefulTemplate(@NotNull Supplier<@NotNull TemplateInventoryListener> supplier) {
        return new TemplateInventoryListener() {
            private final Map<Inventory, TemplateInventoryListener> listeners = new ConcurrentHashMap<>();

            @Override
            public void onInit(@NotNull TemplateInventory inventory, @NotNull User user) {
                var listener = supplier.get();
                listeners.put(inventory, listener);
                listener.onInit(inventory, user);
            }

            @Override
            public void onPreOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
                var listener = listeners.get(inventory);
                if (listener == null) throw new NullPointerException("Failed to find stateful listener");
                listener.onPreOpen(inventory, user);
            }

            @Override
            public void onClose(@NotNull TemplateInventory inventory, @NotNull User user) {
                var listener = listeners.remove(inventory);
                if (listener == null) throw new NullPointerException("Failed to find stateful listener");
                listener.onClose(inventory, user);
            }

            @Override
            public void onOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
                var listener = listeners.get(inventory);
                if (listener == null) throw new NullPointerException("Failed to find stateful listener");
                listener.onOpen(inventory, user);
            }

            @Override
            public void onOpenAnimationFinished(@NotNull TemplateInventory inventory) {
                var listener = listeners.get(inventory);
                if (listener == null) return;
                listener.onOpenAnimationFinished(inventory);
            }

            @Override
            public void onUpdate(@NotNull TemplateInventory inventory) {
                var listener = listeners.get(inventory);
                if (listener == null) return;
                listener.onUpdate(inventory);
            }

            @Override
            public void onSlotUpdate(@NotNull TemplateInventory inventory, int slot) {
                var listener = listeners.get(inventory);
                if (listener == null) return;
                listener.onSlotUpdate(inventory, slot);
            }

            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                var listener = listeners.get(inventory);
                if (listener == null) throw new NullPointerException("Failed to find stateful listener");
                listener.onClick(inventory, user, slot, item, clickData);
            }

            @Override
            public void onContainerAdd(@NotNull TemplateInventory inventory, @NotNull User user, @NotNull ContainerView containerView) {
                var listener = listeners.get(inventory);
                if (listener == null) throw new NullPointerException("Failed to find stateful listener");
                listener.onContainerAdd(inventory, user, containerView);
            }

            @Override
            public void onContainerRemove(@NotNull TemplateInventory inventory, @NotNull User user, @NotNull ContainerView containerView) {
                var listener = listeners.get(inventory);
                if (listener == null) throw new NullPointerException("Failed to find stateful listener");
                listener.onContainerRemove(inventory, user, containerView);
            }
        };
    }
}
