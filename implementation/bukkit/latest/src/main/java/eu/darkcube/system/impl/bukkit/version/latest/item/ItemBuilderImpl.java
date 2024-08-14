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
import java.util.function.Function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.bukkit.item.BukkitItemBuilder;
import eu.darkcube.system.bukkit.item.material.BukkitMaterial;
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
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemBuilderImpl extends AbstractItemBuilder implements BukkitItemBuilder {
    private static final NamespacedKey PERSISTENT_DATA_KEY = new NamespacedKey("darkcubesystem", "persistentdatastorage");
    private static final NamespacedKey PERSISTENT_DATA_KEY_LEGACY = new NamespacedKey("system", "persistentdatastorage");
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
    private static final List<Mapping<?>> MAPPINGS;
    private static final List<DirectMapping<?, ?>> DIRECT_MAPPINGS;

    public ItemBuilderImpl() {
    }

    public ItemBuilderImpl(ItemStack item) {
        var ignoreCloneFailure = false;
        material(item.getType());
        amount(item.getAmount());

        var nms = CraftItemStack.unwrap(item);
        for (var i = 0; i < DIRECT_MAPPINGS.size(); i++) {
            DIRECT_MAPPINGS.get(i).load(this, nms);
        }

        var meta = item.getItemMeta();
        if (meta != null) {
            for (var i = 0; i < MAPPINGS.size(); i++) {
                MAPPINGS.get(i).load(this, item, meta);
            }

            // region persistent data migration
            migration:
            {
                var customData = get(CUSTOM_DATA);
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
                    set(CUSTOM_DATA, customData);
                }
            }
            // endregion

            setFlags(meta.getItemFlags());
        }

        loadPersistentDataStorage();

        if (!ignoreCloneFailure) {
            var b = build();
            if (!item.equals(b) && !(item.getType() == b.getType() && item.getType() == Material.AIR)) {
                LOGGER.error("Failed to clone item correctly: ");
                LOGGER.error(" - {}", CraftItemStack.asNMSCopy(item).save(MinecraftServer.getServer().registryAccess()));
                LOGGER.error(" - {}", net.minecraft.world.item.ItemStack.parse(MinecraftServer.getServer().registryAccess(), CraftItemStack.asNMSCopy(item).save(MinecraftServer.getServer().registryAccess())).orElseThrow().save(MinecraftServer.getServer().registryAccess()));
                LOGGER.error(" - {}", CraftItemStack.asNMSCopy(b).save(MinecraftServer.getServer().registryAccess()));
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
        // ItemStack item = new ItemStack(material);
        var material = ((BukkitMaterialImpl) this.material).bukkitType();
        var item = new ItemStack(material);

        if (material != item.getType()) item = item.withType(material);

        item.setAmount(amount);
        var meta = item.getItemMeta();
        if (meta != null) {
            for (var i = 0; i < MAPPINGS.size(); i++) {
                MAPPINGS.get(i).apply(this, item, meta);
            }
            item.setItemMeta(meta);
        }
        var nms = CraftItemStack.unwrap(item);
        for (var i = 0; i < DIRECT_MAPPINGS.size(); i++) {
            DIRECT_MAPPINGS.get(i).apply(this, nms);
        }
        return item;
    }
    //         meta.addItemFlags(flags.stream().map(flag -> ((BukkitItemFlagImpl) flag).bukkitType()).toArray(ItemFlag[]::new));
    //         if (!lore.isEmpty()) meta.lore(lore.stream().map(AdventureUtils::convert).toList());
    //         if (glow.isPresent()) {
    //             meta.setEnchantmentGlintOverride(glow.get());
    //         }
    //         attributeModifiers.forEach((modifier) -> meta.addAttributeModifier(((BukkitAttribute) modifier.attribute()).bukkitType(), ((BukkitAttributeModifierImpl) modifier).bukkitType()));
    //         if (meta instanceof Damageable damageable) {
    //             if (damage.isPresent()) {
    //                 damageable.setDamage(damage.getAsInt());
    //             }
    //         }
    //         if (meta instanceof Repairable repairable) {
    //             if (repairCost.isPresent()) {
    //                 repairable.setRepairCost(repairCost.getAsInt());
    //             }
    //         }
    //         for (var builderMeta : metas) {
    //             switch (builderMeta) {
    //                 case FireworkBuilderMeta fireworkBuilderMeta -> {
    //                     var fireworkEffect = (BukkitFireworkEffectImpl) fireworkBuilderMeta.fireworkEffect();
    //                     ((FireworkEffectMeta) meta).setEffect(fireworkEffect == null ? null : fireworkEffect.bukkitType());
    //                 }
    //                 case SkullBuilderMeta skullBuilderMeta -> {
    //                     var skullMeta = (SkullMeta) meta;
    //                     var owner = skullBuilderMeta.owningPlayer();
    //                     var texture = owner.texture();
    //                     var profile = Bukkit.getServer().createProfileExact(owner.uniqueId() == null ? UUID.randomUUID() : owner.uniqueId(), owner.name());
    //                     if (texture != null) {
    //                         profile.clearProperties();
    //                         profile.setProperty(new ProfileProperty("textures", texture.value(), texture.signature()));
    //                     }
    //                     skullMeta.setPlayerProfile(profile);
    //                 }
    //                 case LeatherArmorBuilderMeta leatherArmorBuilderMeta -> ((LeatherArmorMeta) meta).setColor(org.bukkit.Color.fromARGB(leatherArmorBuilderMeta.color().rgb()));
    //                 case EnchantmentStorageBuilderMeta enchantmentStorageBuilderMeta -> {
    //                     for (var entry : enchantmentStorageBuilderMeta.enchantments().entrySet()) {
    //                         ((EnchantmentStorageMeta) meta).addStoredEnchant(((BukkitEnchantmentImpl) entry.getKey()).bukkitType(), entry.getValue(), true);
    //                     }
    //                 }
    //                 case null, default -> throw new UnsupportedOperationException("Meta not supported for this mc version: " + builderMeta);
    //             }
    //         }
    //         var json = storage.storeToJsonObject();
    //         if (!json.isEmpty()) {
    //             meta.getPersistentDataContainer().set(PERSISTENT_DATA_KEY, PersistentDataType.STRING, json.toString());
    //         }
    //         item.setItemMeta(meta);
    //     }
    //     return item;
    // }

    @Override
    protected @NotNull ItemStack build0() {
        var item = new ItemStack(((BukkitMaterial) material).bukkitType());
        item.setAmount(amount);

        var meta = item.getItemMeta();
        if (meta != null) {
            for (var i = 0; i < MAPPINGS.size(); i++) {
                var mapping = MAPPINGS.get(i);
                mapping.apply(this, item, meta);
            }
            item.setItemMeta(meta);
        }
        return item;
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
        var mappings = new ArrayList<Mapping<?>>();
        var directMappings = new ArrayList<DirectMapping<?, ?>>();
        var m = new MappingsGenerator() {
            @Override
            public <T> void add(DataComponent<T> component, Mapper<T> mapper) {
                mappings.add(new Mapping<>(component, mapper));
            }
        };
        var d = new DirectMappingsGenerator(directMappings);

        d.add(ATTRIBUTE_MODIFIERS, DataComponents.ATTRIBUTE_MODIFIERS, new AttributeModifiersMapper());
        d.add(BANNER_PATTERNS, DataComponents.BANNER_PATTERNS, new BannerPatternsMapper());
        d.add(BASE_COLOR, DataComponents.BASE_COLOR, MapperUtil::convert, MapperUtil::convert);
        d.add(BEES, DataComponents.BEES, new BeesMapper());
        d.add(BLOCK_ENTITY_DATA, DataComponents.BLOCK_ENTITY_DATA, MapperUtil::convertData, MapperUtil::convertData);
        d.add(BLOCK_STATE, DataComponents.BLOCK_STATE, new BlockStateMapper());
        d.add(BUCKET_ENTITY_DATA, DataComponents.BUCKET_ENTITY_DATA, MapperUtil::convertData, MapperUtil::convertData);
        d.add(BUNDLE_CONTENTS, DataComponents.BUNDLE_CONTENTS, new BundleContentsMapper());
        d.add(CAN_BREAK, DataComponents.CAN_BREAK, MapperUtil::convert, MapperUtil::convert);
        d.add(CAN_PLACE_ON, DataComponents.CAN_PLACE_ON, MapperUtil::convert, MapperUtil::convert);
        d.add(CHARGED_PROJECTILES, DataComponents.CHARGED_PROJECTILES, new ChargedProjectilesMapper());
        d.add(CONTAINER_LOOT, DataComponents.CONTAINER_LOOT, new ContainerLootMapper());
        d.add(CONTAINER, DataComponents.CONTAINER, new ContainerMapper());
        d.addUnit(CREATIVE_SLOT_LOCK, DataComponents.CREATIVE_SLOT_LOCK);
        d.add(CUSTOM_DATA, DataComponents.CUSTOM_DATA, MapperUtil::convertData, MapperUtil::convertData);
        d.add(CUSTOM_MODEL_DATA, DataComponents.CUSTOM_MODEL_DATA, new CustomModelDataMapper());
        d.add(CUSTOM_NAME, DataComponents.CUSTOM_NAME, MapperUtil::convert, MapperUtil::convert);
        d.add(DAMAGE, DataComponents.DAMAGE);
        d.add(DEBUG_STICK_STATE, DataComponents.DEBUG_STICK_STATE, new DebugStickStateMapper());
        d.add(DYED_COLOR, DataComponents.DYED_COLOR, new DyedColorMapper());
        d.add(ENCHANTMENT_GLINT_OVERRIDE, DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        d.add(ENCHANTMENTS, DataComponents.ENCHANTMENTS, new EnchantmentsMapper());
        d.add(ENTITY_DATA, DataComponents.ENTITY_DATA, MapperUtil::convertData, MapperUtil::convertData);
        d.addUnit(FIRE_RESISTANT, DataComponents.FIRE_RESISTANT);
        d.add(FIREWORK_EXPLOSION, DataComponents.FIREWORK_EXPLOSION, new FireworkExplosionMapper());
        d.add(FIREWORKS, DataComponents.FIREWORKS, new FireworksMapper());
        d.add(FOOD, DataComponents.FOOD, new FoodMapper());
        d.addUnit(HIDE_ADDITIONAL_TOOLTIP, DataComponents.HIDE_ADDITIONAL_TOOLTIP);
        d.addUnit(HIDE_TOOLTIP, DataComponents.HIDE_TOOLTIP);
        d.add(INSTRUMENT, DataComponents.INSTRUMENT, new InstrumentMapper());
        d.addUnit(INTANGIBLE_PROJECTILE, DataComponents.INTANGIBLE_PROJECTILE);
        d.add(ITEM_NAME, DataComponents.ITEM_NAME, MapperUtil::convert, MapperUtil::convert);
        d.add(JUKEBOX_PLAYABLE, DataComponents.JUKEBOX_PLAYABLE, new JukeboxPlayableMapper());
        d.add(LOCK, DataComponents.LOCK, LockCode::new, LockCode::key);
        d.add(LODESTONE_TRACKER, DataComponents.LODESTONE_TRACKER, new LodestoneTrackerMapper());
        d.add(LORE, DataComponents.LORE, new LoreMapper());
        d.add(MAP_COLOR, DataComponents.MAP_COLOR, new MapColorMapper());
        d.add(MAP_DECORATIONS, DataComponents.MAP_DECORATIONS, new MapDecorationsMapper());
        d.add(MAP_ID, DataComponents.MAP_ID, new MapIdMapper());
        d.add(MAP_POST_PROCESSING, DataComponents.MAP_POST_PROCESSING, new MapPostProcessingMapper());
        d.add(MAX_DAMAGE, DataComponents.MAX_DAMAGE);
        d.add(MAX_STACK_SIZE, DataComponents.MAX_STACK_SIZE);
        d.add(NOTE_BLOCK_SOUND, DataComponents.NOTE_BLOCK_SOUND, new NoteBlockSoundMapper());
        d.add(OMINOUS_BOTTLE_AMPLIFIER, DataComponents.OMINOUS_BOTTLE_AMPLIFIER);
        d.add(POT_DECORATIONS, DataComponents.POT_DECORATIONS, new PotDecorationsMapper());
        d.add(POTION_CONTENTS, DataComponents.POTION_CONTENTS, new PotionContentsMapper());
        d.add(PROFILE, DataComponents.PROFILE, new ProfileMapper());
        d.add(RARITY, DataComponents.RARITY, new RarityMapper());
        d.add(RECIPES, DataComponents.RECIPES, new RecipesMapper());
        d.add(REPAIR_COST, DataComponents.REPAIR_COST);
        d.add(STORED_ENCHANTMENTS, DataComponents.STORED_ENCHANTMENTS, new EnchantmentsMapper());
        d.add(SUSPICIOUS_STEW_EFFECTS, DataComponents.SUSPICIOUS_STEW_EFFECTS, new SuspiciousStewEffectsMapper());
        d.add(TOOL, DataComponents.TOOL, new ToolMapper());
        d.add(TRIM, DataComponents.TRIM, new TrimMapper());
        m.add(UNBREAKABLE, new UnbreakableMapper());
        m.add(WRITABLE_BOOK_CONTENT, new WritableBookContentMapper());
        m.add(WRITTEN_BOOK_CONTENT, new WrittenBookContentMapper());

        MAPPINGS = List.copyOf(mappings);
        DIRECT_MAPPINGS = List.copyOf(directMappings);
    }

    private record DirectMappingsGenerator(List<DirectMapping<?, ?>> mappings) {
        <T, V> void add(DataComponent<T> component, DataComponentType<V> minecraft, DirectMapper<T, V> mapper) {
            mappings.add(new DirectMapping<>(component, minecraft, mapper));
        }

        <T> void add(DataComponent<T> component, DataComponentType<T> minecraft) {
            mappings.add(new DirectMapping<>(component, minecraft));
        }

        void addUnit(DataComponent<Unit> component, DataComponentType<net.minecraft.util.Unit> minecraft) {
            add(component, minecraft, _ -> net.minecraft.util.Unit.INSTANCE, _ -> Unit.INSTANCE);
        }

        <T, V> void add(DataComponent<T> component, DataComponentType<V> minecraft, Function<T, V> toTheirType, Function<V, T> toOurType) {
            mappings.add(new DirectMapping<>(component, minecraft, new DirectMapper<T, V>() {
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

    private interface MappingsGenerator {
        <T> void add(DataComponent<T> component, Mapper<T> mapper);
    }
}
