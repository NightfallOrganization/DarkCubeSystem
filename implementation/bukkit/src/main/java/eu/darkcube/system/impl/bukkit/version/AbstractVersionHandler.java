/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version;

import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.bukkit.inventory.BukkitInventoryProvider;
import eu.darkcube.system.impl.bukkit.inventory.BukkitInventoryTypeProvider;
import eu.darkcube.system.impl.bukkit.item.BukkitEquipmentSlotGroupProvider;
import eu.darkcube.system.impl.bukkit.item.BukkitEquipmentSlotProvider;
import eu.darkcube.system.impl.bukkit.item.firework.BukkitFireworkEffectProvider;
import eu.darkcube.system.impl.bukkit.util.BukkitColorProvider;
import eu.darkcube.system.impl.server.commandapi.argument.ServerArgumentsImpl;
import eu.darkcube.system.impl.server.inventory.container.ContainerProviderImpl;
import eu.darkcube.system.impl.server.inventory.item.ItemReferenceProviderImpl;
import eu.darkcube.system.impl.server.inventory.item.ItemTemplateProviderImpl;
import eu.darkcube.system.impl.server.inventory.listener.InventoryListenerProviderImpl;
import eu.darkcube.system.impl.server.item.component.ItemComponentProviderImpl;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.commandapi.argument.ServerArguments;
import eu.darkcube.system.server.inventory.InventoryProvider;
import eu.darkcube.system.server.inventory.InventoryTypeProvider;
import eu.darkcube.system.server.inventory.container.ContainerProvider;
import eu.darkcube.system.server.inventory.item.ItemReferenceProvider;
import eu.darkcube.system.server.inventory.item.ItemTemplateProvider;
import eu.darkcube.system.server.inventory.listener.InventoryListenerProvider;
import eu.darkcube.system.server.item.EquipmentSlotGroupProvider;
import eu.darkcube.system.server.item.EquipmentSlotProvider;
import eu.darkcube.system.server.item.component.ItemComponentProvider;
import eu.darkcube.system.server.item.firework.FireworkEffectProvider;
import eu.darkcube.system.util.ColorProvider;
import eu.darkcube.system.version.Version;

public abstract class AbstractVersionHandler implements BukkitVersionHandler {
    protected final BukkitVersionImpl version;

    public AbstractVersionHandler() {
        version = createVersion();
        install(Version.class, version);
        install(ItemComponentProvider.class, new ItemComponentProviderImpl());
        install(FireworkEffectProvider.class, new BukkitFireworkEffectProvider());
        install(EquipmentSlotProvider.class, new BukkitEquipmentSlotProvider());
        install(EquipmentSlotGroupProvider.class, new BukkitEquipmentSlotGroupProvider());
        install(ColorProvider.class, new BukkitColorProvider());
        install(InventoryTypeProvider.class, new BukkitInventoryTypeProvider());
        install(InventoryProvider.class, new BukkitInventoryProvider());
        install(ItemReferenceProvider.class, new ItemReferenceProviderImpl());
        install(InventoryListenerProvider.class, new InventoryListenerProviderImpl());
        install(ContainerProvider.class, new ContainerProviderImpl());
        install(ItemTemplateProvider.class, new ItemTemplateProviderImpl());
        install(ServerArguments.class, new ServerArgumentsImpl());
    }

    protected final <T> void install(Class<T> cls, T instance) {
        InternalProvider.instance().register(cls, instance);
    }

    @Override
    public void onLoad(DarkCubeSystemBukkit system) {
        version.loaded(system);
    }

    @Override
    public void onEnable(DarkCubeSystemBukkit system) {
        version.enabled(system);
    }

    protected abstract BukkitVersionImpl createVersion();
}
