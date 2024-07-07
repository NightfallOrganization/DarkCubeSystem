/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom;

import eu.darkcube.system.impl.minestom.adventure.MinestomAdventureSupportImpl;
import eu.darkcube.system.impl.minestom.inventory.MinestomInventoryProvider;
import eu.darkcube.system.impl.minestom.inventory.MinestomInventoryTypeProvider;
import eu.darkcube.system.impl.minestom.item.MinestomEquipmentSlotProvider;
import eu.darkcube.system.impl.minestom.item.MinestomItemProvider;
import eu.darkcube.system.impl.minestom.item.attribute.MinestomAttributeModifierProvider;
import eu.darkcube.system.impl.minestom.item.attribute.MinestomAttributeProvider;
import eu.darkcube.system.impl.minestom.item.enchant.MinestomEnchantmentProvider;
import eu.darkcube.system.impl.minestom.item.firework.MinestomFireworkEffectProvider;
import eu.darkcube.system.impl.minestom.item.flag.MinestomItemFlagProvider;
import eu.darkcube.system.impl.minestom.item.material.MinestomMaterialProvider;
import eu.darkcube.system.impl.minestom.util.MinestomColorProvider;
import eu.darkcube.system.impl.server.inventory.item.ItemReferenceProviderImpl;
import eu.darkcube.system.impl.server.inventory.item.ItemTemplateProviderImpl;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.inventory.InventoryProvider;
import eu.darkcube.system.server.inventory.InventoryTypeProvider;
import eu.darkcube.system.server.inventory.item.ItemReferenceProvider;
import eu.darkcube.system.server.inventory.item.ItemTemplateProvider;
import eu.darkcube.system.server.item.EquipmentSlotProvider;
import eu.darkcube.system.server.item.ItemProvider;
import eu.darkcube.system.server.item.attribute.AttributeModifierProvider;
import eu.darkcube.system.server.item.attribute.AttributeProvider;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import eu.darkcube.system.server.item.firework.FireworkEffectProvider;
import eu.darkcube.system.server.item.flag.ItemFlagProvider;
import eu.darkcube.system.server.item.material.MaterialProvider;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.ColorProvider;
import eu.darkcube.system.version.Version;
import net.minestom.server.extensions.Extension;

public class DarkCubeSystemMinestomExtension extends Extension {

    @Override
    public void preInitialize() {
        install(Version.class, new MinestomVersion());
        install(AdventureSupport.class, new MinestomAdventureSupportImpl());
        install(EquipmentSlotProvider.class, new MinestomEquipmentSlotProvider());
        install(ItemProvider.class, new MinestomItemProvider());
        install(MaterialProvider.class, new MinestomMaterialProvider());
        install(ItemFlagProvider.class, new MinestomItemFlagProvider());
        install(FireworkEffectProvider.class, new MinestomFireworkEffectProvider());
        install(EnchantmentProvider.class, new MinestomEnchantmentProvider());
        install(AttributeProvider.class, new MinestomAttributeProvider());
        install(AttributeModifierProvider.class, new MinestomAttributeModifierProvider());
        install(InventoryTypeProvider.class, new MinestomInventoryTypeProvider());
        install(InventoryProvider.class, new MinestomInventoryProvider());
        install(ItemTemplateProvider.class, new ItemTemplateProviderImpl());
        install(ItemReferenceProvider.class, new ItemReferenceProviderImpl());
        install(ColorProvider.class, new MinestomColorProvider());
    }

    private <T> void install(Class<T> type, T instance) {
        InternalProvider.instance().register(type, instance);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void terminate() {
    }
}
