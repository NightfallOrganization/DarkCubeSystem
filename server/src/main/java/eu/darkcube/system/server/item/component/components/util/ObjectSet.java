/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components.util;

import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public sealed interface ObjectSet {

    static ObjectSet empty() {
        return new Empty();
    }

    static ObjectSet tag(@NotNull Key tag) {
        return new Tag(tag);
    }

    static ObjectSet entries(@NotNull List<Key> entries) {
        return new Entries(entries);
    }

    record Empty() implements ObjectSet {
    }

    record Tag(@NotNull Key tag) implements ObjectSet {
    }

    record Entries(@NotNull @Unmodifiable List<Key> entries) implements ObjectSet {
        public Entries {
            entries = List.copyOf(entries);
        }
    }
}
