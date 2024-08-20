/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory;

import static eu.darkcube.system.server.inventory.InventoryMask.slots;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.inventory.item.ItemTemplate;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.util.Unit;

public final class DarkCubeItemTemplates {
    private static final ItemProvider PROVIDER = InternalProvider.instance().instance(ItemProvider.class);
    private static final Object l = item('l');
    private static final Object d = item('d');
    private static final Object p = item('p');
    private static final Object m = item('m');

    private static Object item(char c) {
        return PROVIDER.provide(c).set(ItemComponent.HIDE_TOOLTIP, Unit.INSTANCE).displayname(Component.empty());
    }

    private static ItemTemplate template(String mask) {
        var template = ItemTemplate.create();
        template.setItem(l, slots(mask, 'l'));
        template.setItem(d, slots(mask, 'd'));
        template.setItem(p, slots(mask, 'p'));
        template.setItem(m, slots(mask, 'm'));
        return template;
    }

    @Api
    public interface Gray {
        @Api
        ItemTemplate TEMPLATE_6 = template("""
                lllldllll
                dddlllddd
                ldddddddl
                dldddddld
                ldldddldl
                lldldldll
                """);
        @Api
        ItemTemplate TEMPLATE_5 = template("""
                lllldllll
                lddlllddl
                dldddddld
                ldldddldl
                lldldldll
                """);
        @Api
        ItemTemplate TEMPLATE_4 = template("""
                lllldllll
                dddlllddd
                ldldddldl
                llddlddll
                """);
        @Api
        ItemTemplate TEMPLATE_3 = template("""
                lllllllll
                ddddddddd
                lllllllll
                """);
        @Api
        ItemTemplate TEMPLATE_2 = template("""
                lllllllll
                ddddddddd
                """);
        @Api
        ItemTemplate TEMPLATE_1 = template("""
                ddddddddd
                """);
    }

    @Api
    public interface Violet {
        @Api
        ItemTemplate TEMPLATE_6 = template("""
                pmpmpmpmp
                mdldddldm
                pldddddlp
                mldddddlm
                pdldddldp
                mpmpmpmpm
                """);
        @Api
        ItemTemplate TEMPLATE_5 = template("""
                pmpmpmpmp
                mdldddldm
                pldddddlp
                mdldddldm
                pmpmpmpmp
                """);
        @Api
        ItemTemplate TEMPLATE_4 = template("""
                pmpmpmpmp
                mldldldlm
                pdldldldp
                mpmpmpmpm
                """);
        @Api
        ItemTemplate TEMPLATE_3 = template("""
                pmpmpmpmp
                mdldldldm
                pmpmpmpmp
                """);
        @Api
        ItemTemplate TEMPLATE_2 = template("""
                pmpmpmpmp
                ldldldldl
                """);
        @Api
        ItemTemplate TEMPLATE_1 = template("""
                ddddddddd
                """);
    }

    public interface ItemProvider {
        ItemBuilder provide(char character);
    }
}
