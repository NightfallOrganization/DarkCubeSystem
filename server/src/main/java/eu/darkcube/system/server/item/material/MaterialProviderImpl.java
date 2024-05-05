/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.material;

import eu.cloudnetservice.driver.inject.InjectionLayer;

class MaterialProviderImpl {
    private static final MaterialProvider provider = InjectionLayer.ext().instance(MaterialProvider.class);

    static Material of(Object platformMaterial) {
        return provider.of(platformMaterial);
    }

    static Material spawner() throws UnsupportedOperationException {
        return provider.spawner();
    }

    static Material air() {
        return provider.air();
    }
}
