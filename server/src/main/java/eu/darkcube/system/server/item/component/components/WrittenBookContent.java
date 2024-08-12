/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.component.components.util.FilteredText;

public record WrittenBookContent(@NotNull List<FilteredText<Component>> pages, @NotNull FilteredText<String> title, @NotNull String author, int generation, boolean resolved) {
    public WrittenBookContent {
        pages = List.copyOf(pages);
    }
}
