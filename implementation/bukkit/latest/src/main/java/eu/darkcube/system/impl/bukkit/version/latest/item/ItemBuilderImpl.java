/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.bukkit.item.BukkitItemBuilder;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.AttributeModifiersMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BannerPatternsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BeesMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BlockStateMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BundleContentsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ChargedProjectilesMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ContainerLootMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ContainerMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.CustomModelDataMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.DebugStickStateMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.DyedColorMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.EnchantmentsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.FireworkExplosionMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.FireworksMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.FoodMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.InstrumentMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.JukeboxPlayableMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.LodestoneTrackerMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.LoreMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.MapColorMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.MapDecorationsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.MapIdMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.MapPostProcessingMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.NoteBlockSoundMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.PotDecorationsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.PotionContentsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ProfileMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.RarityMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.RecipesMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.SuspiciousStewEffectsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ToolMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.TrimMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.UnbreakableMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.WritableBookContentMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.WrittenBookContentMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.impl.bukkit.version.latest.item.material.BukkitMaterialImpl;
import eu.darkcube.system.impl.common.data.LegacyDataTransformer;
import eu.darkcube.system.impl.server.item.AbstractItemBuilder;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.data.component.DataComponent;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.Unit;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.LockCode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemStack;

public class ItemBuilderImpl extends AbstractItemBuilder implements BukkitItemBuilder {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ItemStack.class, new TypeAdapter<ItemStack>() {
        @Override
        public void write(JsonWriter writer, ItemStack value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            var nms = CraftItemStack.asNMSCopy(value);
            var registries = MinecraftServer.getServer().registryAccess();
            var nbt = nms.save(registries);
            writer.value(nbt.toString());
        }

