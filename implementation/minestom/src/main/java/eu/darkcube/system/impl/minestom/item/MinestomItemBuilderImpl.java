/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item;

import static net.minestom.server.item.Enchantment.INFINITY;
import static net.minestom.server.item.Enchantment.PROTECTION;
import static net.minestom.server.item.Material.BOW;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.logging.Logger;

import eu.darkcube.system.impl.minestom.adventure.AdventureUtils;
import eu.darkcube.system.impl.minestom.item.attribute.MinestomAttributeModifierImpl;
import eu.darkcube.system.impl.minestom.item.enchant.MinestomEnchantmentImpl;
import eu.darkcube.system.impl.minestom.item.firework.MinestomFireworkEffectImpl;
import eu.darkcube.system.impl.minestom.item.flag.MinestomItemFlagImpl;
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
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.MinestomItemBuilder;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.meta.EnchantmentStorageBuilderMeta;
import eu.darkcube.system.server.item.meta.FireworkBuilderMeta;
import eu.darkcube.system.server.item.meta.LeatherArmorBuilderMeta;
import eu.darkcube.system.server.item.meta.SkullBuilderMeta;
import net.minestom.server.color.Color;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.metadata.EnchantedBookMeta;
import net.minestom.server.item.metadata.FireworkEffectMeta;
import net.minestom.server.item.metadata.FireworkMeta;
import net.minestom.server.item.metadata.LeatherArmorMeta;
import net.minestom.server.item.metadata.PlayerHeadMeta;
import net.minestom.server.tag.Tag;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.parser.SNBTParser;

@SuppressWarnings("UnstableApiUsage")
public class MinestomItemBuilderImpl extends AbstractItemBuilder implements MinestomItemBuilder {
    private static final Logger LOGGER = Logger.getLogger("ItemBuilder");
    private static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ItemStack.class, new TypeAdapter<ItemStack>() {
        @Override
        public void write(JsonWriter writer, ItemStack value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value.toItemNBT().toSNBT());
        }

