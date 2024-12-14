/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.EquipmentSlot;
import eu.darkcube.system.server.item.component.components.util.ObjectSet;

public record Equippable(@NotNull EquipmentSlot slot, @NotNull Key equipSound, @Nullable Key model, @Nullable Key cameraOverlay, @Nullable ObjectSet allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt) {
}
