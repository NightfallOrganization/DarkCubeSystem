package eu.darkcube.system.server.inventory.container;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;

import static eu.darkcube.system.server.inventory.container.ContainerProviderImpl.PROVIDER;

public interface Container {
    @Nullable
    ItemBuilder getAt(int slot);

    void setAt(int slot, @Nullable ItemBuilder item);

    void addListener(@NotNull ContainerListener listener);

    void removeListener(@NotNull ContainerListener listener);

    int size();

    static @NotNull Container simple(int size) {
        return PROVIDER.simple(size);
    }
}
