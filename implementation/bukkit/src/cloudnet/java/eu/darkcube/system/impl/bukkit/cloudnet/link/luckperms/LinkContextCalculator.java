/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.cloudnet.link.luckperms;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LinkContextCalculator implements ContextCalculator<Player> {
    private final ServiceInfoHolder info;

    public LinkContextCalculator() {
        info = InjectionLayer.boot().instance(ServiceInfoHolder.class);
    }

    private @NotNull ContextSet contextSet() {
        var b = ImmutableContextSet.builder();
        b.add("task", info.serviceInfo().serviceId().taskName());
        b.add("service", info.serviceInfo().serviceId().name());
        b.add("environment", info.serviceInfo().serviceId().environmentName());
        for (var group : info.serviceInfo().configuration().groups()) {
            b.add("group", group);
        }
        return b.build();
    }

    @Override
    public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
        consumer.accept(contextSet());
    }

    @Override
    public @NonNull ContextSet estimatePotentialContexts() {
        return contextSet();
    }
}
