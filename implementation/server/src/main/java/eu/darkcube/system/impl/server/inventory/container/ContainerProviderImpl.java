package eu.darkcube.system.impl.server.inventory.container;

import java.util.function.Supplier;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerProvider;
import eu.darkcube.system.server.inventory.container.ContainerView;
import eu.darkcube.system.server.inventory.container.ContainerViewConfiguration;
import eu.darkcube.system.server.inventory.container.ContainerViewFactory;
import eu.darkcube.system.userapi.User;

public class ContainerProviderImpl implements ContainerProvider {
    @Override
    public @NotNull Container simple(int size) {
        return new SimpleContainer(size);
    }

    @Override
    public @NotNull ContainerView newView(@NotNull TemplateInventory inventory, @NotNull Container container, int priority) {
        return new SimpleContainerView(inventory, container, priority);
    }

    @Override
    public @NotNull ContainerViewFactory factory(int priority, @NotNull Supplier<Container> containerSupplier, @NotNull ContainerViewConfiguration configuration) {
        return new ContainerViewFactory() {
            @Override
            public @NotNull Container container() {
                return containerSupplier.get();
            }

            @Override
            public int priority() {
                return priority;
            }

            @Override
            public void configureView(@NotNull User user, @NotNull ContainerView view) {
                configuration.configureView(user, view);
            }
        };
    }
}
