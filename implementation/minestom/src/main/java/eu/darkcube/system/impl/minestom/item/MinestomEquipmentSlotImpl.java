/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.MinestomEquipmentSlot;

public record MinestomEquipmentSlotImpl(@NotNull net.minestom.server.entity.EquipmentSlot minestomType) implements MinestomEquipmentSlot {
}
