package eu.darkcube.system.impl.bukkit.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.provider.InternalProvider;

public interface InventoryVersionProvider {
    @Nullable
    Object tryConvertTitle(@NotNull Object title);
}

class InventoryVersionProviderImpl {
    static final InventoryVersionProvider provider = InternalProvider.instance().instance(InventoryVersionProvider.class);
}