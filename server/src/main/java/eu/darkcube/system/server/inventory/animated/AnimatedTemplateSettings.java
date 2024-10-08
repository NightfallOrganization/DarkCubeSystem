/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.animated;

import java.time.Duration;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryTemplateSettings;

public interface AnimatedTemplateSettings extends InventoryTemplateSettings {
    /**
     * @return whether the template should ignore the user's animation settings
     */
    boolean ignoreUserSettings();

    /**
     * Sets whether the template should ignore the user's animation settings
     *
     * @param ignore the ignore value
     */
    void ignoreUserSettings(boolean ignore);

    /**
     * Clears all animations
     */
    void clear();

    /**
     * Whether any animation is configured in these settings
     *
     * @return if animations are configured
     */
    boolean hasAnimation();

    /**
     * Shows a given slot after some duration.
     * Negative durations are scaled with the tick rate, default tick duration is -50ms, with halved tick time this is converted to -100ms
     *
     * @param slot     the slot to show
     * @param duration the duration to wait until the slot is shown
     */
    void showAfter(int slot, @NotNull Duration duration);

    /**
     * Gets the duration a given slot is shown after
     *
     * @param slot the slot for which to get the duration
     * @return the duration
     */
    @NotNull
    Duration getShowAfter(int slot);

    /**
     * Replaces all {@link Duration#ZERO} with the manifold distance to the given slot.
     * The manifold distance in ticks is then multiplied by delay (floating)
     *
     * @param slot  the starting slot
     * @param delay the manifold multiply
     */
    void calculateManifold(int slot, float delay);
}
