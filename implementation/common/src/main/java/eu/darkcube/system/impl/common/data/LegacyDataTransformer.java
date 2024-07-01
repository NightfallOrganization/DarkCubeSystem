/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.common.data;

import java.util.List;

import eu.darkcube.system.libs.com.google.gson.JsonObject;

public class LegacyDataTransformer {
    public static void transformLegacyPersistentData(JsonObject json) {
        for (var name : List.copyOf(json.keySet())) {
            var split = name.split(":");
            var namespace = split[0];
            var key = split[1];
            var builder = new StringBuilder();
            for (var c : namespace.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    builder.append(Character.toLowerCase(c));
                } else {
                    builder.append(c);
                }
            }
            builder.append(":");
            for (var c : key.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    builder.append("_").append(Character.toLowerCase(c));
                } else {
                    builder.append(c);
                }
            }
            var constructed = builder.toString();
            if (!name.equals(constructed)) {
                var element = json.remove(name);
                json.add(constructed, element);
            }
        }
    }
}
