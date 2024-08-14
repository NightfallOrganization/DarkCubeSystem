/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.adventure;

import java.io.IOException;

import eu.darkcube.system.impl.kyori.wrapper.DefaultKyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.TagStringIO;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport;
import eu.darkcube.system.minestom.util.adventure.MinestomAudienceProvider;

public class MinestomAdventureSupportImpl implements MinestomAdventureSupport, DefaultKyoriAdventureSupport {
    private final MinestomAudienceProvider audienceProvider = new MinestomAudienceProviderImpl(this);

    @Override
    @NotNull
    public CompoundBinaryTag convert(@NotNull net.kyori.adventure.nbt.CompoundBinaryTag tag) {
        try {
            return TagStringIO.get().asCompound(net.kyori.adventure.nbt.TagStringIO.get().asString(tag));
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Override
    @NotNull
    public net.kyori.adventure.nbt.CompoundBinaryTag convert(@NotNull CompoundBinaryTag tag) {
        try {
            return net.kyori.adventure.nbt.TagStringIO.get().asCompound(TagStringIO.get().asString(tag));
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Override
    public MinestomAudienceProvider audienceProvider() {
        return audienceProvider;
    }
}
