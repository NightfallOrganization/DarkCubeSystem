/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.item.component;

import eu.darkcube.system.impl.server.data.component.DataComponentImpl;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.data.component.DataComponent;
import eu.darkcube.system.server.item.component.ItemComponentProvider;

public class ItemComponentProviderImpl implements ItemComponentProvider {
    @Override
    public <T> DataComponent<T> create(String id) {
        return new DataComponentImpl<>(Key.key(id));
    }
}
