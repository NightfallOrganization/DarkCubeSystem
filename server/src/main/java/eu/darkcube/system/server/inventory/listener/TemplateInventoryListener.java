package eu.darkcube.system.server.inventory.listener;

import java.util.function.Supplier;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.container.ContainerView;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public interface TemplateInventoryListener {
    static @NotNull TemplateInventoryListener ofStateful(@NotNull Supplier<@NotNull TemplateInventoryListener> listener) {
        return InventoryListenerProviderImpl.listenerProvider().ofStatefulTemplate(listener);
    }

    default void onInit(@NotNull TemplateInventory inventory, @NotNull User user) {
    }

    /**
     * Called before a player has opened an inventory
     *
     * @param inventory the inventory
     * @param user      the player
     */
    default void onPreOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
    }

    /**
     * Called after a player has opened an inventory
     *
     * @param inventory the inventory
     * @param user      the player
     */
    default void onOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
    }

    /**
     * Called once all items in an inventory are visible.
     * This does not imply that items won't change as a result of async computation or other operations.
     * This is called every time all animations have finished. Can be called multiple times for the same inventory.
     *
     * @param inventory the inventory
     */
    default void onOpenAnimationFinished(@NotNull TemplateInventory inventory) {
    }

    /**
     * Called everytime the inventory is updated with new content
     * (max once per tick)
     *
     * @param inventory the inventory
     */
    default void onUpdate(@NotNull TemplateInventory inventory) {
    }

    /**
     * Called every time a slot is updated with a new item.
     * To get the item, use {@link TemplateInventory#getItem(int)}.
     *
     * @param inventory the inventory
     * @param slot      the slot
     */
    default void onSlotUpdate(@NotNull TemplateInventory inventory, int slot) {
    }

    /**
     * Called after a player has closed an inventory
     *
     * @param inventory the inventory that was closed
     * @param user      the user that closed the inventory
     */
    default void onClose(@NotNull TemplateInventory inventory, @NotNull User user) {
    }

    /**
     * Called when an item in an inventory is clicked.
     * Inventories are unmodifiable, so click events are cancelled.
     *
     * @param inventory the affected inventory
     * @param user      the user that clicked
     * @param slot      the slot the item is in
     * @param item      the item
     */
    default void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
    }

    default void onContainerAdd(@NotNull TemplateInventory inventory, @NotNull User user, @NotNull ContainerView containerView) {
    }

    default void onContainerRemove(@NotNull TemplateInventory inventory, @NotNull User user, @NotNull ContainerView containerView) {
    }
}