        @Override
        public ItemStack read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            var tag = reader.nextString();
            CompoundTag nbt;
            try {
                nbt = TagParser.parseTag(tag);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
            var nbtItem = net.minecraft.world.item.ItemStack.parse(MinecraftServer.getServer().registryAccess(), nbt).orElseThrow();
            return CraftItemStack.asBukkitCopy(nbtItem);
        }
    }).create();
    private static final List<Mapping<?, ?>> MAPPINGS;

    public ItemBuilderImpl(ItemStack item) {
        var ignoreCloneFailure = false;
        material(item.getType());
        amount(item.getAmount());

        var nms = CraftItemStack.unwrap(item);
        for (var i = 0; i < MAPPINGS.size(); i++) {
            MAPPINGS.get(i).load(this, nms);
        }

        var meta = item.getItemMeta();
        if (meta != null) {
            // region persistent data migration
            migration:
            {
                var customData = components.get(CUSTOM_DATA);
                var originalCustomData = customData;
                if (customData == null) break migration;
                var bukkitValues = (CompoundBinaryTag) customData.get("PublicBukkitValues");
                var originalBukkitValues = bukkitValues;
                if (bukkitValues == null) break migration;
                if (bukkitValues.keySet().contains("system:persistentdatastorage")) {
                    var data = bukkitValues.getString("system:persistentdatastorage");
                    var json = new Gson().fromJson(data, JsonObject.class);
                    LegacyDataTransformer.transformLegacyPersistentData(json);
                    data = new Gson().toJson(json);
                    bukkitValues = bukkitValues.remove("system:persistentdatastorage");
                    bukkitValues = bukkitValues.putString("darkcubesystem:persistentdatastorage", data);
                }
                if (bukkitValues.keySet().contains("darkcubesystem:persistentdatastorage")) {
                    var data = bukkitValues.getString("darkcubesystem:persistentdatastorage");
                    customData = customData.putString(KEY_DOCUMENT, data);
                    bukkitValues = bukkitValues.remove("darkcubesystem:persistentdatastorage");
                }
                if (originalBukkitValues != bukkitValues) {
                    if (bukkitValues.keySet().isEmpty()) {
                        customData = customData.remove("PublicBukkitValues");
                    } else {
                        customData = customData.put("PublicBukkitValues", bukkitValues);
                    }
                }
                if (originalCustomData != customData) {
                    ignoreCloneFailure = true;
                    set(CUSTOM_DATA, customData);
                }
            }
            // endregion
        }

        if (!ignoreCloneFailure) {
            var b = build();
            var b1 = CraftItemStack.unwrap(b);
            if (!net.minecraft.world.item.ItemStack.isSameItem(b1, nms) && !(item.getType() == b.getType() && item.getType() == Material.AIR)) {
                LOGGER.error("Failed to clone item correctly: ");
                LOGGER.error(" - {}", nms.save(MinecraftServer.getServer().registryAccess()));
                LOGGER.error(" - {}", b1.save(MinecraftServer.getServer().registryAccess()));
            }
        }
    }

    public static ItemBuilderImpl deserialize(JsonElement json) {
        return new ItemBuilderImpl(GSON.fromJson(json, ItemStack.class));
    }

    @Override
    public boolean canBeRepairedBy(ItemBuilder ingredient) {
        var item = build();
        return ingredient.<ItemStack>build().canRepair(item);
    }

    @Override
    public @NotNull ItemStack build() {
        return super.build();
    }

    @Override
    protected @NotNull ItemStack build0() {
        var bukkitMaterial = ((BukkitMaterialImpl) this.material).bukkitType();
        var itemType = (CraftItemType<?>) Objects.requireNonNull(bukkitMaterial.asItemType());
        var nms = new net.minecraft.world.item.ItemStack(itemType.getHandle(), amount);
        for (var component : itemType.getHandle().components()) {
            nms.remove(component.type());
        }

        for (var i = 0; i < MAPPINGS.size(); i++) {
            MAPPINGS.get(i).apply(this, nms);
        }
        return CraftItemStack.asCraftMirror(nms);
    }

    @Override
    public @NotNull AbstractItemBuilder clone() {
        return new ItemBuilderImpl(build());
    }

    @Override
    public @NotNull JsonElement serialize() {
        return GSON.toJsonTree(build());
    }

    static {
        var directMappings = new ArrayList<Mapping<?, ?>>();
        var gen = new MappingsGenerator(directMappings);

        gen.add(ATTRIBUTE_MODIFIERS, DataComponents.ATTRIBUTE_MODIFIERS, new AttributeModifiersMapper());
        gen.add(BANNER_PATTERNS, DataComponents.BANNER_PATTERNS, new BannerPatternsMapper());
        gen.add(BASE_COLOR, DataComponents.BASE_COLOR, MapperUtil::convert, MapperUtil::convert);
        gen.add(BEES, DataComponents.BEES, new BeesMapper());
        gen.add(BLOCK_ENTITY_DATA, DataComponents.BLOCK_ENTITY_DATA, MapperUtil::convertData, MapperUtil::convertData);
        gen.add(BLOCK_STATE, DataComponents.BLOCK_STATE, new BlockStateMapper());
        gen.add(BUCKET_ENTITY_DATA, DataComponents.BUCKET_ENTITY_DATA, MapperUtil::convertData, MapperUtil::convertData);
        gen.add(BUNDLE_CONTENTS, DataComponents.BUNDLE_CONTENTS, new BundleContentsMapper());
        gen.add(CAN_BREAK, DataComponents.CAN_BREAK, MapperUtil::convert, MapperUtil::convert);
        gen.add(CAN_PLACE_ON, DataComponents.CAN_PLACE_ON, MapperUtil::convert, MapperUtil::convert);
        gen.add(CHARGED_PROJECTILES, DataComponents.CHARGED_PROJECTILES, new ChargedProjectilesMapper());
        gen.add(CONTAINER_LOOT, DataComponents.CONTAINER_LOOT, new ContainerLootMapper());
        gen.add(CONTAINER, DataComponents.CONTAINER, new ContainerMapper());
        gen.addUnit(CREATIVE_SLOT_LOCK, DataComponents.CREATIVE_SLOT_LOCK);
        gen.add(CUSTOM_DATA, DataComponents.CUSTOM_DATA, MapperUtil::convertData, MapperUtil::convertData);
        gen.add(CUSTOM_MODEL_DATA, DataComponents.CUSTOM_MODEL_DATA, new CustomModelDataMapper());
        gen.add(CUSTOM_NAME, DataComponents.CUSTOM_NAME, MapperUtil::convert, MapperUtil::convert);
        gen.add(DAMAGE, DataComponents.DAMAGE);
        gen.add(DEBUG_STICK_STATE, DataComponents.DEBUG_STICK_STATE, new DebugStickStateMapper());
        gen.add(DYED_COLOR, DataComponents.DYED_COLOR, new DyedColorMapper());
        gen.add(ENCHANTMENT_GLINT_OVERRIDE, DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        gen.add(ENCHANTMENTS, DataComponents.ENCHANTMENTS, new EnchantmentsMapper());
        gen.add(ENTITY_DATA, DataComponents.ENTITY_DATA, MapperUtil::convertData, MapperUtil::convertData);
        gen.addUnit(FIRE_RESISTANT, DataComponents.FIRE_RESISTANT);
        gen.add(FIREWORK_EXPLOSION, DataComponents.FIREWORK_EXPLOSION, new FireworkExplosionMapper());
        gen.add(FIREWORKS, DataComponents.FIREWORKS, new FireworksMapper());
        gen.add(FOOD, DataComponents.FOOD, new FoodMapper());
        gen.addUnit(HIDE_ADDITIONAL_TOOLTIP, DataComponents.HIDE_ADDITIONAL_TOOLTIP);
        gen.addUnit(HIDE_TOOLTIP, DataComponents.HIDE_TOOLTIP);
        gen.add(INSTRUMENT, DataComponents.INSTRUMENT, new InstrumentMapper());
        gen.addUnit(INTANGIBLE_PROJECTILE, DataComponents.INTANGIBLE_PROJECTILE);
        gen.add(ITEM_NAME, DataComponents.ITEM_NAME, MapperUtil::convert, MapperUtil::convert);
        gen.add(JUKEBOX_PLAYABLE, DataComponents.JUKEBOX_PLAYABLE, new JukeboxPlayableMapper());
        gen.add(LOCK, DataComponents.LOCK, LockCode::new, LockCode::key);
        gen.add(LODESTONE_TRACKER, DataComponents.LODESTONE_TRACKER, new LodestoneTrackerMapper());
        gen.add(LORE, DataComponents.LORE, new LoreMapper());
        gen.add(MAP_COLOR, DataComponents.MAP_COLOR, new MapColorMapper());
        gen.add(MAP_DECORATIONS, DataComponents.MAP_DECORATIONS, new MapDecorationsMapper());
        gen.add(MAP_ID, DataComponents.MAP_ID, new MapIdMapper());
        gen.add(MAP_POST_PROCESSING, DataComponents.MAP_POST_PROCESSING, new MapPostProcessingMapper());
        gen.add(MAX_DAMAGE, DataComponents.MAX_DAMAGE);
        gen.add(MAX_STACK_SIZE, DataComponents.MAX_STACK_SIZE);
        gen.add(NOTE_BLOCK_SOUND, DataComponents.NOTE_BLOCK_SOUND, new NoteBlockSoundMapper());
        gen.add(OMINOUS_BOTTLE_AMPLIFIER, DataComponents.OMINOUS_BOTTLE_AMPLIFIER);
        gen.add(POT_DECORATIONS, DataComponents.POT_DECORATIONS, new PotDecorationsMapper());
        gen.add(POTION_CONTENTS, DataComponents.POTION_CONTENTS, new PotionContentsMapper());
        gen.add(PROFILE, DataComponents.PROFILE, new ProfileMapper());
        gen.add(RARITY, DataComponents.RARITY, new RarityMapper());
        gen.add(RECIPES, DataComponents.RECIPES, new RecipesMapper());
        gen.add(REPAIR_COST, DataComponents.REPAIR_COST);
        gen.add(STORED_ENCHANTMENTS, DataComponents.STORED_ENCHANTMENTS, new EnchantmentsMapper());
        gen.add(SUSPICIOUS_STEW_EFFECTS, DataComponents.SUSPICIOUS_STEW_EFFECTS, new SuspiciousStewEffectsMapper());
        gen.add(TOOL, DataComponents.TOOL, new ToolMapper());
        gen.add(TRIM, DataComponents.TRIM, new TrimMapper());
        gen.add(UNBREAKABLE, DataComponents.UNBREAKABLE, new UnbreakableMapper());
        gen.add(WRITABLE_BOOK_CONTENT, DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContentMapper());
        gen.add(WRITTEN_BOOK_CONTENT, DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContentMapper());

        MAPPINGS = List.copyOf(directMappings);
    }

    private record MappingsGenerator(List<Mapping<?, ?>> mappings) {
        <T, V> void add(DataComponent<T> component, DataComponentType<V> minecraft, Mapper<T, V> mapper) {
            mappings.add(new Mapping<>(component, minecraft, mapper));
        }

        <T> void add(DataComponent<T> component, DataComponentType<T> minecraft) {
            mappings.add(new Mapping<>(component, minecraft));
        }

        void addUnit(DataComponent<Unit> component, DataComponentType<net.minecraft.util.Unit> minecraft) {
            add(component, minecraft, _ -> net.minecraft.util.Unit.INSTANCE, _ -> Unit.INSTANCE);
        }

        <T, V> void add(DataComponent<T> component, DataComponentType<V> minecraft, Function<T, V> toTheirType, Function<V, T> toOurType) {
            mappings.add(new Mapping<>(component, minecraft, new Mapper<>() {
                @Override
                public V apply(T mapping) {
                    return toTheirType.apply(mapping);
                }

                @Override
                public T load(V mapping) {
                    return toOurType.apply(mapping);
                }
            }));
        }
    }
}
