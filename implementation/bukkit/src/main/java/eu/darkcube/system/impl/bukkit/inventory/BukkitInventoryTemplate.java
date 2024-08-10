package eu.darkcube.system.impl.bukkit.inventory;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.impl.server.inventory.InventoryTemplateImpl;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;

public class BukkitInventoryTemplate extends InventoryTemplateImpl<Player> {
    public BukkitInventoryTemplate(@NotNull Key key, @NotNull BukkitInventoryType type) {
        super(key, type, type.size());
    }

    @Override
    protected @NotNull User user(@NotNull Player player) {
        return UserAPI.instance().user(player.getUniqueId());
    }

    @Override
    protected @Nullable Player onlinePlayer(@NotNull Object player) {
        return BukkitInventoryUtils.player(player);
    }

    @Override
    protected @NotNull Inventory open(@Nullable Component title, @NotNull Player player) {
        var inventory = new BukkitTemplateInventory(title != null ? title : Component.empty(), (BukkitInventoryType) type, this, player);
        inventory.open(player);
        return inventory;
    }

    @Override
    protected @Nullable Object tryConvertTitle(@NotNull Object title) {
        return InventoryVersionProviderImpl.provider.tryConvertTitle(title);
    }
}
