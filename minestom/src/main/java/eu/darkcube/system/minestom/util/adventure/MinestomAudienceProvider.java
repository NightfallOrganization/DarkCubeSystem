/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.util.adventure;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.platform.AudienceProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface MinestomAudienceProvider extends AudienceProvider {
    @NotNull
    Audience audience(@NotNull net.kyori.adventure.audience.Audience audience);
}
