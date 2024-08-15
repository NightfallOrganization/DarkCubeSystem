/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.components.util.CustomPotionEffect;

public record Food(int nutrition, float saturationModifier, boolean canAlwaysEat, float eatSeconds, @Nullable ItemBuilder usingConvertsTo, @NotNull List<EffectChance> effects) {
    public Food {
        usingConvertsTo = usingConvertsTo == null ? null : usingConvertsTo.clone();
        effects = List.copyOf(effects);
    }

    public record EffectChance(@NotNull CustomPotionEffect effect, float probability) {
    }
}
