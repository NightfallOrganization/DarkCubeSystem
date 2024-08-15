/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component;

import static eu.darkcube.system.server.item.component.ItemComponentImpl.register;

import java.util.List;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.util.RGBLike;
import eu.darkcube.system.server.data.component.DataComponent;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.ItemRarity;
import eu.darkcube.system.server.item.component.components.ArmorTrim;
import eu.darkcube.system.server.item.component.components.AttributeList;
import eu.darkcube.system.server.item.component.components.BannerPatterns;
import eu.darkcube.system.server.item.component.components.Bee;
import eu.darkcube.system.server.item.component.components.BlockPredicates;
import eu.darkcube.system.server.item.component.components.DebugStickState;
import eu.darkcube.system.server.item.component.components.DyedItemColor;
import eu.darkcube.system.server.item.component.components.EnchantmentList;
import eu.darkcube.system.server.item.component.components.FireworkExplosion;
import eu.darkcube.system.server.item.component.components.FireworkList;
import eu.darkcube.system.server.item.component.components.Food;
import eu.darkcube.system.server.item.component.components.HeadProfile;
import eu.darkcube.system.server.item.component.components.ItemBlockState;
import eu.darkcube.system.server.item.component.components.JukeboxPlayable;
import eu.darkcube.system.server.item.component.components.LodestoneTracker;
import eu.darkcube.system.server.item.component.components.MapDecorations;
import eu.darkcube.system.server.item.component.components.MapPostProcessing;
import eu.darkcube.system.server.item.component.components.PotDecorations;
import eu.darkcube.system.server.item.component.components.PotionContents;
import eu.darkcube.system.server.item.component.components.SeededContainerLoot;
import eu.darkcube.system.server.item.component.components.SuspiciousStewEffects;
import eu.darkcube.system.server.item.component.components.Tool;
import eu.darkcube.system.server.item.component.components.Unbreakable;
import eu.darkcube.system.server.item.component.components.WritableBookContent;
import eu.darkcube.system.server.item.component.components.WrittenBookContent;
import eu.darkcube.system.server.item.component.components.util.DyeColor;
import eu.darkcube.system.util.Unit;

@Api
public interface ItemComponent {
    DataComponent<AttributeList> ATTRIBUTE_MODIFIERS = register("attribute_modifiers");
    DataComponent<BannerPatterns> BANNER_PATTERNS = register("banner_patterns");
    DataComponent<DyeColor> BASE_COLOR = register("base_color");
    DataComponent<List<Bee>> BEES = register("bees");
    DataComponent<CompoundBinaryTag> BLOCK_ENTITY_DATA = register("block_entity_data");
    DataComponent<ItemBlockState> BLOCK_STATE = register("block_state");
    DataComponent<CompoundBinaryTag> BUCKET_ENTITY_DATA = register("bucket_entity_data");
    DataComponent<List<ItemBuilder>> BUNDLE_CONTENTS = register("bundle_contents");
    DataComponent<BlockPredicates> CAN_BREAK = register("can_break");
    DataComponent<BlockPredicates> CAN_PLACE_ON = register("can_place_on");
    DataComponent<List<ItemBuilder>> CHARGED_PROJECTILES = register("charged_projectiles");
    DataComponent<List<ItemBuilder>> CONTAINER = register("container");
    DataComponent<SeededContainerLoot> CONTAINER_LOOT = register("container_loot");
    DataComponent<Unit> CREATIVE_SLOT_LOCK = register("creative_slot_lock");
    DataComponent<CompoundBinaryTag> CUSTOM_DATA = register("custom_data");
    DataComponent<Integer> CUSTOM_MODEL_DATA = register("custom_model_data");
    DataComponent<Component> CUSTOM_NAME = register("custom_name");
    DataComponent<Integer> DAMAGE = register("damage");
    DataComponent<DebugStickState> DEBUG_STICK_STATE = register("debug_stick_state");
    DataComponent<DyedItemColor> DYED_COLOR = register("dyed_color");
    DataComponent<Boolean> ENCHANTMENT_GLINT_OVERRIDE = register("enchantment_glint_override");
    DataComponent<EnchantmentList> ENCHANTMENTS = register("enchantments");
    DataComponent<CompoundBinaryTag> ENTITY_DATA = register("entity_data");
    DataComponent<Unit> FIRE_RESISTANT = register("fire_resistant");
    DataComponent<FireworkExplosion> FIREWORK_EXPLOSION = register("firework_explosion");
    DataComponent<FireworkList> FIREWORKS = register("fireworks");
    DataComponent<Food> FOOD = register("food");
    DataComponent<Unit> HIDE_ADDITIONAL_TOOLTIP = register("hide_additional_tooltip");
    DataComponent<Unit> HIDE_TOOLTIP = register("hide_tooltip");
    DataComponent<String> INSTRUMENT = register("instrument");
    DataComponent<Unit> INTANGIBLE_PROJECTILE = register("intangible_projectile");
    DataComponent<Component> ITEM_NAME = register("item_name");
    DataComponent<JukeboxPlayable> JUKEBOX_PLAYABLE = register("jukebox_playable");
    DataComponent<String> LOCK = register("lock");
    DataComponent<LodestoneTracker> LODESTONE_TRACKER = register("lodestone_tracker");
    DataComponent<List<Component>> LORE = register("lore");
    DataComponent<RGBLike> MAP_COLOR = register("map_color");
    DataComponent<MapDecorations> MAP_DECORATIONS = register("map_decorations");
    DataComponent<Integer> MAP_ID = register("map_id");
    DataComponent<MapPostProcessing> MAP_POST_PROCESSING = register("map_post_processing");
    DataComponent<Integer> MAX_DAMAGE = register("max_damage");
    DataComponent<Integer> MAX_STACK_SIZE = register("max_stack_size");
    DataComponent<String> NOTE_BLOCK_SOUND = register("note_block_sound");
    DataComponent<Integer> OMINOUS_BOTTLE_AMPLIFIER= register("ominous_bottle_amplifier");
    DataComponent<PotDecorations> POT_DECORATIONS = register("pot_decorations");
    DataComponent<PotionContents> POTION_CONTENTS = register("potion_contents");
    DataComponent<HeadProfile> PROFILE = register("profile");
    DataComponent<ItemRarity> RARITY = register("rarity");
    DataComponent<List<String>> RECIPES = register("recipes");
    DataComponent<Integer> REPAIR_COST = register("repair_cost");
    DataComponent<EnchantmentList> STORED_ENCHANTMENTS = register("stored_enchantments");
    DataComponent<SuspiciousStewEffects> SUSPICIOUS_STEW_EFFECTS = register("suspicious_stew_effects");
    DataComponent<Tool> TOOL = register("tool");
    DataComponent<ArmorTrim> TRIM = register("trim");
    DataComponent<Unbreakable> UNBREAKABLE = register("unbreakable");
    DataComponent<WritableBookContent> WRITABLE_BOOK_CONTENT = register("writable_book_content");
    DataComponent<WrittenBookContent> WRITTEN_BOOK_CONTENT = register("written_book_content");
}
