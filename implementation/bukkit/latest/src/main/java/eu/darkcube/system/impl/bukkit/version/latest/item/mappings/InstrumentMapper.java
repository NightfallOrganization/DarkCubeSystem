/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Instrument;

public record InstrumentMapper() implements DirectMapper<String, Holder<Instrument>> {
    @Override
    public Holder<Instrument> apply(String mapping) {
        return BuiltInRegistries.INSTRUMENT.getHolder(ResourceLocation.parse(mapping)).orElseThrow();
    }

    @Override
    public String load(Holder<Instrument> mapping) {
        return mapping.unwrapKey().orElseThrow().location().toString();
    }
}