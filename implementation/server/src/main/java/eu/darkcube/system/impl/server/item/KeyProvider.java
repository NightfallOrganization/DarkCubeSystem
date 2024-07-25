package eu.darkcube.system.impl.server.item;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.provider.InternalProvider;

public interface KeyProvider {
    Key of(Object key);

    static Key get(Object key) {
        return KeyProviderImpl.provider.of(key);
    }
}

class KeyProviderImpl {
    static final KeyProvider provider = InternalProvider.instance().instance(KeyProvider.class);
}