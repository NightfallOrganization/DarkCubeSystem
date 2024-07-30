/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.item;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Consumer;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.EquipmentSlot;
import eu.darkcube.system.server.item.EquipmentSlotGroup;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.ItemRarity;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeModifier;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
import eu.darkcube.system.server.item.enchant.Enchantment;
import eu.darkcube.system.server.item.flag.ItemFlag;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.server.item.meta.BuilderMeta;
import eu.darkcube.system.server.item.storage.BasicItemPersistentDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractItemBuilder implements ItemBuilder {
    protected static final Logger LOGGER = LoggerFactory.getLogger("ItemBuilder");
    protected @NotNull Material material = Material.air();
    protected int amount = 1;
    protected OptionalInt damage = OptionalInt.empty();
    protected @NotNull Set<BuilderMeta> metas = new HashSet<>();
    protected @NotNull Map<Enchantment, Integer> enchantments = new HashMap<>();
    protected @NotNull Component displayname = Component.empty();
    protected @NotNull List<Component> lore = new ArrayList<>();
    protected @NotNull List<ItemFlag> flags = new ArrayList<>();
    protected Optional<Boolean> unbreakable = Optional.empty();
    protected Optional<Boolean> glow = Optional.empty();
    protected OptionalInt customModelData = OptionalInt.empty();
    protected OptionalInt repairCost = OptionalInt.empty();
    protected ItemRarity rarity = null;
    protected @NotNull List<@NotNull AttributeModifier> attributeModifiers = new ArrayList<>();

    protected BasicItemPersistentDataStorage storage = new BasicItemPersistentDataStorage(this);

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
        return List.copyOf(attributeModifiers);
    }

    @Override
    public @NotNull Collection<@NotNull AttributeModifier> attributeModifiers(@NotNull EquipmentSlot slot) {
        Collection<@NotNull AttributeModifier> result = new ArrayList<>();
        for (var modifier : attributeModifiers) {
            var group = modifier.equipmentSlotGroup();
            if (group.slots().contains(slot)) {
                result.add(modifier);
            }
        }
        return List.copyOf(result);
    }

    @Override
    public @NotNull Collection<AttributeModifier> attributeModifiers(@NotNull Attribute attribute) {
        return attributeModifiers.stream().filter(m -> m.attribute().equals(attribute)).toList();
    }

    @Override
    public @NotNull AbstractItemBuilder attributeModifiers(@NotNull Collection<@NotNull AttributeModifier> attributeModifiers) {
        this.attributeModifiers.clear();
        this.attributeModifiers.addAll(attributeModifiers);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder attributeModifier(@NotNull AttributeModifier attributeModifier) {
        this.attributeModifiers.add(attributeModifier);
        return this;
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
        this.attributeModifiers.remove(attributeModifier);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder removeAttributeModifiers(@Nullable EquipmentSlot slot) {
        this.attributeModifiers.removeIf(a -> a.equipmentSlotGroup().slots().contains(slot));
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder removeAttributeModifiers(@NotNull Attribute attribute) {
        this.attributeModifiers.removeIf(a -> a.attribute().equals(attribute));
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder damage(int damage) {
        this.damage = OptionalInt.of(damage);
        return this;
    }

    @Override
    public int damage() {
        return damage.orElse(0);
    }

    @Override
    public @NotNull AbstractItemBuilder enchant(@NotNull Enchantment enchant, int level) {
        enchantments.put(enchant, level);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder enchant(@NotNull Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder enchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        this.enchantments.clear();
        this.enchantments.putAll(enchantments);
        return this;
    }

    @Override
    public @NotNull Map<Enchantment, Integer> enchantments() {
        return Map.copyOf(enchantments);
    }

    @Override
    public @NotNull Component displayname() {
        return displayname;
    }

    @Override
    public @NotNull AbstractItemBuilder displayname(@Nullable Component displayname) {
        this.displayname = displayname == null ? Component.empty() : Component.empty().decoration(TextDecoration.ITALIC, false).append(displayname);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder displaynameRaw(@NotNull Component displayname) {
        this.displayname = displayname;
        return this;
    }

    @Override
    public @NotNull List<Component> lore() {
        return List.copyOf(lore);
    }

    @Override
    public @NotNull AbstractItemBuilder lore(@NotNull Component line) {
        lore.add(Component.empty().decoration(TextDecoration.ITALIC, false).append(line));
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder lore(@NotNull Collection<Component> lore) {
        for (var component : lore) {
            lore(component);
        }
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder lore(Component... lines) {
        return lore(Arrays.asList(lines));
    }

    @Override
    public @NotNull AbstractItemBuilder lore(@NotNull Component line, int index) {
        lore.add(index, Component.empty().decoration(TextDecoration.ITALIC, false).append(line));
        return this;
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated(forRemoval = true)
    public AbstractItemBuilder lore(String... lines) {
        for (var line : lines) {
            lore(LegacyComponentSerializer.legacySection().deserialize(line));
        }
        return this;
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated(forRemoval = true)
    public @NotNull AbstractItemBuilder lores(@NotNull Collection<String> lines) {
        lore.clear();
        lore(lines.toArray(new String[0]));
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder setLore(@NotNull Collection<Component> lore) {
        this.lore.clear();
        lore(lore);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder flag(@NotNull ItemFlag flag) {
        flags.add(flag);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder flag(@NotNull Collection<?> flags) {
        this.flags.addAll(flags.stream().map(ItemFlag::of).toList());
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder flag(ItemFlag @NotNull ... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder setFlags(@NotNull Collection<?> flags) {
        this.flags.clear();
        flag(flags);
        return this;
    }

    @Override
    public @NotNull List<ItemFlag> flags() {
        return Collections.unmodifiableList(flags);
    }

    @Override
    public @NotNull AbstractItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = Optional.of(unbreakable);
        return this;
    }

    @Override
    public boolean unbreakable() {
        return unbreakable.orElse(false);
    }

    @Override
    public @NotNull AbstractItemBuilder glow(boolean glow) {
        this.glow = Optional.of(glow);
        return this;
    }

    @Override
    public boolean glow() {
        return glow.orElse(false);
    }

    @Override
    public @NotNull BasicItemPersistentDataStorage persistentDataStorage() {
        return storage;
    }

    @Override
    public <T extends BuilderMeta> @NotNull T meta(@NotNull Class<T> clazz) {
        if (clazz.getSuperclass() == Object.class) {
            for (var existing : metas) {
                if (clazz.equals(existing.getClass())) {
                    return clazz.cast(existing);
                }
            }
            try {
                var constructor = clazz.getConstructor();
                constructor.setAccessible(true);
                var t = constructor.newInstance();
                metas.add(t);
                return t;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Invalid BuilderMeta", e);
            }
        }
        throw new ClassCastException("Invalid BuilderMeta");
    }

    @Override
    public @NotNull <T extends BuilderMeta> AbstractItemBuilder meta(@NotNull Class<T> clazz, @NotNull Consumer<@NotNull T> meta) {
        var m = meta(clazz);
        meta.accept(m);
        return this;
    }

    @Override
    public @NotNull AbstractItemBuilder meta(@NotNull BuilderMeta meta) {
        metas.removeIf(m -> m.getClass().equals(meta.getClass()));
        metas.add(meta);
        return this;
    }

    @Override
    public @NotNull Set<BuilderMeta> metas() {
        return Set.copyOf(metas);
    }

    @Override
    public @NotNull AbstractItemBuilder metas(@NotNull Set<BuilderMeta> metas) {
        this.metas.clear();
        metas.stream().map(BuilderMeta::clone).forEach(this::meta);
        return this;
    }

    @Override
    public int repairCost() {
        return repairCost.orElse(-1);
    }

    @Override
    public @NotNull AbstractItemBuilder repairCost(int repairCost) {
        this.repairCost = OptionalInt.of(repairCost);
        return this;
    }

    @NotNull
    @Override
    public AbstractItemBuilder customModelData(int customModelData) {
        this.customModelData = OptionalInt.of(customModelData);
        return this;
    }

    @Override
    public boolean hasCustomModelData() {
        return customModelData.isPresent();
    }

    @Override
    public int customModelData() {
        return customModelData.orElse(Integer.MAX_VALUE);
    }

    @Override
    public ItemRarity rarity() {
        return rarity;
    }

    @NotNull
    @Override
    public ItemBuilder rarity(@NotNull ItemRarity rarity) {
        this.rarity = rarity;
        return this;
    }

    @Override
    public abstract @NotNull ItemBuilder clone();
}
