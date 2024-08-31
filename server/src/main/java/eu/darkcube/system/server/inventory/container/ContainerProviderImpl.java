package eu.darkcube.system.server.inventory.container;

import eu.darkcube.system.provider.InternalProvider;

class ContainerProviderImpl {
    static final ContainerProvider PROVIDER = InternalProvider.instance().instance(ContainerProvider.class);
}
