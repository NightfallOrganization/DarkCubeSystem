/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import com.mojang.datafixers.util.Either;
import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.component.components.JukeboxPlayable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EitherHolder;

public record JukeboxPlayableMapper() implements DirectMapper<JukeboxPlayable, net.minecraft.world.item.JukeboxPlayable> {
    @Override
    public net.minecraft.world.item.JukeboxPlayable apply(JukeboxPlayable mapping) {
        var id = ResourceLocation.parse(mapping.song().asString());
        var resourceKey = ResourceKey.create(Registries.JUKEBOX_SONG, id);
        return new net.minecraft.world.item.JukeboxPlayable(EitherHolder.fromEither(Either.right(resourceKey)), mapping.showInTooltip());
    }

    @Override
    public JukeboxPlayable load(net.minecraft.world.item.JukeboxPlayable mapping) {
        var either = mapping.song().asEither().mapLeft(l -> l.unwrapKey().orElseThrow().location()).mapRight(ResourceKey::location);
        var song = Key.key(either.left().or(either::right).orElseThrow().toString());
        return new JukeboxPlayable(song, mapping.showInTooltip());
    }
}
