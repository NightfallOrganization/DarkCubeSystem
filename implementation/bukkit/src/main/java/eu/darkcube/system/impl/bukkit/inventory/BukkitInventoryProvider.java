package eu.darkcube.system.impl.bukkit.inventory;

import java.util.function.Supplier;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.impl.server.inventory.LazyInventoryTemplate;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryProvider;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.PreparedInventory;

public class BukkitInventoryProvider implements InventoryProvider {
    @Override
    public @NotNull InventoryTemplate createTemplate(@NotNull Key key, @NotNull InventoryType inventoryType) {
        return new BukkitInventoryTemplate(key, (BukkitInventoryType) inventoryType);
    }

    @Override
    public @NotNull InventoryTemplate createChestTemplate(@NotNull Key key, int size) {
        return createTemplate(key, type(size));
    }

    @Override
    public @NotNull PreparedInventory prepare(@NotNull InventoryType inventoryType, @NotNull Component title) {
        return new BukkitPreparedInventory(title, inventoryType);
    }

    @Override
    public @NotNull PreparedInventory prepareChest(int size, @NotNull Component title) {
        return prepare(type(size), title);
    }

    @Override
    public @NotNull InventoryTemplate lazy(@NotNull Supplier<@NotNull InventoryTemplate> supplier) {
        return new LazyInventoryTemplate(supplier);
    }

    private @NotNull InventoryType type(int size) {
        return switch (size) {
            case 9 -> new ChestInventoryType(9);
            case 18 -> new ChestInventoryType(18);
            case 27 -> new ChestInventoryType(27);
            case 36 -> new ChestInventoryType(36);
            case 45 -> new ChestInventoryType(45);
            case 54 -> new ChestInventoryType(54);
            default -> throw new IllegalArgumentException("Invalid size: " + size);
        };
    }
}
