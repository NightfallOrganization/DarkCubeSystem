/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import eu.darkcube.system.impl.server.item.storage.BasicItemPersistentDataStorage;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.StringBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.data.component.DataComponent;
import eu.darkcube.system.server.data.component.DataComponentMap;
import eu.darkcube.system.server.item.EquipmentSlot;
import eu.darkcube.system.server.item.EquipmentSlotGroup;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.ItemRarity;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeModifier;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
import eu.darkcube.system.server.item.component.components.AttributeList;
import eu.darkcube.system.server.item.component.components.EnchantmentList;
import eu.darkcube.system.server.item.component.components.Unbreakable;
import eu.darkcube.system.server.item.enchant.Enchantment;
import eu.darkcube.system.server.item.flag.ItemFlag;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.util.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("removal")
public abstract class AbstractItemBuilder implements ItemBuilder {
    protected static final String KEY_DOCUMENT = "darkcubesystem:persistent_data_storage";
    protected static final Logger LOGGER = LoggerFactory.getLogger("ItemBuilder");
    protected static final Gson STORAGE_GSON = new Gson();
    protected final DataComponentMap components = DataComponentMap.create();
    protected @NotNull Material material = Material.air();
    protected int amount = 1;
    protected BasicItemPersistentDataStorage storage = new BasicItemPersistentDataStorage(this);

    protected void loadPersistentDataStorage() {
        var customData = get(CUSTOM_DATA);
        if (customData == null) return;
        var tag = (StringBinaryTag) customData.get(KEY_DOCUMENT);
        if (tag == null) return;
        storage.loadFromJsonObject(STORAGE_GSON.fromJson(tag.value(), JsonObject.class));
    }

    protected void savePersistentDataStorage() {
        var json = storage.storeToJsonObject();
        var data = get(CUSTOM_DATA);
        if (!json.isEmpty()) {
            if (data == null) data = CompoundBinaryTag.empty();
            data = data.putString(KEY_DOCUMENT, json.toString());
            set(CUSTOM_DATA, data);
        } else {
            if (data != null) {
                data = data.remove(KEY_DOCUMENT);
                set(CUSTOM_DATA, data);
            }
        }
    }

    @Override
    public boolean has(@NotNull DataComponent<?> component) {
        return components.has(component);
    }

    @Override
    public <T> @Nullable T get(@NotNull DataComponent<T> component) {
        return components.get(component);
    }

    @Override
    public @NotNull <T> AbstractItemBuilder set(@NotNull DataComponent<T> component, @NotNull T value) {
        components.set(component, value);
        return this;
    }

    @Override
    public @NotNull <T> AbstractItemBuilder remove(@NotNull DataComponent<T> component) {
        components.remove(component);
        return this;
    }

    @Override
    public @NotNull <T> AbstractItemBuilder map(@NotNull DataComponent<T> component, @NotNull Function<T, T> mapper) {
        var data = get(component);
        if (data != null) set(component, mapper.apply(data));
        return this;
    }

    @Override
    public @NotNull <T> AbstractItemBuilder mapNotNull(@NotNull DataComponent<T> component, @NotNull Function<T, T> mapper) {
        var data = Objects.requireNonNull(get(component), "Item doesn't have the component " + component.key().asMinimalString());
        set(component, mapper.apply(data));
        return this;
    }

    @Override
    public @NotNull Material material() {
        return material;
    }

    @Override
    public @NotNull AbstractItemBuilder material(@NotNull Material material) {
        this.material = material;
        return this;
    }

    @Override
    public int amount() {
        return amount;
    }

    @Override
    public @NotNull AbstractItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public @NotNull Collection<@NotNull AttributeModifier> attributeModifiers() {
        var modifiers = get(ATTRIBUTE_MODIFIERS);
        if (modifiers == null) return List.of();
        return modifiers.modifiers();
    }

    @Override
    public @NotNull Collection<@NotNull AttributeModifier> attributeModifiers(@NotNull EquipmentSlot slot) {
        Collection<@NotNull AttributeModifier> result = new ArrayList<>();
        for (var modifier : attributeModifiers()) {
            var group = modifier.equipmentSlotGroup();
            if (group.slots().contains(slot)) {
                result.add(modifier);
            }
        }
        return List.copyOf(result);
    }

    @Override
    public @NotNull Collection<AttributeModifier> attributeModifiers(@NotNull Attribute attribute) {
        return attributeModifiers().stream().filter(m -> m.attribute().equals(attribute)).toList();
    }

