package eu.darkcube.system.server.inventory.listener;

import eu.darkcube.system.provider.InternalProvider;

class InventoryListenerProviderImpl {
    private static final InventoryListenerProvider provider = InternalProvider.instance().instance(InventoryListenerProvider.class);

    public static InventoryListenerProvider listenerProvider() {
        return provider;
    }
}
