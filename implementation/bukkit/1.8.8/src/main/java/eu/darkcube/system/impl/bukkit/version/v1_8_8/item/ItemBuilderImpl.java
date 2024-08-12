/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.bukkit.item.BukkitItemBuilder;
import eu.darkcube.system.impl.bukkit.item.material.BukkitMaterialImpl;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.CustomDataMapper;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.CustomNameMapper;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.DamageMapper;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.DyedColorMapper;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.EnchantmentGlintOverrideMapper;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.EnchantmentsMapper;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.FireworkExplosionMapper;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.LoreMapper;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.ProfileMapper;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.SpawnerEntityDataMapper;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings.UnbreakableMapper;
import eu.darkcube.system.impl.server.item.AbstractItemBuilder;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.LegacyItemComponent;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class ItemBuilderImpl extends AbstractItemBuilder implements BukkitItemBuilder {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ItemStack.class, new TypeAdapter<ItemStack>() {
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
    private static final List<Mapping<?>> MAPPINGS;
    private static final List<Mapping<?>> MAPPINGS_PURE;

    public ItemBuilderImpl() {
    }

    public ItemBuilderImpl(ItemStack item) {
        material(item.getType());
        amount(item.getAmount());
        for (var i = 0; i < MAPPINGS_PURE.size(); i++) {
            var mapping = MAPPINGS_PURE.get(i);
            mapping.load(this, item, null);
        }
        if (item.hasItemMeta()) {
            var meta = item.getItemMeta();
            for (var i = 0; i < MAPPINGS.size(); i++) {
                var mapping = MAPPINGS.get(i);
                mapping.load(this, item, meta);
            }

            // region persistent data migration
            {
                var customData = get(CUSTOM_DATA);
                if (customData != null) {
                    var original = customData;
                    if (customData.keySet().contains("System:persistentDataStorage")) {
                        var s = customData.getString("System:persistentDataStorage");
                        customData = customData.remove("System:persistentDataStorage");
                        customData = customData.putString("system:persistent_data_storage", s);
                    }
                    if (customData.keySet().contains("system:persistent_data_storage")) {
                        var s = customData.getString("system:persistent_data_storage");
                        customData = customData.remove("system:persistent_data_storage");
                        customData = customData.putString(KEY_DOCUMENT, s);
                    }
                    if (original != customData) {
                        set(CUSTOM_DATA, customData);
                    }
                }
            }
            // endregion
        }

        loadPersistentDataStorage();
    }

    @Override
    public @NotNull ItemStack build0() {
        var item = new ItemStack(((BukkitMaterialImpl) material).bukkitType());
        item.setAmount(amount);

        for (var i = 0; i < MAPPINGS_PURE.size(); i++) {
            var mapping = MAPPINGS_PURE.get(i);
            mapping.apply(this, item, null);
        }
        var meta = item.getItemMeta();
        if (meta != null) {
            for (var i = 0; i < MAPPINGS.size(); i++) {
                var mapping = MAPPINGS.get(i);
                mapping.apply(this, item, meta);
            }
            item.setItemMeta(meta);
        } else {
            throw new IllegalArgumentException("Item without Meta: " + material);
        }
        return item;
    }

    @Override
    public final @NotNull ItemStack build() {
        return super.build();
    }

    @Override
    public @NotNull AbstractItemBuilder clone() {
        return new ItemBuilderImpl(build());
    }

    public static ItemBuilderImpl deserialize(JsonElement json) {
        return new ItemBuilderImpl(GSON.fromJson(json, ItemStack.class));
    }

    @Override
    public @NotNull JsonElement serialize() {
        return GSON.toJsonTree(build());
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
    public boolean canBeRepairedBy(ItemBuilder item) {
        throw new UnsupportedOperationException();
    }

    static {
        var m = new ArrayList<Mapping<?>>();
        var p = new ArrayList<Mapping<?>>();
        m.add(new Mapping<>(CUSTOM_DATA, new CustomDataMapper()));
        m.add(new Mapping<>(CUSTOM_NAME, new CustomNameMapper()));
        p.add(new Mapping<>(DAMAGE, new DamageMapper()));
        m.add(new Mapping<>(DYED_COLOR, new DyedColorMapper()));
        m.add(new Mapping<>(ENCHANTMENT_GLINT_OVERRIDE, new EnchantmentGlintOverrideMapper()));
        m.add(new Mapping<>(ENCHANTMENTS, new EnchantmentsMapper()));
        m.add(new Mapping<>(FIREWORK_EXPLOSION, new FireworkExplosionMapper()));
        m.add(new Mapping<>(LORE, new LoreMapper()));
        m.add(new Mapping<>(PROFILE, new ProfileMapper()));
        m.add(new Mapping<>(LegacyItemComponent.SPAWNER_ENTITY_DATA, new SpawnerEntityDataMapper()));
        m.add(new Mapping<>(UNBREAKABLE, new UnbreakableMapper()));
        MAPPINGS = List.copyOf(m);
        MAPPINGS_PURE = List.copyOf(p);
    }
}
