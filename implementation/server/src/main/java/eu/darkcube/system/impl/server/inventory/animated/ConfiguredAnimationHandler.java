/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.animated;

import java.time.Duration;
import java.time.Instant;

import eu.darkcube.system.impl.server.inventory.TemplateInventoryImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.animated.AnimatedTemplateSettings;

public class ConfiguredAnimationHandler<PlatformItem> implements AnimationHandler<PlatformItem> {
    private final @NotNull TemplateInventoryImpl<PlatformItem> templateInventory;
    private final @NotNull AnimatedTemplateSettings settings;

    public ConfiguredAnimationHandler(@NotNull TemplateInventoryImpl<PlatformItem> templateInventory, @NotNull AnimatedTemplateSettings settings) {
        this.templateInventory = templateInventory;
        this.settings = settings;
    }

    @Override
    public void setItem(@NotNull TemplateInventoryImpl<PlatformItem> inventory, int slot, @NotNull PlatformItem item) {
        var slotDuration = settings.getShowAfter(slot);
        var duration = Duration.between(Instant.now(), templateInventory.openInstant().plus(slotDuration));
        inventory.scheduleSetItem(slot, duration, item);
    }
}
