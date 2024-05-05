/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.meta;

import eu.darkcube.system.util.Color;

public final class LeatherArmorBuilderMeta implements BuilderMeta {
    private Color color;

    public LeatherArmorBuilderMeta() {
    }

    public LeatherArmorBuilderMeta(Color color) {
        this.color = color;
    }

    public Color color() {
        return color;
    }

    public LeatherArmorBuilderMeta color(Color color) {
        this.color = color;
        return this;
    }

    @Override
    public LeatherArmorBuilderMeta clone() {
        return new LeatherArmorBuilderMeta(color);
    }
}
