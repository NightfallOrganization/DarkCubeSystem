/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;
import java.util.UUID;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.component.components.util.PlayerSkin;

public record HeadProfile(@Nullable String name, @Nullable UUID uuid, @NotNull List<Property> properties) {
    public HeadProfile {
        properties = List.copyOf(properties);
    }

    public HeadProfile(@NotNull PlayerSkin skin) {
        this(null, null, List.of(new Property("textures", skin.textures(), skin.signature())));
    }

    public @Nullable PlayerSkin skin() {
        for (var property : properties) {
            if ("textures".equals(property.name)) {
                return new PlayerSkin(property.value, property.signature);
            }
        }
        return null;
    }

    public record Property(@NotNull String name, @NotNull String value, @Nullable String signature) {
    }
}
