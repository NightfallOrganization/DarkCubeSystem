/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import net.minecraft.resources.ResourceLocation;

public record NoteBlockSoundMapper() implements Mapper<String, ResourceLocation> {
    @Override
    public ResourceLocation apply(String mapping) {
        return ResourceLocation.parse(mapping);
    }

    @Override
    public String load(ResourceLocation mapping) {
        return mapping.toString();
    }
}
