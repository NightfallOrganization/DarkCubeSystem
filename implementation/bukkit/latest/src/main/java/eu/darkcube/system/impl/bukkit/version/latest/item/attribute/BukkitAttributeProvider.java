/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.attribute;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeProvider;
import org.bukkit.Registry;

public class BukkitAttributeProvider implements AttributeProvider {
    private Map<org.bukkit.attribute.Attribute, Attribute> attributes;

    private Map<org.bukkit.attribute.Attribute, Attribute> attributes() {
        if (attributes != null) return attributes;
        synchronized (this) {
            if (attributes != null) return attributes;
            var attributes = new HashMap<org.bukkit.attribute.Attribute, Attribute>();
            for (var attribute : Registry.ATTRIBUTE) {
                attributes.put(attribute, new BukkitAttribute(attribute));
            }
            this.attributes = Map.copyOf(attributes);
            return this.attributes;
        }
    }

    @Override
    public @NotNull Attribute of(@NotNull Object platformAttribute) {
        if (platformAttribute instanceof Attribute attribute) return attribute;
        if (platformAttribute instanceof org.bukkit.attribute.Attribute attribute) return this.attributes().get(attribute);
        throw new IllegalArgumentException("Invalid attribute: " + platformAttribute);
    }
}
