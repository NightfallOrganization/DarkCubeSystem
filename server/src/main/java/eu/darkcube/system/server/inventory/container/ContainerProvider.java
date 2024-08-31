package eu.darkcube.system.server.inventory.container;

import java.util.function.Supplier;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.TemplateInventory;

public interface ContainerProvider {
    static @NotNull ContainerView createView(@NotNull TemplateInventory inventory, @NotNull Container container, int priority) {
        return ContainerProviderImpl.PROVIDER.newView(inventory, container, priority);
    }

    @NotNull
    Container simple(int size);

    @NotNull
    ContainerView newView(@NotNull TemplateInventory inventory, @NotNull Container container, int priority);

    @NotNull
    ContainerViewFactory factory(int priority, @NotNull Supplier<Container> containerSupplier, @NotNull ContainerViewConfiguration configuration);
}
