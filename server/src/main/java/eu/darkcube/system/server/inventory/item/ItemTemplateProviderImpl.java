/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.item;

import eu.cloudnetservice.driver.inject.InjectionLayer;

class ItemTemplateProviderImpl {
    private static final ItemTemplateProvider provider = InjectionLayer.ext().instance(ItemTemplateProvider.class);

    public static ItemTemplateProvider itemTemplateProvider() {
        return provider;
    }
}
