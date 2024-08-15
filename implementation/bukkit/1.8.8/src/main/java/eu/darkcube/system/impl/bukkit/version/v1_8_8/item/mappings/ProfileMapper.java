/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import java.lang.reflect.Field;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.darkcube.system.bukkit.util.ReflectionUtils;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.component.components.HeadProfile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class ProfileMapper implements Mapper<HeadProfile> {
    private static final Field CraftMetaSkull$profile = ReflectionUtils.getField("CraftMetaSkull", ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY, true, "profile");

    @Override
    public void apply(HeadProfile mapping, ItemStack item, ItemMeta meta) {
        var skullMeta = (SkullMeta) meta;
        var uuid = mapping.uuid();
        var name = mapping.name();
        var profile = new GameProfile(uuid, name);
        for (var property : mapping.properties()) {
            profile.getProperties().put(property.name(), new Property(property.name(), property.value(), property.signature()));
        }
        if (name != null && uuid == null && mapping.properties().isEmpty()) {
            skullMeta.setOwner(name);
        } else {
            ReflectionUtils.setValue(meta, CraftMetaSkull$profile, profile);
        }
    }

    @Override
    public @Nullable HeadProfile convert(ItemStack item, ItemMeta meta) {
        if (!(meta instanceof SkullMeta skullMeta)) return null;
        var pp = (GameProfile) ReflectionUtils.getValue(skullMeta, CraftMetaSkull$profile);
        if (pp == null) return null;
        return new HeadProfile(pp.getName(), pp.getId(), pp.getProperties().entries().stream().map(e -> new HeadProfile.Property(e.getValue().getName(), e.getValue().getValue(), e.getValue().getSignature())).toList());
    }
}
