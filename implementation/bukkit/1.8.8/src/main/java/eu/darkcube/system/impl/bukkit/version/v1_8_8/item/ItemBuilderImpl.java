/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.darkcube.system.bukkit.item.BukkitItemBuilder;
import eu.darkcube.system.bukkit.util.ReflectionUtils;
import eu.darkcube.system.bukkit.util.ReflectionUtils.PackageType;
import eu.darkcube.system.impl.bukkit.item.enchant.BukkitEnchantmentImpl;
import eu.darkcube.system.impl.bukkit.item.firework.BukkitFireworkEffectImpl;
import eu.darkcube.system.impl.bukkit.item.flag.BukkitItemFlagImpl;
import eu.darkcube.system.impl.bukkit.item.material.BukkitMaterialImpl;
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
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.meta.FireworkBuilderMeta;
import eu.darkcube.system.server.item.meta.LeatherArmorBuilderMeta;
import eu.darkcube.system.server.item.meta.SkullBuilderMeta;
import eu.darkcube.system.server.item.meta.SpawnEggBuilderMeta;
import eu.darkcube.system.util.Color;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilderImpl extends AbstractItemBuilder implements BukkitItemBuilder {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ItemStack.class, new TypeAdapter<ItemStack>() {
        @Override
        public void write(JsonWriter writer, ItemStack value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            var nms = CraftItemStack.asNMSCopy(value);
            var nbt = new NBTTagCompound();
            nms.save(nbt);
            writer.value(nbt.toString());
        }

        @Override
        public ItemStack read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            var tag = reader.nextString();
            NBTTagCompound nbt;
            try {
                nbt = MojangsonParser.parse(tag);
            } catch (MojangsonParseException e) {
                throw new RuntimeException(e);
            }
            var nbtItem = net.minecraft.server.v1_8_R3.ItemStack.createStack(nbt);
            return CraftItemStack.asBukkitCopy(nbtItem);
        }
    }).create();
    private static final Field CraftMetaSkull$profile = ReflectionUtils.getField("CraftMetaSkull", PackageType.CRAFTBUKKIT_INVENTORY, true, "profile");

    public ItemBuilderImpl() {
    }

    public ItemBuilderImpl(ItemStack item) {
        material(item.getType());
        amount(item.getAmount());
        if (item.hasItemMeta()) {
            var meta = item.getItemMeta();
            unbreakable(meta.spigot().isUnbreakable());
            var displayname = meta.getDisplayName();
            if (displayname != null) displayname(LegacyComponentSerializer.legacySection().deserialize(displayname));
            for (var e : meta.getEnchants().entrySet()) {
                enchant(e.getKey(), e.getValue());
            }
            setFlags(meta.getItemFlags().stream().map(eu.darkcube.system.server.item.flag.ItemFlag::of).toList());
            lore(meta.hasLore() ? meta.getLore().stream().map(LegacyComponentSerializer.legacySection()::deserialize).collect(Collectors.toList()) : new ArrayList<>());
            damage(item.getDurability());
            if (meta instanceof FireworkEffectMeta fireworkEffectMeta) meta(FireworkBuilderMeta.class).fireworkEffect(fireworkEffectMeta.getEffect());
            if (meta instanceof SkullMeta) {
                var pp = (GameProfile) ReflectionUtils.getValue(meta, CraftMetaSkull$profile);
                if (pp != null) {
                    var prop = pp.getProperties().containsKey("textures") ? pp.getProperties().get("textures").stream().findFirst().orElse(null) : null;
                    var texture = prop == null ? null : new SkullBuilderMeta.UserProfile.Texture(prop.getValue(), prop.getSignature());
                    var up = new SkullBuilderMeta.UserProfile(pp.getName(), pp.getId(), texture);
                    meta(SkullBuilderMeta.class).owningPlayer(up);
                }
            }
            if (meta instanceof LeatherArmorMeta leatherArmorMeta) meta(LeatherArmorBuilderMeta.class).color(new Color(leatherArmorMeta.getColor().asRGB()));
            var mci = CraftItemStack.asNMSCopy(item);
            var tag = mci.getTag();
            if (tag != null) {
                if (tag.hasKey("System:persistentDataStorage")) {
                    var s = tag.getString("System:persistentDataStorage");
                    tag.remove("System:persistentDataStorage");
                    tag.setString("system:persistent_data_storage", s);
                }
                if (tag.hasKey("system:persistent_data_storage")) {
                    storage.loadFromJsonObject(new Gson().fromJson(tag.getString("system:persistent_data_storage"), JsonObject.class));
                }

                if (item.getType() == Material.MONSTER_EGG && tag.hasKey("EntityTag")) {
                    meta(SpawnEggBuilderMeta.class).entityTag(tag.getCompound("EntityTag").toString());
                }
            }
        }
    }

    public static ItemBuilderImpl deserialize(JsonElement json) {
        return new ItemBuilderImpl(gson.fromJson(json, ItemStack.class));
    }

    @Override
    public int repairCost() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull AbstractItemBuilder repairCost(int repairCost) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull AbstractItemBuilder clone() {
        return new ItemBuilderImpl(build());
    }

    @Override
    public boolean canBeRepairedBy(ItemBuilder item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ItemStack build() {
        var item = new ItemStack(((BukkitMaterialImpl) material).bukkitType());
        item.setAmount(amount);
        item.setDurability((short) damage);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.spigot().setUnbreakable(unbreakable);
            if (displayname != Component.empty()) meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(displayname));
            for (var e : enchantments.entrySet()) {
                meta.addEnchant(((BukkitEnchantmentImpl) e.getKey()).bukkitType(), e.getValue(), true);
            }
            meta.addItemFlags(flags.stream().map(flag -> ((BukkitItemFlagImpl) flag).bukkitType()).toArray(ItemFlag[]::new));
            List<String> lore = new ArrayList<>();
            for (var loreComponent : this.lore) {
                var l = LegacyComponentSerializer.legacySection().serialize(loreComponent);
                String last = null;
                for (var line : l.split("\\R")) {
                    if (last != null) line = last + line;
                    last = ChatColor.getLastColors(line);
                    lore.add(line);
                }
            }
            meta.setLore(lore);
            if (glow) {
                if (enchantments.isEmpty()) {
                    meta.addEnchant(item.getType() == Material.BOW ? Enchantment.PROTECTION_ENVIRONMENTAL : Enchantment.ARROW_INFINITE, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
            }
            for (var builderMeta : metas) {
                switch (builderMeta) {
                    case FireworkBuilderMeta fireworkBuilderMeta -> {
                        var bukkitFirework = ((BukkitFireworkEffectImpl) fireworkBuilderMeta.fireworkEffect());
                        ((FireworkEffectMeta) meta).setEffect(bukkitFirework == null ? null : bukkitFirework.bukkitType());
                    }
                    case SkullBuilderMeta skullBuilderMeta -> {
                        var owner = skullBuilderMeta.owningPlayer();
                        var texture = owner.texture();
                        var generateUUID = texture != null;
                        var uuid = generateUUID ? (owner.uniqueId() == null ? UUID.randomUUID() : owner.uniqueId()) : null;
                        var profile = new GameProfile(uuid, owner.name());
                        if (texture != null) {
                            profile.getProperties().put("textures", new Property("textures", texture.value(), texture.signature()));
                        }
                        if (owner.name() != null && owner.uniqueId() == null && texture == null) {
                            ((SkullMeta) meta).setOwner(owner.name());
                        } else {
                            ReflectionUtils.setValue(meta, CraftMetaSkull$profile, profile);
                        }
                    }
                    case LeatherArmorBuilderMeta leatherArmorBuilderMeta -> ((LeatherArmorMeta) meta).setColor(org.bukkit.Color.fromRGB(leatherArmorBuilderMeta.color().rgb()));
                    case SpawnEggBuilderMeta spawnEggBuilderMeta -> {
                        if (spawnEggBuilderMeta.entityTag() != null) {
                            item.setItemMeta(meta);
                            var mci = CraftItemStack.asNMSCopy(item);
                            var tag = mci.getTag() == null ? new NBTTagCompound() : mci.getTag();
                            try {
                                tag.set("EntityTag", MojangsonParser.parse(spawnEggBuilderMeta.entityTag()));
                            } catch (MojangsonParseException e) {
                                throw new RuntimeException(e);
                            }
                            mci.setTag(tag);
                            item = CraftItemStack.asBukkitCopy(mci);
                            meta = item.getItemMeta();
                        }
                    }
                    case null, default -> throw new UnsupportedOperationException("Meta not supported for this mc version: " + builderMeta);
                }
            }
            item.setItemMeta(meta);
            var mci = CraftItemStack.asNMSCopy(item);
            var tag = mci.getTag() == null ? new NBTTagCompound() : mci.getTag();
            tag.setString("System:persistentDataStorage", storage.storeToJsonObject().toString());
            mci.setTag(tag);
            item = CraftItemStack.asBukkitCopy(mci);
        } else {
            throw new IllegalArgumentException("Item without Meta: " + material);
        }
        return item;
    }

    @Override
    public @NotNull JsonElement serialize() {
        return gson.toJsonTree(build());
    }
}
