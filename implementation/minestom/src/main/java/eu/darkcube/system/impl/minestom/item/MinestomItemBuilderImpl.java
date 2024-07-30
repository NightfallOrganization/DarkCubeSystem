/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item;

import static eu.darkcube.system.minestom.item.flag.MinestomItemFlag.*;
import static eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport.adventureSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import eu.darkcube.system.impl.minestom.item.enchant.MinestomEnchantmentImpl;
import eu.darkcube.system.impl.minestom.item.firework.MinestomFireworkEffectImpl;
import eu.darkcube.system.impl.minestom.item.material.MinestomMaterialImpl;
import eu.darkcube.system.impl.server.item.AbstractItemBuilder;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.MinestomEquipmentSlotGroup;
import eu.darkcube.system.minestom.item.MinestomItemBuilder;
import eu.darkcube.system.minestom.item.attribute.MinestomAttribute;
import eu.darkcube.system.minestom.item.attribute.MinestomAttributeModifier;
import eu.darkcube.system.minestom.item.attribute.MinestomAttributeModifierOperation;
import eu.darkcube.system.minestom.item.enchant.MinestomEnchantment;
import eu.darkcube.system.minestom.item.flag.MinestomItemFlag;
import eu.darkcube.system.server.item.EquipmentSlotGroup;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeModifier;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
import eu.darkcube.system.server.item.meta.EnchantmentStorageBuilderMeta;
import eu.darkcube.system.server.item.meta.FireworkBuilderMeta;
import eu.darkcube.system.server.item.meta.LeatherArmorBuilderMeta;
import eu.darkcube.system.server.item.meta.SkullBuilderMeta;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.nbt.TagStringIOExt;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.AttributeList;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.item.component.DyedItemColor;
import net.minestom.server.item.component.EnchantmentList;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.item.component.Unbreakable;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.tag.Tag;

