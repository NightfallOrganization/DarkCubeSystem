/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.meta;

public final class SpawnEggBuilderMeta implements BuilderMeta {
    private String entityTag;

    public SpawnEggBuilderMeta() {
    }

    public SpawnEggBuilderMeta(String entityTag) {
        this.entityTag = entityTag;
    }

    public String entityTag() {
        return entityTag;
    }

    public SpawnEggBuilderMeta entityTag(String entityTag) {
        this.entityTag = entityTag;
        return this;
    }

    @Override
    public SpawnEggBuilderMeta clone() {
        return new SpawnEggBuilderMeta(entityTag);
    }
}
