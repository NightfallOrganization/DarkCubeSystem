/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.Optional;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.component.components.WritableBookContent;
import eu.darkcube.system.server.item.component.components.util.FilteredText;
import net.minecraft.server.network.Filterable;

public record WritableBookContentMapper() implements Mapper<WritableBookContent, net.minecraft.world.item.component.WritableBookContent> {
    @Override
    public net.minecraft.world.item.component.WritableBookContent apply(WritableBookContent mapping) {
        return new net.minecraft.world.item.component.WritableBookContent(mapping.pages().stream().map(f -> new Filterable<>(f.text(), Optional.ofNullable(f.filtered()))).toList());
    }

    @Override
    public WritableBookContent load(net.minecraft.world.item.component.WritableBookContent mapping) {
        return new WritableBookContent(mapping.pages().stream().map(f -> new FilteredText<>(f.raw(), f.filtered().orElse(null))).toList());
    }
}
