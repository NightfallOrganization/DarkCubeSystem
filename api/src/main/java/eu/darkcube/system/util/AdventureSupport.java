/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import eu.darkcube.system.libs.net.kyori.adventure.platform.AudienceProvider;

public interface AdventureSupport {

    static AdventureSupport adventureSupport() {
        return AdventureSupportHolder.instance();
    }

    AudienceProvider audienceProvider();
}
