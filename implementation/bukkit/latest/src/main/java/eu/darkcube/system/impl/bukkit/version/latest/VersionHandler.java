/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest;

import eu.darkcube.system.impl.bukkit.version.AbstractVersionHandler;
import eu.darkcube.system.impl.bukkit.version.latest.item.ItemProviderImpl;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeModifierProvider;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeProvider;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.item.ItemProvider;
import eu.darkcube.system.server.item.attribute.AttributeModifierProvider;
import eu.darkcube.system.server.item.attribute.AttributeProvider;

public class VersionHandler extends AbstractVersionHandler {
    public VersionHandler() {
        InternalProvider.instance().register(ItemProvider.class, new ItemProviderImpl());
        InternalProvider.instance().register(AttributeProvider.class, new BukkitAttributeProvider());
        InternalProvider.instance().register(AttributeModifierProvider.class, new BukkitAttributeModifierProvider());
    }

    @Override
    protected Version createVersion() {
        return new Version();
    }
}