    @Override
    public @NotNull AbstractItemBuilder attributeModifiers(@NotNull Collection<@NotNull AttributeModifier> attributeModifiers) {
        var modifiers = get(ATTRIBUTE_MODIFIERS);
        return set(ATTRIBUTE_MODIFIERS, new AttributeList(List.copyOf(attributeModifiers), modifiers == null || modifiers.showInTooltip()));
    }

    @Override
    public @NotNull AbstractItemBuilder attributeModifier(@NotNull AttributeModifier attributeModifier) {
        var modifiers = get(ATTRIBUTE_MODIFIERS);
        if (modifiers == null) return attributeModifiers(List.of(attributeModifier));
        var list = new ArrayList<>(modifiers.modifiers());
        list.add(attributeModifier);
        return attributeModifiers(list);
    }

    @Override
    public @NotNull ItemBuilder attributeModifier(@NotNull Attribute attribute, @NotNull Key key, @NotNull EquipmentSlotGroup equipmentSlotGroup, double amount, @NotNull AttributeModifierOperation operation) {
        return attributeModifier(AttributeModifier.of(attribute, key, equipmentSlotGroup, amount, operation));
    }

    @Override
    public @NotNull ItemBuilder attributeModifier(@NotNull Object attribute, @NotNull Object key, @NotNull Object equipmentSlotGroup, double amount, @NotNull Object operation) {
        return attributeModifier(Attribute.of(attribute), KeyProvider.get(key), EquipmentSlotGroup.of(equipmentSlotGroup), amount, AttributeModifierOperation.of(operation));
    }

    @Override
    public @NotNull AbstractItemBuilder removeAttributeModifier(@NotNull AttributeModifier attributeModifier) {
        var attributes = get(ATTRIBUTE_MODIFIERS);
        if (attributes == null) return this;
        if (!attributes.modifiers().contains(attributeModifier)) return this;
        var list = new ArrayList<>(attributes.modifiers());
        list.remove(attributeModifier);
        return set(ATTRIBUTE_MODIFIERS, new AttributeList(list, attributes.showInTooltip()));
    }

    @Override
    public @NotNull AbstractItemBuilder removeAttributeModifiers(@Nullable EquipmentSlot slot) {
        var attributes = get(ATTRIBUTE_MODIFIERS);
        if (attributes == null) return this;
        var list = new ArrayList<>(attributes.modifiers());
        list.removeIf(a -> a.equipmentSlotGroup().slots().contains(slot));
        if (attributes.modifiers().size() == list.size()) return this;
        return set(ATTRIBUTE_MODIFIERS, new AttributeList(list, attributes.showInTooltip()));
    }

    @Override
    public @NotNull AbstractItemBuilder removeAttributeModifiers(@NotNull Attribute attribute) {
        var attributes = get(ATTRIBUTE_MODIFIERS);
        if (attributes == null) return this;
        var list = new ArrayList<>(attributes.modifiers());
        list.removeIf(a -> a.attribute().equals(attribute));
        if (attributes.modifiers().size() == list.size()) return this;
        return set(ATTRIBUTE_MODIFIERS, new AttributeList(list, attributes.showInTooltip()));
    }

    @Override
    public @NotNull AbstractItemBuilder damage(int damage) {
        return set(DAMAGE, damage);
    }

    @Override
    public int damage() {
        return get(DAMAGE, 0);
    }

    @Override
    public @NotNull AbstractItemBuilder enchant(@NotNull Enchantment enchantment, int level) {
        var enchantments = get(ENCHANTMENTS, EnchantmentList.EMPTY);
        return set(ENCHANTMENTS, enchantments.with(enchantment.key(), level));
    }

    @Override
    public @NotNull AbstractItemBuilder enchant(@NotNull Map<Enchantment, Integer> enchantments) {
        var e = get(ENCHANTMENTS, EnchantmentList.EMPTY);
        return set(ENCHANTMENTS, e.withEnchantments(enchantments));
    }

