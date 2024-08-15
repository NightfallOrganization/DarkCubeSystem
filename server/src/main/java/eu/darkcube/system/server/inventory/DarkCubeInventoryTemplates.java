/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory;

import static eu.darkcube.system.server.inventory.InventoryMask.slots;

import java.io.IOException;
import java.util.function.Function;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Language;

public class DarkCubeInventoryTemplates {
    static final PlatformProvider PROVIDER = InternalProvider.instance().instance(PlatformProvider.class);

    public static void configureSounds(@NotNull InventoryTemplate template) {
        template.addListener(TemplateInventoryListener.ofStateful(() -> new TemplateInventoryListener() {
            private boolean finished = false;
            private User user;

            @Override
            public void onOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
                this.user = user;
            }

            @Override
            public void onClose(@NotNull TemplateInventory inventory, @NotNull User user) {
                this.user = null;
            }

            @Override
            public void onOpenAnimationFinished(@NotNull TemplateInventory inventory) {
                this.finished = true;
            }

            @Override
            public void onUpdate(@NotNull TemplateInventory inventory) {
                if (!this.finished) {
                    if (this.user == null) return;
                    if (!this.user.settings().sounds()) return;
                    PROVIDER.playSound(this.user);
                }
            }
        }));
    }

    public static void configureDisplayItem(@NotNull InventoryTemplate template, @NotNull Object displayItem) {
        template.setItem(0, 4, displayItem);
    }

    @Api
    public interface Paged {
        String MASK_1x9 = """
                p#######n
                """;
        String MASK_2x9 = """
                .........
                p#######n
                """;
        String MASK_3x9 = """
                .........
                p#######n
                .........
                """;
        String MASK_4x9 = """
                .........
                p#######n
                p#######n
                .........
                """;
        String MASK_5x9 = """
                .........
                p#######n
                p#######n
                p#######n
                .........
                """;
        String MASK_6x9 = """
                .........
                .#######.
                p#######n
                p#######n
                .#######.
                .........
                """;
        int[] SLOTS_1x9_PREV = slots(MASK_1x9, 'p');
        int[] SLOTS_1x9_NEXT = slots(MASK_1x9, 'n');
        int[] SLOTS_1x9_PAGE = slots(MASK_1x9, '#');
        int[] SLOTS_2x9_PREV = slots(MASK_2x9, 'p');
        int[] SLOTS_2x9_NEXT = slots(MASK_2x9, 'n');
        int[] SLOTS_2x9_PAGE = slots(MASK_2x9, '#');
        int[] SLOTS_3x9_PREV = slots(MASK_3x9, 'p');
        int[] SLOTS_3x9_NEXT = slots(MASK_3x9, 'n');
        int[] SLOTS_3x9_PAGE = slots(MASK_3x9, '#');
        int[] SLOTS_4x9_PREV = slots(MASK_4x9, 'p');
        int[] SLOTS_4x9_NEXT = slots(MASK_4x9, 'n');
        int[] SLOTS_4x9_PAGE = slots(MASK_4x9, '#');
        int[] SLOTS_5x9_PREV = slots(MASK_5x9, 'p');
        int[] SLOTS_5x9_NEXT = slots(MASK_5x9, 'n');
        int[] SLOTS_5x9_PAGE = slots(MASK_5x9, '#');
        int[] SLOTS_6x9_PREV = slots(MASK_6x9, 'p');
        int[] SLOTS_6x9_NEXT = slots(MASK_6x9, 'n');
        int[] SLOTS_6x9_PAGE = slots(MASK_6x9, '#');

        @Api
        static void configureButtonItems(@NotNull InventoryTemplate template) {
            template.pagination().previousButton().setItem(user -> {
                var builder = PROVIDER.previousItem();
                builder.displayname(Messages.PREV_ITEM.get(user));
                return builder;
            });
            template.pagination().nextButton().setItem(user -> {
                var builder = PROVIDER.nextItem();
                builder.displayname(Messages.NEXT_ITEM.get(user));
                return builder;
            });
        }

        @Api
        static void configure1x9(@NotNull InventoryTemplate template) {
            configureSounds(template);
            template.pagination().pageSlots(SLOTS_1x9_PAGE);
            configure1x9Buttons(template);
        }

        @Api
        static void configure1x9(@NotNull InventoryTemplate template, @NotNull Object displayItem) {
            configure1x9(template);
            configureDisplayItem(template, displayItem);
        }

        @Api
        static void configure1x9Buttons(@NotNull InventoryTemplate template) {
            configureButtonItems(template);
            template.pagination().previousButton().slots(SLOTS_1x9_PREV);
            template.pagination().nextButton().slots(SLOTS_1x9_NEXT);
        }

        @Api
        static void configure2x9(@NotNull InventoryTemplate template) {
            configureSounds(template);
            template.pagination().pageSlots(SLOTS_2x9_PAGE);
            configure2x9Buttons(template);
        }

        @Api
        static void configure2x9(@NotNull InventoryTemplate template, @NotNull Object displayItem) {
            configure2x9(template);
            configureDisplayItem(template, displayItem);
        }

        @Api
        static void configure2x9Buttons(@NotNull InventoryTemplate template) {
            configureButtonItems(template);
            template.pagination().previousButton().slots(SLOTS_2x9_PREV);
            template.pagination().nextButton().slots(SLOTS_2x9_NEXT);
        }

        @Api
        static void configure3x9(@NotNull InventoryTemplate template) {
            configureSounds(template);
            template.pagination().pageSlots(SLOTS_3x9_PAGE);
            configure3x9Buttons(template);
        }

        @Api
        static void configure3x9(@NotNull InventoryTemplate template, @NotNull Object displayItem) {
            configure3x9(template);
            configureDisplayItem(template, displayItem);
        }

        @Api
        static void configure3x9Buttons(@NotNull InventoryTemplate template) {
            configureButtonItems(template);
            template.pagination().previousButton().slots(SLOTS_3x9_PREV);
            template.pagination().nextButton().slots(SLOTS_3x9_NEXT);
        }

        @Api
        static void configure4x9(@NotNull InventoryTemplate template) {
            configureSounds(template);
            template.pagination().pageSlots(SLOTS_4x9_PAGE);
            configure4x9Buttons(template);
        }

        @Api
        static void configure4x9(@NotNull InventoryTemplate template, @NotNull Object displayItem) {
            configure4x9(template);
            configureDisplayItem(template, displayItem);
        }

        @Api
        static void configure4x9Buttons(@NotNull InventoryTemplate template) {
            configureButtonItems(template);
            template.pagination().previousButton().slots(SLOTS_4x9_PREV);
            template.pagination().nextButton().slots(SLOTS_4x9_NEXT);
        }

        @Api
        static void configure5x9(@NotNull InventoryTemplate template) {
            configureSounds(template);
            template.pagination().pageSlots(SLOTS_5x9_PAGE);
            configure5x9Buttons(template);
        }

        @Api
        static void configure5x9(@NotNull InventoryTemplate template, @NotNull Object displayItem) {
            configure5x9(template);
            configureDisplayItem(template, displayItem);
        }

        @Api
        static void configure5x9Buttons(@NotNull InventoryTemplate template) {
            configureButtonItems(template);
            template.pagination().previousButton().slots(SLOTS_5x9_PREV);
            template.pagination().nextButton().slots(SLOTS_5x9_NEXT);
        }

        @Api
        static void configure6x9(@NotNull InventoryTemplate template) {
            configureSounds(template);
            template.pagination().pageSlots(SLOTS_6x9_PAGE);
            configure6x9Buttons(template);
        }

        @Api
        static void configure6x9(@NotNull InventoryTemplate template, @NotNull Object displayItem) {
            configure6x9(template);
            configureDisplayItem(template, displayItem);
        }

        @Api
        static void configure6x9Buttons(@NotNull InventoryTemplate template) {
            configureButtonItems(template);
            template.pagination().previousButton().slots(SLOTS_6x9_PREV);
            template.pagination().nextButton().slots(SLOTS_6x9_NEXT);
        }
    }

    public interface PlatformProvider {
        @NotNull
        ItemBuilder previousItem();

        @NotNull
        ItemBuilder nextItem();

        void playSound(@NotNull User user);
    }

    private enum Messages {
        PREV_ITEM,
        NEXT_ITEM;

        private static final String PREFIX = "DARKCUBE_DEFAULTS_ITEM_";
        private static final Function<String, String> MODIFIER = s -> PREFIX + s;

        private Component get(User user) {
            return user.language().getMessage(MODIFIER.apply(name()));
        }

        static {
            var loader = Messages.class.getClassLoader();
            try {
                Language.ENGLISH.registerLookup(loader, "darkcubesystem/items_en.properties", MODIFIER);
                Language.GERMAN.registerLookup(loader, "darkcubesystem/items_de.properties", MODIFIER);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