        @Override
        public ItemStack read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            var tag = reader.nextString();
            NBT nbt;
            try {
                nbt = new SNBTParser(new StringReader(tag)).parse();
            } catch (NBTException e) {
                throw new RuntimeException(e);
            }
            return ItemStack.fromItemNBT((NBTCompound) nbt);
        }
    }).create();
    private static final Tag<String> TAG_DOCUMENT = Tag.String("darkcubesystem:persistent_data_storage");
    private static final Tag<Integer> REPAIR_COST = Tag.Integer("RepairCost").defaultValue(0);

    public MinestomItemBuilderImpl() {
    }

    public MinestomItemBuilderImpl(ItemStack item) {
        var meta = item.meta();
        material(item.material());
        amount(item.amount());
        repairCost(meta.getTag(REPAIR_COST));

        unbreakable(meta.isUnbreakable());
        if (meta.getDisplayName() != null) {
            displaynameRaw(AdventureUtils.convert(meta.getDisplayName()));
        }
        for (var entry : meta.getEnchantmentMap().entrySet()) {
            enchant(entry.getKey(), entry.getValue());
        }
        for (var attribute : meta.getAttributes()) {
            attributeModifier(attribute.attribute(), attribute);
        }
        for (var flag : ItemHideFlag.values()) {
            if ((meta.getHideFlag() & flag.getBitFieldPart()) == flag.getBitFieldPart()) {
                flag(flag);
            }
        }
        lore.addAll(AdventureUtils.convertComponentsBack(meta.getLore()));
        damage(meta.getDamage());
        {
            var fireworkEffect = item.meta(FireworkEffectMeta.class).getFireworkEffect();
            if (fireworkEffect != null) meta(FireworkBuilderMeta.class).fireworkEffect(fireworkEffect);
        }
        {
            var enchantments = item.meta(EnchantedBookMeta.class).getStoredEnchantmentMap();
            meta(EnchantmentStorageBuilderMeta.class).enchantments(enchantments);
        }
        {
            var playerHeadMeta = item.meta(PlayerHeadMeta.class);
            var owner = playerHeadMeta.getSkullOwner();
            var skin = playerHeadMeta.getPlayerSkin();
            var skullMeta = meta(SkullBuilderMeta.class);
            if (owner != null || skin != null) {
                var texture = skin == null ? null : new SkullBuilderMeta.UserProfile.Texture(skin.textures(), skin.signature());
                skullMeta.owningPlayer(new SkullBuilderMeta.UserProfile(null, owner, texture));
            }
        }
        {
            var color = item.meta(LeatherArmorMeta.class).getColor();
            if (color != null) meta(LeatherArmorBuilderMeta.class).color(new eu.darkcube.system.util.Color(color.asRGB()));
        }
        if (meta.hasTag(TAG_DOCUMENT)) {
            var data = meta.getTag(TAG_DOCUMENT);
            if (data != null) {
                storage.loadFromJsonObject(new Gson().fromJson(data, JsonObject.class));
            }
        }
        var b = build();
        if (!b.isSimilar(item)) {
            LOGGER.severe("Failed to clone item correctly: ");
            LOGGER.severe(" - " + item.toItemNBT().toSNBT());
            LOGGER.severe(" - " + b.toItemNBT().toSNBT());
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
        builder.meta(meta -> {
            if (repairCost != 0) {
                meta.setTag(REPAIR_COST, repairCost);
            }
            if (unbreakable) {
                meta.unbreakable(true);
            }
            if (displayname != Component.empty()) {
                meta.displayName(AdventureUtils.convert(displayname));
            }
            for (var entry : enchantments.entrySet()) {
                meta.enchantment(((MinestomEnchantmentImpl) entry.getKey()).minestomType(), entry.getValue().shortValue());
            }

            if (!flags.isEmpty()) {
                meta.hideFlag(flags.stream().map(flag -> ((MinestomItemFlagImpl) flag).minestomType()).toArray(ItemHideFlag[]::new));
            }
            if (!lore.isEmpty()) meta.lore(lore.stream().map(AdventureUtils::convert).toList());
            if (glow) {
                if (enchantments.isEmpty()) {
                    meta.enchantment(material == BOW ? PROTECTION : INFINITY, (short) 1);
                    meta.hideFlag(ItemHideFlag.HIDE_ENCHANTS);
                }
            }
            attributeModifiers.forEach((attribute, modifiers) -> meta.attributes(modifiers.stream().map(modifier -> ((MinestomAttributeModifierImpl) modifier).minestomType()).toList()));
            if (material.registry().maxDamage() != 0 && damage != 0) {
                meta.damage(damage);
            }
            for (var builderMeta : metas) {
                switch (builderMeta) {
                    case FireworkBuilderMeta fireworkBuilderMeta -> builder.meta(FireworkMeta.class, m -> {
                        var cast = ((MinestomFireworkEffectImpl) fireworkBuilderMeta.fireworkEffect());
                        if (cast != null) {
                            m.effects(Collections.singletonList(cast.minestomType()));
                        }
                    });
                    case SkullBuilderMeta skullBuilderMeta -> builder.meta(PlayerHeadMeta.class, m -> {
                        var owner = skullBuilderMeta.owningPlayer();
                        if (owner == null) return;
                        var texture = owner.texture();
                        if (texture != null) {
                            m.playerSkin(new PlayerSkin(texture.value(), texture.signature()));
                        }
                        m.skullOwner(owner.uniqueId());
                    });
                    case LeatherArmorBuilderMeta leatherArmorBuilderMeta -> builder.meta(LeatherArmorMeta.class, m -> m.color(new Color(leatherArmorBuilderMeta.color().rgb())));
                    case EnchantmentStorageBuilderMeta enchantmentStorageBuilderMeta -> builder.meta(EnchantedBookMeta.class, m -> {
                        for (var entry : enchantmentStorageBuilderMeta.enchantments().entrySet()) {
                            m.enchantment(((MinestomEnchantmentImpl) entry.getKey()).minestomType(), entry.getValue().shortValue());
                        }
                    });
                    case null, default -> throw new UnsupportedOperationException("Meta not supported for this mc version: " + builderMeta);
                }
            }
            var json = storage.storeToJsonObject();
            if (!json.isEmpty()) {
                meta.setTag(TAG_DOCUMENT, json.toString());
            }
        });
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
}
