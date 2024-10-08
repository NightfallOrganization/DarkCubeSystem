/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.animated;

import java.time.Duration;
import java.util.Arrays;

import eu.darkcube.system.impl.server.inventory.InventoryAPIUtils;
import eu.darkcube.system.impl.server.inventory.InventoryTemplateImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.animated.AnimatedTemplateSettings;

public class AnimatedTemplateSettingsImpl<PlatformPlayer> implements AnimatedTemplateSettings {
    private final InventoryTemplateImpl<PlatformPlayer> template;
    private final Duration[] durations;
    private boolean ignoreUserSettings;

    public AnimatedTemplateSettingsImpl(InventoryTemplateImpl<PlatformPlayer> template) {
        this.template = template;
        this.durations = new Duration[template.size()];
        Arrays.fill(this.durations, Duration.ZERO);
    }

    @Override
    public boolean ignoreUserSettings() {
        return this.ignoreUserSettings;
    }

    @Override
    public void ignoreUserSettings(boolean ignore) {
        this.ignoreUserSettings = ignore;
    }

    @Override
    public void clear() {
        Arrays.fill(this.durations, Duration.ZERO);
    }

    @Override
    public boolean hasAnimation() {
        for (var duration : this.durations) {
            if (!duration.isZero()) return true;
        }
        return false;
    }

    @Override
    public void showAfter(int slot, @NotNull Duration duration) {
        this.durations[slot] = duration;
    }

    @Override
    public @NotNull Duration getShowAfter(int slot) {
        return this.durations[slot];
    }

    @Override
    public void calculateManifold(int slot, float delay) {
        var startRow = slot / 9;
        var startIdx = slot % 9;
        var defaultTick = InventoryAPIUtils.utils().defaultTickTime().toMillis();

        for (var i = 0; i < this.durations.length; i++) {
            var duration = this.durations[i];
            if (!duration.isZero()) continue;
            var row = i / 9;
            var idx = i % 9;

            var manifold = Math.abs(startRow - row) + Math.abs(startIdx - idx) + 1;
            var millis = Math.round(manifold * defaultTick * delay);
            duration = Duration.ofMillis(-millis);
            this.durations[i] = duration;
        }
    }

    @Override
    public @NotNull InventoryTemplateImpl<PlatformPlayer> inventoryTemplate() {
        return this.template;
    }
}
