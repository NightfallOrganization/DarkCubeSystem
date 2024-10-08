/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.data.component.DataComponent;
import eu.darkcube.system.server.data.component.DataComponentMap;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeModifier;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.enchant.Enchantment;
import eu.darkcube.system.server.item.flag.ItemFlag;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.server.item.storage.ItemPersistentDataStorage;

@Api
public interface ItemBuilder extends DataComponent.Holder, ItemComponent {
    @Api
    static @NotNull ItemBuilder item() {
        return ItemProvider.itemProvider().item((Material) null);
    }

    /**
     * Tries to construct an ItemBuilder for the given object.
     * The object may be:
     * <ul>
     *     <li>a platform specific Material</li>
     *     <li>a platform specific ItemStack</li>
     *     <li>an {@link ItemBuilder}</li>
     * </ul>
     *
     * @param object the object by which the ItemBuilder should be created.
     * @return a new {@link ItemBuilder}
     */
    @Api
    static @NotNull ItemBuilder item(@NotNull Object object) {
        return ItemProvider.itemProvider().item(object);
    }

    @Api
    static @NotNull ItemBuilder item(@NotNull Material material) {
        return ItemProvider.itemProvider().item(material);
    }

    @Api
    static @NotNull ItemBuilder item(@NotNull JsonElement json) {
        return ItemProvider.itemProvider().item(json);
    }

    @Api
    @NotNull
    Material material();

    @Api
    @NotNull
    ItemBuilder material(@NotNull Material material);

    @Api
    default @NotNull ItemBuilder material(@NotNull Object material) {
        return material(Material.of(material));
    }

    @Api
    @NotNull
    default ItemBuilder apply(@NotNull Consumer<@NotNull ItemBuilder> function) {
        function.accept(this);
        return this;
    }

    @Api
    @NotNull
    default ItemBuilder map(@NotNull Function<@NotNull ItemBuilder, @NotNull ItemBuilder> mapper) {
        return mapper.apply(this);
    }

    @Api
    @NotNull
    DataComponentMap components();

    @Override
    @Api
    @Nullable
    <T> T get(@NotNull DataComponent<T> component);

    @Api
    @NotNull
    <T> ItemBuilder set(@NotNull DataComponent<T> component, @NotNull T value);

    @Api
    @NotNull
    <T> ItemBuilder remove(@NotNull DataComponent<T> component);

    /**
     * Maps a component to another version of the same type. Will do nothing if the component doesn't exist on the item
     */
    @Api
    @NotNull
    <T> ItemBuilder map(@NotNull DataComponent<T> component, @NotNull Function<T, T> mapper);

    /**
     * Maps a component to another version of the same type. The component must exist in the item - it must not be null
     */
    @Api
    @NotNull
    <T> ItemBuilder mapNotNull(@NotNull DataComponent<T> component, @NotNull Function<T, T> mapper);

    @Api
    boolean isSimilar(@NotNull ItemBuilder item);

    @Api
    int amount();

    @Api
    @NotNull
    ItemBuilder amount(int amount);

    @Api
    boolean canBeRepairedBy(ItemBuilder item);

    @Api
    @NotNull
    @Unmodifiable
    Collection<@NotNull AttributeModifier> attributeModifiers();

    @Api
    @NotNull
    @Unmodifiable
    Collection<@NotNull AttributeModifier> attributeModifiers(@NotNull EquipmentSlot slot);

    @Api
    @NotNull
    Collection<@NotNull AttributeModifier> attributeModifiers(@NotNull Attribute attribute);

    @Api
    @NotNull
    ItemBuilder attributeModifiers(@NotNull Collection<@NotNull AttributeModifier> attributeModifiers);

    @Api
    @NotNull
    ItemBuilder attributeModifier(@NotNull AttributeModifier attributeModifier);

    @Api
    @NotNull
    ItemBuilder attributeModifier(@NotNull Attribute attribute, @NotNull Key key, @NotNull EquipmentSlotGroup equipmentSlotGroup, double amount, @NotNull AttributeModifierOperation operation);

    @Api
    @NotNull
    ItemBuilder attributeModifier(@NotNull Object attribute, @NotNull Object key, @NotNull Object equipmentSlotGroup, double amount, @NotNull Object operation);

    @Api
    @NotNull
    ItemBuilder removeAttributeModifiers(@Nullable EquipmentSlot slot);

    @Api
    @NotNull
    ItemBuilder removeAttributeModifiers(@NotNull Attribute attribute);

    @Api
    @NotNull
    ItemBuilder removeAttributeModifier(@NotNull AttributeModifier modifier);

    @Api
    @NotNull
    ItemBuilder damage(int damage);

    @Api
    int damage();

    @Api
    @NotNull
    ItemBuilder enchant(@NotNull Enchantment enchant, int level);

    @Api
    default @NotNull ItemBuilder enchant(@NotNull Object enchantment, int level) {
        return enchant(Enchantment.of(enchantment), level);
    }

