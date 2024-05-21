/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest;

import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.impl.bukkit.version.AbstractVersionHandler;
import eu.darkcube.system.impl.bukkit.version.latest.item.ItemProviderImpl;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeModifierProvider;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeProvider;
import eu.darkcube.system.server.item.ItemProvider;
import eu.darkcube.system.server.item.attribute.AttributeModifierProvider;
import eu.darkcube.system.server.item.attribute.AttributeProvider;

public class VersionHandler extends AbstractVersionHandler {
    public VersionHandler() {
        var ext = InjectionLayer.ext();
        ext.install(BindingBuilder.create().bind(ItemProvider.class).toInstance(new ItemProviderImpl()));
        ext.install(BindingBuilder.create().bind(AttributeProvider.class).toInstance(new BukkitAttributeProvider()));
        ext.install(BindingBuilder.create().bind(AttributeModifierProvider.class).toInstance(new BukkitAttributeModifierProvider()));
    }

    @Override
    protected Version createVersion() {
        return new Version();
    }
}
