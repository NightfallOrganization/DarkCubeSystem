/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.Optional;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.server.item.component.components.WrittenBookContent;
import eu.darkcube.system.server.item.component.components.util.FilteredText;
import net.minecraft.server.network.Filterable;

public record WrittenBookContentMapper() implements Mapper<WrittenBookContent, net.minecraft.world.item.component.WrittenBookContent> {
    @Override
    public net.minecraft.world.item.component.WrittenBookContent apply(WrittenBookContent mapping) {
        return new net.minecraft.world.item.component.WrittenBookContent(new Filterable<>(mapping.title().text(), Optional.ofNullable(mapping.title().filtered())), mapping.author(), mapping.generation(), mapping.pages().stream().map(f -> new Filterable<>(MapperUtil.convert(f.text()), Optional.ofNullable(f.filtered()).map(MapperUtil::convert))).toList(), mapping.resolved());
    }

    @Override
    public WrittenBookContent load(net.minecraft.world.item.component.WrittenBookContent mapping) {
        return new WrittenBookContent(mapping.pages().stream().map(f -> new FilteredText<>(MapperUtil.convert(f.raw()), f.filtered().map(MapperUtil::convert).orElse(null))).toList(), new FilteredText<>(mapping.title().raw(), mapping.title().filtered().orElse(null)), mapping.author(), mapping.generation(), mapping.resolved());
    }
}