    @Override
    public @NotNull AbstractItemBuilder enchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        var e = get(ENCHANTMENTS, EnchantmentList.EMPTY);
        return set(ENCHANTMENTS, new EnchantmentList(Map.of(), e.showInTooltip()).withEnchantments(enchantments));
    }

    @Override
    public @NotNull Map<Enchantment, Integer> enchantments() {
        return get(ENCHANTMENTS, EnchantmentList.EMPTY).getEnchantments();
    }

    @Override
    public @NotNull Component displayname() {
        return get(CUSTOM_NAME, Component.empty());
    }

    @Override
    public @NotNull AbstractItemBuilder displayname(@Nullable Component displayname) {
        if (displayname == null) return remove(CUSTOM_NAME);
        return set(CUSTOM_NAME, Component.empty().decoration(TextDecoration.ITALIC, false).append(displayname));
    }

    @Override
    public @NotNull AbstractItemBuilder displaynameRaw(@NotNull Component displayname) {
        return set(CUSTOM_NAME, displayname);
    }

    @Override
    public @NotNull List<Component> lore() {
        return get(LORE, List.of());
    }

    @Override
    public @NotNull AbstractItemBuilder lore(@NotNull Component line) {
        return lore(List.of(line));
    }

    @Override
    public @NotNull AbstractItemBuilder lore(@NotNull Collection<Component> lore) {
        var l = new ArrayList<>(lore());
        for (var component : lore) {
            l.add(mapLore(component));
        }
        return set(LORE, l);
    }

    private Component mapLore(Component component) {
        if (component.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) {
            return component.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        } else {
            return component;
        }
    }

    @Override
    public @NotNull AbstractItemBuilder lore(Component... lines) {
        return lore(Arrays.asList(lines));
    }

    @Override
    public @NotNull AbstractItemBuilder lore(@NotNull Component line, int index) {
        var l = new ArrayList<>(lore());
        l.add(index, mapLore(line));
        return set(LORE, l);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated(forRemoval = true)
    public AbstractItemBuilder lore(String... lines) {
        var list = Arrays.stream(lines).<Component>map(LegacyComponentSerializer.legacySection()::deserialize).toList();
        return lore(list);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated(forRemoval = true)
    public @NotNull AbstractItemBuilder lores(@NotNull Collection<String> lines) {
        return remove(LORE).lore(lines.toArray(String[]::new));
    }

    @Override
    public @NotNull AbstractItemBuilder setLore(@NotNull Collection<Component> lore) {
        return remove(LORE).lore(lore);
    }

    @Override
    public @NotNull AbstractItemBuilder flag(@NotNull ItemFlag flag) {
        flag.apply(this);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder flag(@NotNull Collection<?> flags) {
        for (var flag : flags) {
            flag(flag);
        }
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder flag(ItemFlag @NotNull ... flags) {
        for (var flag : flags) {
            flag(flag);
        }
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder setFlags(@NotNull Collection<?> flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull List<ItemFlag> flags() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull AbstractItemBuilder hideJukeboxPlayableTooltip() {
        return map(JUKEBOX_PLAYABLE, p -> p.withTooltip(false));
    }

    @Override
    public @NotNull AbstractItemBuilder unbreakable(boolean unbreakable) {
        if (!unbreakable) {
            return remove(UNBREAKABLE);
        }
        if (has(UNBREAKABLE)) return this;
        return set(UNBREAKABLE, new Unbreakable(true));
    }

    @Override
    public @NotNull ItemBuilder hiddenUnbreakable() {
        return set(UNBREAKABLE, new Unbreakable(false));
    }

    @Override
    public boolean unbreakable() {
        return has(UNBREAKABLE);
    }

    @Override
    public @NotNull AbstractItemBuilder hideAdditionalTooltip() {
        set(HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder hideTooltip() {
        set(HIDE_TOOLTIP, Unit.INSTANCE);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder intangibleProjectile() {
        set(INTANGIBLE_PROJECTILE, Unit.INSTANCE);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder glow(boolean glow) {
        return set(ENCHANTMENT_GLINT_OVERRIDE, glow);
    }

    @Override
    public boolean glow() {
        return has(ENCHANTMENT_GLINT_OVERRIDE);
    }

    @Override
    public @NotNull BasicItemPersistentDataStorage persistentDataStorage() {
        return storage;
    }

    @Override
    public int repairCost() {
        return get(REPAIR_COST, -1);
    }

    @Override
    public @NotNull AbstractItemBuilder repairCost(int repairCost) {
        return set(REPAIR_COST, repairCost);
    }

    @NotNull
    @Override
    public AbstractItemBuilder customModelData(int customModelData) {
        return set(CUSTOM_MODEL_DATA, customModelData);
    }

    @Override
    public boolean hasCustomModelData() {
        return has(CUSTOM_MODEL_DATA);
    }

    @Override
    public int customModelData() {
        return get(CUSTOM_MODEL_DATA, Integer.MAX_VALUE);
    }

    @Override
    public ItemRarity rarity() {
        return get(RARITY);
    }

    @NotNull
    @Override
    public ItemBuilder rarity(@Nullable ItemRarity rarity) {
        if (rarity == null) return remove(RARITY);
        return set(RARITY, rarity);
    }

    @Override
    public <T> @NotNull T build() {
        savePersistentDataStorage();
        return build0();
    }

    protected abstract <T> @NotNull T build0();

    @Override
    public abstract @NotNull ItemBuilder clone();
}
