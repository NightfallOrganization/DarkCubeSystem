/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components.util;

import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public sealed interface ConsumeEffect {
    record ApplyEffects(@NotNull List<CustomPotionEffect> effects, float probability) implements ConsumeEffect {
        public ApplyEffects {
            effects = List.copyOf(effects);
        }

        public ApplyEffects(@NotNull List<CustomPotionEffect> effects) {
            this(effects, 1F);
        }
    }

    record RemoveEffects(@NotNull ObjectSet effects) implements ConsumeEffect {
    }

    record ClearAllEffects() implements ConsumeEffect {
        public static final ClearAllEffects INSTANCE = new ClearAllEffects();
    }

    record TeleportRandomly(float diameter) implements ConsumeEffect {
        public TeleportRandomly() {
            this(16);
        }
    }

    record PlaySound(@NotNull Key sound) implements ConsumeEffect {
    }
}
