/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.item.ItemTemplateProvider;

public class ItemTemplateProviderImpl implements ItemTemplateProvider {
    @Override
    public @NotNull ItemTemplateImpl create() {
        return new ItemTemplateImpl();
    }
}
