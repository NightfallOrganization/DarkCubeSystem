/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.util;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.BukkitAudiences;
import eu.darkcube.system.util.AdventureSupport;

@Api
public interface BukkitAdventureSupport extends AdventureSupport {
    @Api
    static BukkitAdventureSupport adventureSupport() {
        return (BukkitAdventureSupport) AdventureSupport.adventureSupport();
    }

    @Override
    BukkitAudiences audienceProvider();
}
