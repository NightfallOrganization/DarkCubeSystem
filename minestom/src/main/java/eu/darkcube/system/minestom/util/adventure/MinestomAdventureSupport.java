/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.util.adventure;

import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.AdventureSupport;

public interface MinestomAdventureSupport extends KyoriAdventureSupport {

    static MinestomAdventureSupport adventureSupport() {
        return (MinestomAdventureSupport) AdventureSupport.adventureSupport();
    }

    @Override
    MinestomAudienceProvider audienceProvider();

    @NotNull
    CompoundBinaryTag convert(@NotNull net.kyori.adventure.nbt.CompoundBinaryTag tag);

    @NotNull
    net.kyori.adventure.nbt.CompoundBinaryTag convert(@NotNull CompoundBinaryTag tag);

}
