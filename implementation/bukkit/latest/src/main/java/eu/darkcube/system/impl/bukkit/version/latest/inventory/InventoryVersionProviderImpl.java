package eu.darkcube.system.impl.bukkit.version.latest.inventory;

import eu.darkcube.system.impl.bukkit.inventory.InventoryVersionProvider;
import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.kyori.adventure.text.Component;

public class InventoryVersionProviderImpl implements InventoryVersionProvider {
    @Override
    public @Nullable Object tryConvertTitle(@NotNull Object title) {
        if (title instanceof Component component) {
            return KyoriAdventureSupport.adventureSupport().convert(component);
        }
        return null;
    }
}
