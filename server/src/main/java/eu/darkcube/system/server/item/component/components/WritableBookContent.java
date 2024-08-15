/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.component.components.util.FilteredText;

public record WritableBookContent(@NotNull List<FilteredText<String>> pages) {
    public WritableBookContent {
        pages = List.copyOf(pages);
    }
}
