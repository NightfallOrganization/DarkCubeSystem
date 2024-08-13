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
import java.util.UUID;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.bukkit.item.BukkitItemBuilder;
import eu.darkcube.system.bukkit.item.material.BukkitMaterial;
import eu.darkcube.system.impl.bukkit.item.enchant.BukkitEnchantmentImpl;
import eu.darkcube.system.impl.bukkit.item.firework.BukkitFireworkEffectImpl;
import eu.darkcube.system.impl.bukkit.item.flag.BukkitItemFlagImpl;
import eu.darkcube.system.impl.bukkit.item.material.BukkitMaterialImpl;
import eu.darkcube.system.impl.bukkit.version.latest.AdventureUtils;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttribute;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeModifierImpl;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.AttributeModifiersMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BannerPatternsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BaseColorMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BeesMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BlockEntityDataMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BlockStateMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BucketEntityDataMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.BundleContentsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.CanBreakMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.CanPlaceOnMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ChargedProjectilesMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ContainerLootMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ContainerMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.CustomDataMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.CustomModelDataMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.CustomNameMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.DamageMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.DebugStickStateMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.DyedColorMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.EnchantmentGlintOverrideMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.EnchantmentsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.EntityDataMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.FireResistantMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.FireworkExplosionMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.FireworksMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.FoodMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.HideAdditionalTooltipMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.HideTooltipMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.InstrumentMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.IntangibleProjectileMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ItemNameMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.JukeboxPlayableMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.LockMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.LodestoneTrackerMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.LoreMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.MapColorMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.MapDecorationsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.MapIdMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.MapPostProcessingMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.MaxDamageMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.MaxStackSizeMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.NoteBlockSoundMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.OminousBottleAmplifierMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.PotDecorationsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.PotionContentsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ProfileMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.RecipesMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.RepairCostMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.StoredEnchantmentsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.SuspiciousStewEffectsMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.ToolMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.TrimMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.UnbreakableMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.WritableBookContentMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.WrittenBookContentMapper;
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
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.data.component.DataComponent;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.ItemRarity;
import eu.darkcube.system.server.item.meta.EnchantmentStorageBuilderMeta;
import eu.darkcube.system.server.item.meta.FireworkBuilderMeta;
import eu.darkcube.system.server.item.meta.LeatherArmorBuilderMeta;
import eu.darkcube.system.server.item.meta.SkullBuilderMeta;
import eu.darkcube.system.util.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

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

    public ItemBuilderImpl() {
    }

    public ItemBuilderImpl(ItemStack item) {
        var ignoreCloneFailure = false;
        material(item.getType());
        amount(item.getAmount());

        var meta = item.getItemMeta();

        if (meta != null) {
            for (var i = 0; i < MAPPINGS.size(); i++) {
                var mapping = MAPPINGS.get(i);
                mapping.apply(this, item, meta);
            }

            // region persistent data migration
            {
                var persistentData = meta.getPersistentDataContainer();

                if (persistentData.has(PERSISTENT_DATA_KEY_LEGACY)) {
                    var data = persistentData.get(PERSISTENT_DATA_KEY_LEGACY, PersistentDataType.STRING);
                    if (data != null) {
                        var json = new Gson().fromJson(data, JsonObject.class);
                        LegacyDataTransformer.transformLegacyPersistentData(json);
                        storage.loadFromJsonObject(json);
                    }
                    ignoreCloneFailure = true;
                    persistentData.remove(PERSISTENT_DATA_KEY_LEGACY);
                } else if (persistentData.has(PERSISTENT_DATA_KEY)) {
                    var data = persistentData.get(PERSISTENT_DATA_KEY, PersistentDataType.STRING);
                    if (data != null) {
                        var json = new Gson().fromJson(data, JsonObject.class);
                        storage.loadFromJsonObject(json);
                    }
                    persistentData.remove(PERSISTENT_DATA_KEY);
                }

                var customData = get(CUSTOM_DATA);
            }
            // endregion

            if (meta.hasCustomModelData()) {
                customModelData(meta.getCustomModelData());
            }
            unbreakable(meta.isUnbreakable());
            meta.setUnbreakable(false);
            if (meta.hasRarity()) {
                var rarity = ItemRarity.values()[meta.getRarity().ordinal()];
                rarity(rarity);
                meta.setRarity(null);
            }
            if (meta.hasDisplayName()) {
                displayname = AdventureUtils.convert(meta.displayName());
                meta.displayName(null);
            }
            for (var e : new ArrayList<>(meta.getEnchants().entrySet())) {
                enchant(e.getKey(), e.getValue());
                meta.removeEnchant(e.getKey());
            }

            if (meta.getAttributeModifiers() != null) {
                meta.getAttributeModifiers().forEach((attribute, m) -> this.attributeModifier(attribute, m.key(), m.getSlotGroup(), m.getAmount(), m.getOperation()));
                meta.setAttributeModifiers(null);
            }

            setFlags(meta.getItemFlags());
            meta.removeItemFlags(ItemFlag.values());
            if (meta.lore() != null) {
                lore.addAll(AdventureUtils.convert2(Objects.requireNonNull(meta.lore())));
                meta.lore(null);
            }
            if (meta instanceof Damageable damageable && damageable.hasDamageValue()) {
                damage(damageable.getDamage());
                damageable.resetDamage();
            }
            if (meta instanceof Repairable repairable && repairable.hasRepairCost()) {
                repairCost(repairable.getRepairCost());
            }
            if (meta instanceof FireworkEffectMeta fireworkEffectMeta) {
                meta(FireworkBuilderMeta.class).fireworkEffect(fireworkEffectMeta.getEffect());
                fireworkEffectMeta.setEffect(null);
            }
            if (meta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
                meta(EnchantmentStorageBuilderMeta.class).enchantments(enchantmentStorageMeta.getStoredEnchants());
                for (var enchantment : enchantmentStorageMeta.getStoredEnchants().keySet()) {
                    enchantmentStorageMeta.removeStoredEnchant(enchantment);
                }
            }
            if (meta instanceof SkullMeta skullMeta) {
                if (skullMeta.hasOwner()) {
                    var pp = skullMeta.getPlayerProfile();
                    if (pp != null) {
                        var prop = pp.getProperties().stream().filter(p -> p.getName().equals("textures")).findFirst().orElse(null);
                        var texture = prop == null ? null : new SkullBuilderMeta.UserProfile.Texture(prop.getValue(), prop.getSignature());
                        var up = new SkullBuilderMeta.UserProfile(pp.getName(), pp.getId(), texture);
                        meta(SkullBuilderMeta.class).owningPlayer(up);
                    }
                    skullMeta.setOwningPlayer(null);
                }
            }
            if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
                if (leatherArmorMeta.isDyed()) {
                    meta(LeatherArmorBuilderMeta.class).color(new Color(leatherArmorMeta.getColor().asRGB()));
                    leatherArmorMeta.setColor(null);
                }
            }
            this.item.setItemMeta(meta);
        }
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
        var item = this.item == null ? new ItemStack(material) : this.item.clone();

        if (material != item.getType()) item = item.withType(material);

        item.setAmount(amount);
        var meta = item.getItemMeta();
        if (meta != null) {
            if (unbreakable.isPresent()) {
                meta.setUnbreakable(unbreakable.get());
            }
            if (customModelData.isPresent()) {
                meta.setCustomModelData(customModelData.getAsInt());
            }
            if (rarity != null) {
                meta.setRarity(org.bukkit.inventory.ItemRarity.values()[rarity.ordinal()]);
            }
            if (displayname != Component.empty()) {
                meta.displayName(AdventureUtils.convert(displayname));
            }
            for (var e : enchantments.entrySet()) {
                meta.addEnchant(((BukkitEnchantmentImpl) e.getKey()).bukkitType(), e.getValue(), true);
            }

            meta.addItemFlags(flags.stream().map(flag -> ((BukkitItemFlagImpl) flag).bukkitType()).toArray(ItemFlag[]::new));
            if (!lore.isEmpty()) meta.lore(lore.stream().map(AdventureUtils::convert).toList());
            if (glow.isPresent()) {
                meta.setEnchantmentGlintOverride(glow.get());
            }
            attributeModifiers.forEach((modifier) -> meta.addAttributeModifier(((BukkitAttribute) modifier.attribute()).bukkitType(), ((BukkitAttributeModifierImpl) modifier).bukkitType()));
            if (meta instanceof Damageable damageable) {
                if (damage.isPresent()) {
                    damageable.setDamage(damage.getAsInt());
                }
            }
            if (meta instanceof Repairable repairable) {
                if (repairCost.isPresent()) {
                    repairable.setRepairCost(repairCost.getAsInt());
                }
            }
            for (var builderMeta : metas) {
                switch (builderMeta) {
                    case FireworkBuilderMeta fireworkBuilderMeta -> {
                        var fireworkEffect = (BukkitFireworkEffectImpl) fireworkBuilderMeta.fireworkEffect();
                        ((FireworkEffectMeta) meta).setEffect(fireworkEffect == null ? null : fireworkEffect.bukkitType());
                    }
                    case SkullBuilderMeta skullBuilderMeta -> {
                        var skullMeta = (SkullMeta) meta;
                        var owner = skullBuilderMeta.owningPlayer();
                        var texture = owner.texture();
                        var profile = Bukkit.getServer().createProfileExact(owner.uniqueId() == null ? UUID.randomUUID() : owner.uniqueId(), owner.name());
                        if (texture != null) {
                            profile.clearProperties();
                            profile.setProperty(new ProfileProperty("textures", texture.value(), texture.signature()));
                        }
                        skullMeta.setPlayerProfile(profile);
                    }
                    case LeatherArmorBuilderMeta leatherArmorBuilderMeta -> ((LeatherArmorMeta) meta).setColor(org.bukkit.Color.fromARGB(leatherArmorBuilderMeta.color().rgb()));
                    case EnchantmentStorageBuilderMeta enchantmentStorageBuilderMeta -> {
                        for (var entry : enchantmentStorageBuilderMeta.enchantments().entrySet()) {
                            ((EnchantmentStorageMeta) meta).addStoredEnchant(((BukkitEnchantmentImpl) entry.getKey()).bukkitType(), entry.getValue(), true);
                        }
                    }
                    case null, default -> throw new UnsupportedOperationException("Meta not supported for this mc version: " + builderMeta);
                }
            }
            var json = storage.storeToJsonObject();
            if (!json.isEmpty()) {
                meta.getPersistentDataContainer().set(PERSISTENT_DATA_KEY, PersistentDataType.STRING, json.toString());
            }
            item.setItemMeta(meta);
        }
        return item;
    }

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
        var m = new MappingsGenerator() {
            @Override
            public <T> void add(DataComponent<T> component, Mapper<T> mapper) {
                mappings.add(new Mapping<>(component, mapper));
            }
        };

        m.add(ATTRIBUTE_MODIFIERS, new AttributeModifiersMapper());
        m.add(BANNER_PATTERNS, new BannerPatternsMapper());
        m.add(BASE_COLOR, new BaseColorMapper());
        m.add(BEES, new BeesMapper());
        m.add(BLOCK_ENTITY_DATA, new BlockEntityDataMapper());
        m.add(BLOCK_STATE, new BlockStateMapper());
        m.add(BUCKET_ENTITY_DATA, new BucketEntityDataMapper());
        m.add(BUNDLE_CONTENTS, new BundleContentsMapper());
        m.add(CAN_BREAK, new CanBreakMapper());
        m.add(CAN_PLACE_ON, new CanPlaceOnMapper());
        m.add(CHARGED_PROJECTILES, new ChargedProjectilesMapper());
        m.add(CONTAINER_LOOT, new ContainerLootMapper());
        m.add(CONTAINER, new ContainerMapper());
        m.add(CUSTOM_DATA, new CustomDataMapper());
        m.add(CUSTOM_MODEL_DATA, new CustomModelDataMapper());
        m.add(CUSTOM_NAME, new CustomNameMapper());
        m.add(DAMAGE, new DamageMapper());
        m.add(DEBUG_STICK_STATE, new DebugStickStateMapper());
        m.add(DYED_COLOR, new DyedColorMapper());
        m.add(ENCHANTMENT_GLINT_OVERRIDE, new EnchantmentGlintOverrideMapper());
        m.add(ENCHANTMENTS, new EnchantmentsMapper());
        m.add(ENTITY_DATA, new EntityDataMapper());
        m.add(FIRE_RESISTANT, new FireResistantMapper());
        m.add(FIREWORK_EXPLOSION, new FireworkExplosionMapper());
        m.add(FIREWORKS, new FireworksMapper());
        m.add(FOOD, new FoodMapper());
        m.add(HIDE_ADDITIONAL_TOOLTIP, new HideAdditionalTooltipMapper());
        m.add(HIDE_TOOLTIP, new HideTooltipMapper());
        m.add(INSTRUMENT, new InstrumentMapper());
        m.add(INTANGIBLE_PROJECTILE, new IntangibleProjectileMapper());
        m.add(ITEM_NAME, new ItemNameMapper());
        m.add(JUKEBOX_PLAYABLE, new JukeboxPlayableMapper());
        m.add(LOCK, new LockMapper());
        m.add(LODESTONE_TRACKER, new LodestoneTrackerMapper());
        m.add(LORE, new LoreMapper());
        m.add(MAP_COLOR, new MapColorMapper());
        m.add(MAP_DECORATIONS, new MapDecorationsMapper());
        m.add(MAP_ID, new MapIdMapper());
        m.add(MAP_POST_PROCESSING, new MapPostProcessingMapper());
        m.add(MAX_DAMAGE, new MaxDamageMapper());
        m.add(MAX_STACK_SIZE, new MaxStackSizeMapper());
        m.add(NOTE_BLOCK_SOUND, new NoteBlockSoundMapper());
        m.add(OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifierMapper());
        m.add(POT_DECORATIONS, new PotDecorationsMapper());
        m.add(POTION_CONTENTS, new PotionContentsMapper());
        m.add(PROFILE, new ProfileMapper());
        m.add(RECIPES, new RecipesMapper());
        m.add(REPAIR_COST, new RepairCostMapper());
        m.add(STORED_ENCHANTMENTS, new StoredEnchantmentsMapper());
        m.add(SUSPICIOUS_STEW_EFFECTS, new SuspiciousStewEffectsMapper());
        m.add(TOOL, new ToolMapper());
        m.add(TRIM, new TrimMapper());
        m.add(UNBREAKABLE, new UnbreakableMapper());
        m.add(WRITABLE_BOOK_CONTENT, new WritableBookContentMapper());
        m.add(WRITTEN_BOOK_CONTENT, new WrittenBookContentMapper());

        MAPPINGS = List.copyOf(mappings);
    }

    private interface MappingsGenerator {
        <T> void add(DataComponent<T> component, Mapper<T> mapper);
    }
}
