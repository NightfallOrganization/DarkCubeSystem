/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.Optional;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.component.components.HeadProfile;
import net.minecraft.world.item.component.ResolvableProfile;

public record ProfileMapper() implements Mapper<HeadProfile, ResolvableProfile> {
    @Override
    public ResolvableProfile apply(HeadProfile mapping) {
        var name = mapping.name();
        var id = mapping.uuid();
        var properties = new PropertyMap();
        for (var property : mapping.properties()) {
            properties.put(property.name(), new Property(property.name(), property.value(), property.signature()));
        }
        return new ResolvableProfile(Optional.ofNullable(name), Optional.ofNullable(id), properties);
    }

    @Override
    public HeadProfile load(ResolvableProfile mapping) {
        return new HeadProfile(mapping.name().orElse(null), mapping.id().orElse(null), mapping.properties().values().stream().map(p -> new HeadProfile.Property(p.name(), p.value(), p.signature())).toList());
    }
}
