/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component;

import eu.darkcube.system.server.data.component.DataComponent;

@Deprecated
public interface LegacyItemComponent {
    DataComponent<String> SPAWNER_ENTITY_DATA = ItemComponentImpl.register("spawner_entity_data");
}