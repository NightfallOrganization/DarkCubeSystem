/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.util.adventure;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.platform.AudienceProvider;

public interface MinestomAudienceProvider extends AudienceProvider {
    Audience audience(net.kyori.adventure.audience.Audience audience);
}
