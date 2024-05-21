/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest;

import java.util.List;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class AdventureUtils {

    public static Component convert(eu.darkcube.system.libs.net.kyori.adventure.text.Component component) {
        return GsonComponentSerializer
                .gson()
                .deserialize(eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
                        .gson()
                        .serialize(component));
    }

    public static eu.darkcube.system.libs.net.kyori.adventure.text.Component convert(Component component) {
        return eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
                .gson()
                .deserialize(GsonComponentSerializer.gson().serialize(component));
    }

    public static List<eu.darkcube.system.libs.net.kyori.adventure.text.Component> convert2(List<Component> components) {
        return components.stream().map(AdventureUtils::convert).collect(Collectors.toList());
    }
}