@SuppressWarnings({"UnstableApiUsage", "unchecked"})
public class MinestomItemBuilderImpl extends AbstractItemBuilder implements MinestomItemBuilder {
    private static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ItemStack.class, new TypeAdapter<ItemStack>() {
        @Override
        public void write(JsonWriter writer, ItemStack value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(TagStringIO.get().asString(value.toItemNBT()));
        }

        @Override
        public ItemStack read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            var tag = reader.nextString();
            return ItemStack.fromItemNBT(TagStringIO.get().asCompound(tag));
        }
    }).create();
    private static final Tag<String> TAG_DOCUMENT = Tag.String("darkcubesystem:persistent_data_storage");
    private static final Tag<Integer> REPAIR_COST = Tag.Integer("RepairCost").defaultValue(0);

    public MinestomItemBuilderImpl() {
    }

    public MinestomItemBuilderImpl(ItemStack item) {
        material(item.material());
        amount(item.amount());
        if (item.has(ItemComponent.REPAIR_COST)) {
            this.repairCost(Objects.requireNonNull(item.get(ItemComponent.REPAIR_COST)));
        }

        if (item.has(ItemComponent.UNBREAKABLE)) {
            var unbreakable = item.get(ItemComponent.UNBREAKABLE);
            if (!unbreakable.showInTooltip()) {
                flag(HIDE_UNBREAKABLE);
            }
            this.unbreakable(true);
        }
        if (item.has(ItemComponent.CUSTOM_NAME)) {
            this.displaynameRaw(adventureSupport().convert(Objects.requireNonNull(item.get(ItemComponent.CUSTOM_NAME))));
        }
        if (item.has(ItemComponent.ENCHANTMENTS)) {
            var enchantments = Objects.requireNonNull(item.get(ItemComponent.ENCHANTMENTS));
            if (!enchantments.showInTooltip()) {
                flag(HIDE_ENCHANTMENTS);
            }
            for (var entry : enchantments.enchantments().entrySet()) {
                this.enchant(entry.getKey(), entry.getValue());
            }
        }
        if (item.has(ItemComponent.ATTRIBUTE_MODIFIERS)) {
            var attributeList = Objects.requireNonNull(item.get(ItemComponent.ATTRIBUTE_MODIFIERS));
            if (!attributeList.showInTooltip()) {
                flag(HIDE_ATTRIBUTE_LIST);
            }
            for (var modifier : attributeList.modifiers()) {
                this.attributeModifier(modifier.attribute(), modifier.modifier().id(), modifier.slot(), modifier.modifier().amount(), modifier.modifier().operation());
            }
        }
        if (item.has(ItemComponent.LORE)) {
            var loreList = Objects.requireNonNull(item.get(ItemComponent.LORE));
            lore.addAll(adventureSupport().convertComponentsP2C(loreList));
        }
        if (item.has(ItemComponent.DAMAGE)) {
            this.damage(Objects.requireNonNull(item.get(ItemComponent.DAMAGE)));
        }
        if (item.has(ItemComponent.FIREWORK_EXPLOSION)) {
            var fireworkExplosion = Objects.requireNonNull(item.get(ItemComponent.FIREWORK_EXPLOSION));
            meta(FireworkBuilderMeta.class).fireworkEffect(fireworkExplosion);
        }
        if (item.has(ItemComponent.STORED_ENCHANTMENTS)) {
            var storedEnchants = Objects.requireNonNull(item.get(ItemComponent.STORED_ENCHANTMENTS));
            if (!storedEnchants.showInTooltip()) {
                flag(HIDE_STORED_ENCHANTMENTS);
            }
            meta(EnchantmentStorageBuilderMeta.class).enchantments(storedEnchants.enchantments());
        }
        if (item.has(ItemComponent.PROFILE)) {
            var profile = Objects.requireNonNull(item.get(ItemComponent.PROFILE));
            var owner = profile.name();
            var ownerId = profile.uuid();
            var skin = profile.skin();
            var meta = meta(SkullBuilderMeta.class);
            if (owner != null || skin != null || ownerId != null) {
                var texture = skin == null ? null : new SkullBuilderMeta.UserProfile.Texture(skin.textures(), skin.signature());
                meta.owningPlayer(new SkullBuilderMeta.UserProfile(owner, ownerId, texture));
            }
        }
        if (item.has(ItemComponent.DYED_COLOR)) {
            var color = Objects.requireNonNull(item.get(ItemComponent.DYED_COLOR));
            if (!color.showInTooltip()) {
                flag(MinestomItemFlag.HIDE_DYED_COLOR);
            }
            meta(LeatherArmorBuilderMeta.class).color(color.color());
        }
        if (item.has(ItemComponent.CUSTOM_DATA)) {
            var document = Objects.requireNonNull(item.get(ItemComponent.CUSTOM_DATA));
            var data = document.getTag(TAG_DOCUMENT);
            if (data != null) {
                storage.loadFromJsonObject(new Gson().fromJson(data, JsonObject.class));
            }
        }
        var b = build();
        if (!b.isSimilar(item)) {
            LOGGER.error("Failed to clone item correctly: ");
            LOGGER.error(" - {}", TagStringIOExt.writeTag(item.toItemNBT()));
            LOGGER.error(" - {}", TagStringIOExt.writeTag(b.toItemNBT()));
        }
    }

    public static MinestomItemBuilderImpl deserialize(JsonElement json) {
        return new MinestomItemBuilderImpl(gson.fromJson(json, ItemStack.class));
    }

    @Override
    public @NotNull ItemStack build() {
        var material = ((MinestomMaterialImpl) this.material).minestomType();
        var builder = ItemStack.builder(material);
        builder.amount(amount);
        if (repairCost.isPresent()) {
            builder.set(ItemComponent.REPAIR_COST, repairCost.getAsInt());
        }
        if (unbreakable.isPresent() && unbreakable.get()) {
            builder.set(ItemComponent.UNBREAKABLE, new Unbreakable(!flags.contains(HIDE_UNBREAKABLE)));
        }
        if (displayname != Component.empty()) {
            builder.set(ItemComponent.CUSTOM_NAME, adventureSupport().convert(displayname));
        }
        {
            var enchantmentMap = new HashMap<DynamicRegistry.Key<Enchantment>, Integer>();
            for (var entry : enchantments.entrySet()) {
                var enchantment = ((MinestomEnchantmentImpl) entry.getKey()).minestomType();
                var key = MinecraftServer.getEnchantmentRegistry().getKey(enchantment);
                enchantmentMap.put(key, entry.getValue());
            }
            var enchantmentList = new EnchantmentList(enchantmentMap, !flags.contains(HIDE_ENCHANTMENTS));
            if (!enchantmentList.enchantments().isEmpty()) {
                builder.set(ItemComponent.ENCHANTMENTS, enchantmentList);
            }
        }
        if (!lore.isEmpty()) {
            builder.set(ItemComponent.LORE, adventureSupport().convertComponentsC2P(lore));
        }
        if (glow.isPresent()) {
            builder.set(ItemComponent.ENCHANTMENT_GLINT_OVERRIDE, glow.get());
        }
        {
            var modifiers = new ArrayList<AttributeList.Modifier>();
            for (var modifier : attributeModifiers) {
                var m = ((MinestomAttributeModifier) modifier).minestomType();
                modifiers.add(m);
            }
            var attributeList = new AttributeList(modifiers, !flags.contains(HIDE_ATTRIBUTE_LIST));
            builder.set(ItemComponent.ATTRIBUTE_MODIFIERS, attributeList);
        }
        if (material.registry().prototype().has(ItemComponent.MAX_DAMAGE) && damage.isPresent()) {
            builder.set(ItemComponent.DAMAGE, damage.getAsInt());
        }

        for (var meta : metas) {
            switch (meta) {
                case FireworkBuilderMeta firework -> {
                    var cast = ((MinestomFireworkEffectImpl) firework.fireworkEffect());
                    if (cast != null) {
                        builder.set(ItemComponent.FIREWORK_EXPLOSION, cast.minestomType());
                    }
                }
                case SkullBuilderMeta skull -> {
                    var owner = skull.owningPlayer();
                    if (owner == null) break;
                    var name = owner.name();
                    var uuid = owner.uniqueId();
                    var texture = owner.texture();
                    var properties = texture == null ? List.<HeadProfile.Property>of() : List.of(new HeadProfile.Property("textures", texture.value(), texture.signature()));
                    builder.set(ItemComponent.PROFILE, new HeadProfile(name, uuid, properties));
                }
                case LeatherArmorBuilderMeta leatherArmor -> builder.set(ItemComponent.DYED_COLOR, new DyedItemColor(leatherArmor.color().rgb(), !flags.contains(HIDE_DYED_COLOR)));
                case EnchantmentStorageBuilderMeta enchantmentStorage -> {
                    var enchantments = new HashMap<DynamicRegistry.Key<Enchantment>, Integer>();
                    for (var entry : enchantmentStorage.enchantments().entrySet()) {
                        enchantments.put(MinecraftServer.getEnchantmentRegistry().getKey(((MinestomEnchantment) entry.getKey()).minestomType()), entry.getValue());
                    }
                    var enchantmentList = new EnchantmentList(enchantments, !flags.contains(HIDE_STORED_ENCHANTMENTS));
                    builder.set(ItemComponent.STORED_ENCHANTMENTS, enchantmentList);
                }
                case null, default -> throw new UnsupportedOperationException("Meta not supported for this mc version: " + meta);
            }
        }
        {
            var json = storage.storeToJsonObject();
            if (!json.isEmpty()) {
                builder.set(ItemComponent.CUSTOM_DATA, CustomData.EMPTY.withTag(TAG_DOCUMENT, json.toString()));
            }
        }
        return builder.build();
    }

    @Override
    public @NotNull MinestomItemBuilderImpl clone() {
        return new MinestomItemBuilderImpl(build());
    }

    @Override
    public @NotNull JsonElement serialize() {
        return gson.toJsonTree(build());
    }

    @Override
    public boolean canBeRepairedBy(ItemBuilder item) {
        return false; // Nothing can be repaired on minestom - there is no anvil
    }

    @Override
    public @NotNull ItemBuilder attributeModifier(@NotNull Attribute attribute, @NotNull Key key, @NotNull EquipmentSlotGroup equipmentSlotGroup, double amount, @NotNull AttributeModifierOperation operation) {
        var modifier = new net.minestom.server.entity.attribute.AttributeModifier(key.toString(), amount, ((MinestomAttributeModifierOperation) operation).minestomType());
        this.attributeModifier(AttributeModifier.of(new AttributeList.Modifier(((MinestomAttribute) attribute).minestomType(), modifier, ((MinestomEquipmentSlotGroup) equipmentSlotGroup).minestomType())));
        return this;
    }
}
