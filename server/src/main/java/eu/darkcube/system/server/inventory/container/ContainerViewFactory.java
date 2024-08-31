package eu.darkcube.system.server.inventory.container;

import static eu.darkcube.system.server.inventory.container.ContainerProviderImpl.PROVIDER;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface ContainerViewFactory extends ContainerViewConfiguration {
    static @NotNull ContainerViewFactory simple(int priority, int size) {
        return PROVIDER.factory(priority, () -> Container.simple(size), ContainerViewConfiguration.EMPTY);
    }

    static @NotNull ContainerViewFactory shared(int priority, @NotNull Container container) {
        return shared(priority, container, ContainerViewConfiguration.EMPTY);
    }

    static @NotNull ContainerViewFactory shared(int priority, @NotNull Container container, @NotNull ContainerViewConfiguration configuration) {
        return PROVIDER.factory(priority, () -> container, configuration);
    }

    @NotNull
    Container container();

    int priority();
}
