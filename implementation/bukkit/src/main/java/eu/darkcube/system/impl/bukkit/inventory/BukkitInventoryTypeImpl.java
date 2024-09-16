/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.inventory;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.event.inventory.InventoryType;

public record BukkitInventoryTypeImpl(@NotNull InventoryType bukkitType) implements BukkitInventoryType {
    @Override
    public Component defaultTitle() {
        return KyoriAdventureSupport.adventureSupport().convert(bukkitType.defaultTitle());
    }

    @Override
    public int size() {
        return bukkitType.getDefaultSize();
    }
}
