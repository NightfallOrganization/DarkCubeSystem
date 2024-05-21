/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.attribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeProvider;

public class MinestomAttributeProvider implements AttributeProvider {
    private final Map<net.minestom.server.attribute.Attribute, Attribute> attributeMap = new ConcurrentHashMap<>();

    @Override
    public @NotNull Attribute of(@NotNull Object platformAttribute) {
        if (platformAttribute instanceof Attribute attribute) return attribute;
        if (platformAttribute instanceof net.minestom.server.attribute.Attribute attribute) return attributeMap.computeIfAbsent(attribute, MinestomAttributeImpl::new);
        throw new IllegalArgumentException("Invalid Attribute: " + platformAttribute);
    }
}
