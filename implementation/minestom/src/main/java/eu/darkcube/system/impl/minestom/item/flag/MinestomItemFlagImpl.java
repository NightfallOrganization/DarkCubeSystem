/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.flag;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.flag.MinestomItemFlag;
import net.minestom.server.item.ItemHideFlag;

public record MinestomItemFlagImpl(@NotNull ItemHideFlag minestomType) implements MinestomItemFlag {
}
