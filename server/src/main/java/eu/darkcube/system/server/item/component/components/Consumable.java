/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.component.components.util.ConsumeEffect;
import eu.darkcube.system.server.item.component.components.util.ItemAnimation;

public record Consumable(float consumeSeconds, @NotNull ItemAnimation animation, @NotNull Key sound, boolean hasConsumeParticles, @NotNull List<ConsumeEffect> onConsumeEffects) {
    private static final float CONSUME_SECONDS = 1.6F;
    private static final ItemAnimation ANIMATION = ItemAnimation.EAT;
    private static final Key SOUND = Key.key("minecraft:entity.generic.eat");
    private static final boolean HAS_CUSTOM_PARTICLES = true;
    private static final List<ConsumeEffect> ON_CONSUME_EFFECTS = List.of();

    public Consumable() {
        this(CONSUME_SECONDS, ANIMATION, SOUND, HAS_CUSTOM_PARTICLES, ON_CONSUME_EFFECTS);
    }

    public Consumable(float consumeSeconds) {
        this(consumeSeconds, ANIMATION, SOUND, HAS_CUSTOM_PARTICLES, ON_CONSUME_EFFECTS);
    }

    public Consumable(@NotNull ItemAnimation animation) {
        this(CONSUME_SECONDS, animation, SOUND, HAS_CUSTOM_PARTICLES, ON_CONSUME_EFFECTS);
    }

    public Consumable(boolean hasConsumeParticles) {
        this(CONSUME_SECONDS, ANIMATION, SOUND, hasConsumeParticles, ON_CONSUME_EFFECTS);
    }

    public Consumable(float consumeSeconds, @NotNull ItemAnimation animation, @NotNull Key sound, boolean hasConsumeParticles) {
        this(consumeSeconds, animation, sound, hasConsumeParticles, ON_CONSUME_EFFECTS);
    }
}
