package eu.darkcube.system.impl.server.inventory.listener;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public class TemplateWrapperListener implements InventoryListener {
    private final TemplateInventoryListener listener;

    public TemplateWrapperListener(TemplateInventoryListener listener) {
        this.listener = listener;
    }

    @Override
    public void onPreOpen(@NotNull Inventory inventory, @NotNull User user) {
        listener.onPreOpen((TemplateInventory) inventory, user);
    }

    @Override
    public void onOpen(@NotNull Inventory inventory, @NotNull User user) {
        listener.onOpen((TemplateInventory) inventory, user);
    }

    @Override
    public void onOpenAnimationFinished(@NotNull Inventory inventory) {
        listener.onOpenAnimationFinished((TemplateInventory) inventory);
    }

    @Override
    public void onUpdate(@NotNull Inventory inventory) {
        listener.onUpdate((TemplateInventory) inventory);
    }

    @Override
    public void onSlotUpdate(@NotNull Inventory inventory, int slot) {
        listener.onSlotUpdate((TemplateInventory) inventory, slot);
    }

    @Override
    public void onClose(@NotNull Inventory inventory, @NotNull User user) {
        listener.onClose((TemplateInventory) inventory, user);
    }

    @Override
    public void onClick(@NotNull Inventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        listener.onClick((TemplateInventory) inventory, user, slot, item);
    }
}