    @Api
    @NotNull
    ItemBuilder enchant(@NotNull Map<@NotNull Enchantment, @NotNull Integer> enchantments);

    @Api
    @NotNull
    ItemBuilder enchantments(@NotNull Map<@NotNull Enchantment, @NotNull Integer> enchantments);

    @Api
    @NotNull
    @Unmodifiable
    Map<@NotNull Enchantment, @NotNull Integer> enchantments();

    @Api
    @NotNull
    Component displayname();

    /**
     * Sets the displayname of the Item.
     *
     * @param displayname the display name
     * @return this builder
     * @deprecated Use {@link ItemBuilder#displayname(Component)}
     */
    @Api
    @Deprecated(forRemoval = true)
    default @NotNull ItemBuilder displayname(@Nullable String displayname) {
        if (displayname == null) {
            return displayname((Component) null);
        }
        return displayname(LegacyComponentSerializer.legacySection().deserialize(displayname));
    }

    /**
     * Displayname via this method will not start italic.
     *
     * @param displayname the displayname
     * @return this builder
     */
    @Api
    @NotNull
    ItemBuilder displayname(@Nullable Component displayname);

    /**
     * Displayname via this method will start italic.
     *
     * @param displayname the displayname
     * @return this builder
     */
    @Api
    @NotNull
    ItemBuilder displaynameRaw(@NotNull Component displayname);

    @Api
    @NotNull
    @Unmodifiable
    List<@NotNull Component> lore();

    @Api
    @NotNull
    ItemBuilder lore(@NotNull Component line);

    @Api
    @NotNull
    ItemBuilder lore(@NotNull Collection<@NotNull Component> lore);

    @Api
    @NotNull
    ItemBuilder lore(@NotNull Component... lines);

    @Api
    @NotNull
    ItemBuilder lore(@NotNull Component line, int index);

    /**
     * Adds lore.
     *
     * @param lines the lore
     * @return this builder
     * @deprecated Use {@link ItemBuilder#lore(Component...)}
     */
    @Api
    @Deprecated(forRemoval = true)
    ItemBuilder lore(String... lines);

    /**
     * Sets lore.
     *
     * @param lines the lore
     * @return this builder
     * @deprecated Use {@link ItemBuilder#lore(Collection)}
     */

    @Api
    @Deprecated(forRemoval = true)
    @NotNull
    ItemBuilder lores(@NotNull Collection<@NotNull String> lines);

    @Api
    @NotNull
    ItemBuilder setLore(@NotNull Collection<@NotNull Component> lore);

    @Api
    @NotNull
    ItemBuilder flag(@NotNull ItemFlag flag);

    @Api
    default @NotNull ItemBuilder flag(@NotNull Object flag) {
        return flag(ItemFlag.of(flag));
    }

    @Api
    @NotNull
    ItemBuilder flag(@NotNull Collection<@NotNull ?> flags);

    @Api
    @NotNull
    ItemBuilder flag(@NotNull ItemFlag @NotNull ... flags);

    @Api
    default @NotNull ItemBuilder flag(@NotNull Object @NotNull ... flags) {
        for (var flag : flags) {
            flag(flag);
        }
        return this;
    }

    @Api
    @NotNull
    @Deprecated(forRemoval = true)
    ItemBuilder setFlags(@NotNull Collection<@NotNull ?> flags);

    @Api
    @NotNull
    @Unmodifiable
    @Deprecated(forRemoval = true)
    List<@NotNull ItemFlag> flags();

    @Api
    @NotNull
    ItemBuilder unbreakable(boolean unbreakable);

    @Api
    @NotNull
    ItemBuilder hiddenUnbreakable();

    @Api
    @NotNull
    ItemBuilder hideJukeboxPlayableTooltip();

    @Api
    boolean unbreakable();

    @Api
    @NotNull
    ItemBuilder hideAdditionalTooltip();

    @Api
    @NotNull
    ItemBuilder hideTooltip();

    @Api
    @NotNull
    ItemBuilder intangibleProjectile();

    @Api
    @NotNull
    ItemBuilder glow(boolean glow);

    @Api
    boolean glow();

    @Api
    @NotNull
    ItemPersistentDataStorage persistentDataStorage();

    @Api
    @NotNull
    ItemBuilder clone();

    @Api
    int repairCost();

    @Api
    @NotNull
    ItemBuilder repairCost(int repairCost);

    @Api
    @Nullable
    ItemRarity rarity();

    @Api
    @NotNull
    ItemBuilder rarity(@Nullable ItemRarity rarity);

    @Api
    @NotNull
    ItemBuilder customModelData(int customModelData);

    @Api
    boolean hasCustomModelData();

    /**
     * Check first with {@link #hasCustomModelData()}. Returns 0 if no data is set.
     *
     * @return the custom model data
     */
    @Api
    int customModelData();

    @Api
    @NotNull
    JsonElement serialize();

    @Api
    @NotNull
    <T> T build();

    @Api
    default @NotNull Object buildSafe() {
        return build();
    }
}
