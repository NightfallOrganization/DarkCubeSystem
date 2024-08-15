/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item;

import static eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport.adventureSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import eu.darkcube.system.impl.minestom.item.material.MinestomMaterialImpl;
import eu.darkcube.system.impl.server.item.AbstractItemBuilder;
import eu.darkcube.system.impl.server.item.KeyProvider;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.util.RGBLike;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.MinestomEquipmentSlotGroup;
import eu.darkcube.system.minestom.item.MinestomItemBuilder;
import eu.darkcube.system.minestom.item.attribute.MinestomAttribute;
import eu.darkcube.system.minestom.item.attribute.MinestomAttributeModifierOperation;
import eu.darkcube.system.minestom.item.material.MinestomMaterial;
import eu.darkcube.system.server.data.component.DataComponent;
import eu.darkcube.system.server.item.EquipmentSlotGroup;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.ItemRarity;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeModifier;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
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
import eu.darkcube.system.server.item.component.components.util.BlockTypeFilter;
import eu.darkcube.system.server.item.component.components.util.CustomPotionEffect;
import eu.darkcube.system.server.item.component.components.util.DyeColor;
import eu.darkcube.system.server.item.component.components.util.FilteredText;
import eu.darkcube.system.server.item.component.components.util.WorldPos;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.util.Unit;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.nbt.TagStringIOExt;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.predicate.BlockPredicate;
import net.minestom.server.instance.block.predicate.PropertiesPredicate;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.potion.PotionType;
import net.minestom.server.registry.DynamicRegistry;

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
    private static final List<Mapping<?, ?>> MAPPINGS;

    private record Mapping<O, T>(DataComponent<O> ourType, net.minestom.server.component.DataComponent<T> theirType, Function<O, T> toTheirType, Function<T, O> toOurType) {
        public <S> Mapping(DataComponent<S> ourType, net.minestom.server.component.DataComponent<S> theirType) {
            this((DataComponent<O>) ourType, (net.minestom.server.component.DataComponent<T>) theirType, s -> (T) s, s -> (O) s);
        }

        void set(ItemBuilder builder, ItemStack from) {
            builder.set(ourType, toOurType.apply(from.get(theirType)));
        }

        void set(ItemStack.Builder builder, ItemBuilder from) {
            builder.set(theirType, toTheirType.apply(from.get(ourType)));
        }
    }

    // region constructor
    public MinestomItemBuilderImpl() {
    }

    public MinestomItemBuilderImpl(@NotNull ItemStack item) {
        material(item.material());
        amount(item.amount());
        for (var i = 0; i < MAPPINGS.size(); i++) {
            var m = MAPPINGS.get(i);
            if (item.has(m.theirType)) {
                m.set(this, item);
            }
        }

        loadPersistentDataStorage();

        var b = build();
        if (!b.isSimilar(item)) {
            LOGGER.error("Failed to clone item correctly: ");
            LOGGER.error(" - {}", TagStringIOExt.writeTag(item.toItemNBT()));
            LOGGER.error(" - {}", TagStringIOExt.writeTag(b.toItemNBT()));
        }
    }
    // endregion

    public static MinestomItemBuilderImpl deserialize(JsonElement json) {
        return new MinestomItemBuilderImpl(gson.fromJson(json, ItemStack.class));
    }

    // region builder

    @Override
    public @NotNull ItemStack build() {
        return super.build();
    }

    @Override
    public @NotNull ItemStack build0() {
        var material = ((MinestomMaterialImpl) this.material).minestomType();
        var builder = ItemStack.builder(material);
        builder.amount(amount);
        for (var i = 0; i < MAPPINGS.size(); i++) {
            var m = MAPPINGS.get(i);
            if (has(m.ourType)) {
                m.set(builder, this);
            }
        }
        return builder.build();
    }
    // endregion

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
        this.attributeModifier(AttributeModifier.of(new net.minestom.server.item.component.AttributeList.Modifier(((MinestomAttribute) attribute).minestomType(), modifier, ((MinestomEquipmentSlotGroup) equipmentSlotGroup).minestomType())));
        return this;
    }

    private static final FireworkExplosion.Shape[] SHAPE_VALUES_CUSTOM = FireworkExplosion.Shape.values();
    private static final net.minestom.server.item.component.FireworkExplosion.Shape[] SHAPE_VALUES_MINESTOM = net.minestom.server.item.component.FireworkExplosion.Shape.values();
    private static final DyeColor[] DYE_COLOR_CUSTOM = DyeColor.values();
    private static final net.minestom.server.color.DyeColor[] DYE_COLOR_MINESTOM = net.minestom.server.color.DyeColor.values();
    private static final MapPostProcessing[] MAP_POST_PROCESSING_CUSTOM = MapPostProcessing.values();
    private static final net.minestom.server.item.component.MapPostProcessing[] MAP_POST_PROCESSING_MINESTOM = net.minestom.server.item.component.MapPostProcessing.values();
    private static final ItemRarity[] ITEM_RARITY_CUSTOM = ItemRarity.values();
    private static final net.minestom.server.item.component.ItemRarity[] ITEM_RARITY_MINESTOM = net.minestom.server.item.component.ItemRarity.values();

    private static net.minestom.server.item.component.WrittenBookContent convert(WrittenBookContent c) {
        return new net.minestom.server.item.component.WrittenBookContent(c.pages().stream().map(f -> new net.minestom.server.item.book.FilteredText<>(adventureSupport().convert(f.text()), f.filtered() == null ? null : adventureSupport().convert(f.filtered()))).toList(), new net.minestom.server.item.book.FilteredText<>(c.title().text(), c.title().filtered()), c.author(), c.generation(), c.resolved());
    }

    private static WrittenBookContent convert(net.minestom.server.item.component.WrittenBookContent c) {
        return new WrittenBookContent(c.pages().stream().map(f -> new FilteredText<>(adventureSupport().convert(f.text()), f.filtered() == null ? null : adventureSupport().convert(f.filtered()))).toList(), new FilteredText<>(c.title().text(), c.title().filtered()), c.author(), c.generation(), c.resolved());
    }

    private static net.minestom.server.item.component.Unbreakable convert(Unbreakable u) {
        return new net.minestom.server.item.component.Unbreakable(u.showInTooltip());
    }

    private static Unbreakable convert(net.minestom.server.item.component.Unbreakable u) {
        return new Unbreakable(u.showInTooltip());
    }

    private static net.minestom.server.item.component.WritableBookContent convert(WritableBookContent c) {
        return new net.minestom.server.item.component.WritableBookContent(c.pages().stream().map(f -> new net.minestom.server.item.book.FilteredText<>(f.text(), f.filtered())).toList());
    }

    private static WritableBookContent convert(net.minestom.server.item.component.WritableBookContent c) {
        return new WritableBookContent(c.pages().stream().map(f -> new FilteredText<>(f.text(), f.filtered())).toList());
    }

    private static net.minestom.server.item.component.ArmorTrim convert(ArmorTrim t) {
        return new net.minestom.server.item.component.ArmorTrim(DynamicRegistry.Key.of(t.material().asString()), DynamicRegistry.Key.of(t.pattern().asString()), t.showInTooltip());
    }

    private static ArmorTrim convert(net.minestom.server.item.component.ArmorTrim trim) {
        return new ArmorTrim(adventureSupport().convert(trim.material().key()), adventureSupport().convert(trim.pattern().key()), trim.showInTooltip());
    }

    private static net.minestom.server.item.component.Tool convert(Tool t) {
        return new net.minestom.server.item.component.Tool(t.rules().stream().map(s -> new net.minestom.server.item.component.Tool.Rule(convert(s.blocks()), s.speed(), s.correctForDrops())).toList(), t.defaultMiningSpeed(), t.damagePerBlock());
    }

    private static Tool convert(net.minestom.server.item.component.Tool t) {
        return new Tool(t.rules().stream().map(s -> new Tool.Rule(convert(s.blocks()), s.speed(), s.correctForDrops())).toList(), t.defaultMiningSpeed(), t.damagePerBlock());
    }

    private static net.minestom.server.item.component.SuspiciousStewEffects convert(SuspiciousStewEffects s) {
        return new net.minestom.server.item.component.SuspiciousStewEffects(s.effects().stream().map(e -> new net.minestom.server.item.component.SuspiciousStewEffects.Effect(Objects.requireNonNull(PotionEffect.fromNamespaceId(e.id().asString())), e.durationTicks())).toList());
    }

    private static SuspiciousStewEffects convert(net.minestom.server.item.component.SuspiciousStewEffects s) {
        return new SuspiciousStewEffects(s.effects().stream().map(e -> new SuspiciousStewEffects.Effect(adventureSupport().convert(e.id().key().key()), e.durationTicks())).toList());
    }

    private static net.minestom.server.item.component.ItemRarity convert(ItemRarity r) {
        return ITEM_RARITY_MINESTOM[r.ordinal()];
    }

    private static ItemRarity convert(net.minestom.server.item.component.ItemRarity r) {
        return ITEM_RARITY_CUSTOM[r.ordinal()];
    }

    private static net.minestom.server.item.component.HeadProfile convert(HeadProfile p) {
        return new net.minestom.server.item.component.HeadProfile(p.name(), p.uuid(), p.properties().stream().map(s -> new net.minestom.server.item.component.HeadProfile.Property(s.name(), s.value(), s.signature())).toList());
    }

    private static HeadProfile convert(net.minestom.server.item.component.HeadProfile headProfile) {
        return new HeadProfile(headProfile.name(), headProfile.uuid(), headProfile.properties().stream().map(p -> new HeadProfile.Property(p.name(), p.value(), p.signature())).toList());
    }

    private static net.minestom.server.item.component.PotionContents convert(PotionContents c) {
        return new net.minestom.server.item.component.PotionContents(Objects.requireNonNull(c.potion() == null ? null : PotionType.fromNamespaceId(c.potion().asString())), c.customColor() == null ? null : adventureSupport().convert(c.customColor()), c.customEffects().stream().map(MinestomItemBuilderImpl::convert).toList());
    }

    private static PotionContents convert(net.minestom.server.item.component.PotionContents c) {
        return new PotionContents(c.potion() == null ? null : adventureSupport().convert(c.potion().key()), c.customColor() == null ? null : adventureSupport().convert(c.customColor()), c.customEffects().stream().map(MinestomItemBuilderImpl::convert).toList());
    }

    private static net.minestom.server.item.component.PotDecorations convert(PotDecorations d) {
        return new net.minestom.server.item.component.PotDecorations(m(d.back()), m(d.left()), m(d.right()), m(d.front()));
    }

    private static net.minestom.server.item.Material m(Material m) {
        return ((MinestomMaterial) m).minestomType();
    }

    private static PotDecorations convert(net.minestom.server.item.component.PotDecorations d) {
        return new PotDecorations(Material.of(d.back()), Material.of(d.left()), Material.of(d.right()), Material.of(d.front()));
    }

    private static net.minestom.server.item.component.MapPostProcessing convert(MapPostProcessing p) {
        return MAP_POST_PROCESSING_MINESTOM[p.ordinal()];
    }

    private static MapPostProcessing convert(net.minestom.server.item.component.MapPostProcessing p) {
        return MAP_POST_PROCESSING_CUSTOM[p.ordinal()];
    }

    private static net.minestom.server.item.component.MapDecorations convert(MapDecorations d) {
        return new net.minestom.server.item.component.MapDecorations(Map.ofEntries(d.decorations().entrySet().stream().map(s -> Map.entry(s.getKey(), new net.minestom.server.item.component.MapDecorations.Entry(s.getValue().type(), s.getValue().x(), s.getValue().z(), s.getValue().rotation()))).toArray(Map.Entry[]::new)));
    }

    private static MapDecorations convert(net.minestom.server.item.component.MapDecorations d) {
        return new MapDecorations(Map.ofEntries(d.decorations().entrySet().stream().map(s -> Map.entry(s.getKey(), new MapDecorations.Entry(s.getValue().type(), s.getValue().x(), s.getValue().z(), s.getValue().rotation()))).toArray(Map.Entry[]::new)));
    }

    private static net.kyori.adventure.util.RGBLike convert(RGBLike r) {
        return adventureSupport().convert(r);
    }

    private static RGBLike convert(net.kyori.adventure.util.RGBLike r) {
        return adventureSupport().convert(r);
    }

    private static List<net.kyori.adventure.text.Component> convertComponentsC2P(List<Component> l) {
        return adventureSupport().convertComponentsC2P(l);
    }

    private static List<Component> convertComponentsP2C(List<net.kyori.adventure.text.Component> l) {
        return adventureSupport().convertComponentsP2C(l);
    }

    private static net.minestom.server.item.component.LodestoneTracker convert(LodestoneTracker t) {
        return new net.minestom.server.item.component.LodestoneTracker(t.target() == null ? null : new net.minestom.server.network.packet.server.play.data.WorldPos(t.target().dimension(), new Pos(t.target().x(), t.target().y(), t.target().z())), t.tracked());
    }

    private static LodestoneTracker convert(net.minestom.server.item.component.LodestoneTracker t) {
        return new LodestoneTracker(t.target() == null ? null : new WorldPos(t.target().dimension(), t.target().blockPosition().blockX(), t.target().blockPosition().blockY(), t.target().blockPosition().blockZ()), t.tracked());
    }

    private static net.minestom.server.item.component.JukeboxPlayable convert(JukeboxPlayable p) {
        return new net.minestom.server.item.component.JukeboxPlayable(DynamicRegistry.Key.of(p.song().asString()), p.showInTooltip());
    }

    private static JukeboxPlayable convert(net.minestom.server.item.component.JukeboxPlayable p) {
        return new JukeboxPlayable(adventureSupport().convert(p.song().key()), p.showInTooltip());
    }

    private static net.minestom.server.item.component.Food convert(Food f) {
        return new net.minestom.server.item.component.Food(f.nutrition(), f.saturationModifier(), f.canAlwaysEat(), f.eatSeconds(), f.usingConvertsTo() == null ? ItemStack.AIR : f.usingConvertsTo().build(), f.effects().stream().map(MinestomItemBuilderImpl::convert).toList());
    }

    private static Food convert(net.minestom.server.item.component.Food f) {
        return new Food(f.nutrition(), f.saturationModifier(), f.canAlwaysEat(), f.eatSeconds(), new MinestomItemBuilderImpl(f.usingConvertsTo()), f.effects().stream().map(MinestomItemBuilderImpl::convert).toList());
    }

    private static net.minestom.server.item.component.Food.EffectChance convert(Food.EffectChance e) {
        return new net.minestom.server.item.component.Food.EffectChance(convert(e.effect()), e.probability());
    }

    private static Food.EffectChance convert(net.minestom.server.item.component.Food.EffectChance e) {
        return new Food.EffectChance(convert(e.effect()), e.probability());
    }

    private static net.minestom.server.potion.CustomPotionEffect convert(CustomPotionEffect e) {
        var s = e.settings();
        return new net.minestom.server.potion.CustomPotionEffect(Objects.requireNonNull(PotionEffect.fromNamespaceId(e.id().asString())), new net.minestom.server.potion.CustomPotionEffect.Settings((byte) s.amplifier(), s.duration(), s.isAmbient(), s.showParticles(), s.showIcon(), null));
    }

    private static CustomPotionEffect convert(net.minestom.server.potion.CustomPotionEffect e) {
        var s = e.settings();
        return new CustomPotionEffect(adventureSupport().convert(e.id().key()), new CustomPotionEffect.Settings(s.amplifier(), s.duration(), s.isAmbient(), s.showParticles(), s.showIcon()));
    }

    private static net.minestom.server.item.component.FireworkList convert(FireworkList l) {
        return new net.minestom.server.item.component.FireworkList((byte) l.flightDuration(), l.explosions().stream().map(MinestomItemBuilderImpl::convert).toList());
    }

    private static FireworkList convert(net.minestom.server.item.component.FireworkList l) {
        return new FireworkList(l.flightDuration(), l.explosions().stream().map(MinestomItemBuilderImpl::convert).toList());
    }

    private static net.minestom.server.item.component.FireworkExplosion convert(FireworkExplosion e) {
        return new net.minestom.server.item.component.FireworkExplosion(convert(e.shape()), adventureSupport().convertRGBLikeC2P(e.colors()), adventureSupport().convertRGBLikeC2P(e.fadeColors()), e.hasTrail(), e.hasTwinkle());
    }

    private static FireworkExplosion convert(net.minestom.server.item.component.FireworkExplosion explosion) {
        return new FireworkExplosion(convert(explosion.shape()), adventureSupport().convertRGBLikeP2C(explosion.colors()), adventureSupport().convertRGBLikeP2C(explosion.fadeColors()), explosion.hasTrail(), explosion.hasTwinkle());
    }

    private static net.minestom.server.item.component.FireworkExplosion.Shape convert(FireworkExplosion.Shape shape) {
        return SHAPE_VALUES_MINESTOM[shape.ordinal()];
    }

    private static FireworkExplosion.Shape convert(net.minestom.server.item.component.FireworkExplosion.Shape shape) {
        return SHAPE_VALUES_CUSTOM[shape.ordinal()];
    }

    private static net.minestom.server.item.component.EnchantmentList convert(EnchantmentList e) {
        return new net.minestom.server.item.component.EnchantmentList(Map.ofEntries(e.enchantments().entrySet().stream().map(m -> Map.entry(DynamicRegistry.Key.of(m.getKey().asString()), m.getValue())).toArray(Map.Entry[]::new)), e.showInTooltip());
    }

    private static EnchantmentList convert(net.minestom.server.item.component.EnchantmentList enchantmentList) {
        return new EnchantmentList(Map.ofEntries(enchantmentList.enchantments().entrySet().stream().map(m -> Map.entry(adventureSupport().convert(m.getKey().key()), m.getValue())).toArray(Map.Entry[]::new)), enchantmentList.showInTooltip());
    }

    private static net.minestom.server.item.component.DyedItemColor convert(DyedItemColor c) {
        return new net.minestom.server.item.component.DyedItemColor(adventureSupport().convert(c.color()), c.showInTooltip());
    }

    private static DyedItemColor convert(net.minestom.server.item.component.DyedItemColor color) {
        return new DyedItemColor(adventureSupport().convert(color.color()), color.showInTooltip());
    }

    private static net.minestom.server.item.component.DebugStickState convert(DebugStickState s) {
        return new net.minestom.server.item.component.DebugStickState(s.state());
    }

    private static DebugStickState convert(net.minestom.server.item.component.DebugStickState s) {
        return new DebugStickState(s.state());
    }

    private static net.kyori.adventure.text.Component convert(Component c) {
        return adventureSupport().convert(c);
    }

    private static Component convert(net.kyori.adventure.text.Component c) {
        return adventureSupport().convert(c);
    }

    private static net.minestom.server.utils.Unit convert(Unit unit) {
        return net.minestom.server.utils.Unit.INSTANCE;
    }

    private static Unit convert(net.minestom.server.utils.Unit unit) {
        return Unit.INSTANCE;
    }

    private static net.minestom.server.item.component.SeededContainerLoot convert(SeededContainerLoot loot) {
        return new net.minestom.server.item.component.SeededContainerLoot(loot.lootTable(), loot.seed());
    }

    private static SeededContainerLoot convert(net.minestom.server.item.component.SeededContainerLoot loot) {
        return new SeededContainerLoot(loot.lootTable(), loot.seed());
    }

    private static net.minestom.server.item.component.BlockPredicates convert(BlockPredicates p) {
        return new net.minestom.server.item.component.BlockPredicates(p.predicates().stream().map(MinestomItemBuilderImpl::convert).toList(), p.showInTooltip());
    }

    private static BlockPredicates convert(net.minestom.server.item.component.BlockPredicates p) {
        return new BlockPredicates(p.predicates().stream().map(MinestomItemBuilderImpl::convert).toList(), p.showInTooltip());
    }

    private static BlockPredicate convert(BlockPredicates.BlockPredicate p) {
        return new BlockPredicate(p.blocks() == null ? null : convert(p.blocks()), p.state() == null ? null : convert(p.state()), p.nbt() == null ? null : convert(p.nbt()));
    }

    private static BlockPredicates.BlockPredicate convert(BlockPredicate p) {
        return new BlockPredicates.BlockPredicate(p.blocks() == null ? null : convert(p.blocks()), p.state() == null ? null : convert(p.state()), p.nbt() == null ? null : adventureSupport().convert(p.nbt()));
    }

    private static net.minestom.server.instance.block.predicate.BlockTypeFilter convert(BlockTypeFilter f) {
        return switch (f) {
            case BlockTypeFilter.Blocks(var blocks) -> new net.minestom.server.instance.block.predicate.BlockTypeFilter.Blocks(blocks.stream().map(b -> Block.fromNamespaceId(((MinestomMaterial) b).minestomType().key().asString())).toList());
            case BlockTypeFilter.Tag(var tag) -> new net.minestom.server.instance.block.predicate.BlockTypeFilter.Tag(tag.asString());
        };
    }

    private static BlockTypeFilter convert(net.minestom.server.instance.block.predicate.BlockTypeFilter f) {
        return switch (f) {
            case net.minestom.server.instance.block.predicate.BlockTypeFilter.Blocks(var blocks) -> new BlockTypeFilter.Blocks(blocks.stream().map(b -> Material.of(Objects.requireNonNull(b.registry().material()))).toList());
            case net.minestom.server.instance.block.predicate.BlockTypeFilter.Tag(var tag) -> new BlockTypeFilter.Tag(adventureSupport().convert(tag.key()));
        };
    }

    private static PropertiesPredicate convert(BlockPredicates.PropertiesPredicate p) {
        return new PropertiesPredicate(Map.ofEntries(p.properties().entrySet().stream().map(e -> Map.entry(e.getKey(), convert(e.getValue()))).toArray(Map.Entry[]::new)));
    }

    private static BlockPredicates.PropertiesPredicate convert(PropertiesPredicate p) {
        return new BlockPredicates.PropertiesPredicate(Map.ofEntries(p.properties().entrySet().stream().map(e -> Map.entry(e.getKey(), convert(e.getValue()))).toArray(Map.Entry[]::new)));
    }

    private static PropertiesPredicate.ValuePredicate convert(BlockPredicates.PropertiesPredicate.ValuePredicate p) {
        return switch (p) {
            case BlockPredicates.PropertiesPredicate.ValuePredicate.Exact exact -> new PropertiesPredicate.ValuePredicate.Exact(exact.value());
            case BlockPredicates.PropertiesPredicate.ValuePredicate.Range range -> new PropertiesPredicate.ValuePredicate.Range(range.min(), range.max());
        };
    }

    private static BlockPredicates.PropertiesPredicate.ValuePredicate convert(PropertiesPredicate.ValuePredicate p) {
        return switch (p) {
            case PropertiesPredicate.ValuePredicate.Exact exact -> new BlockPredicates.PropertiesPredicate.ValuePredicate.Exact(exact.value());
            case PropertiesPredicate.ValuePredicate.Range range -> new BlockPredicates.PropertiesPredicate.ValuePredicate.Range(range.min(), range.max());
        };
    }

    private static List<ItemBuilder> convertItemsP2C(List<ItemStack> items) {
        return items.stream().<ItemBuilder>map(MinestomItemBuilderImpl::new).toList();
    }

    private static List<ItemStack> convertItemsC2P(List<ItemBuilder> items) {
        return items.stream().<ItemStack>map(ItemBuilder::build).toList();
    }

    private static ItemBlockState convert(net.minestom.server.item.component.ItemBlockState state) {
        return new ItemBlockState(state.properties());
    }

    private static net.minestom.server.item.component.ItemBlockState convert(ItemBlockState state) {
        return new net.minestom.server.item.component.ItemBlockState(state.properties());
    }

    private static List<Bee> convertBeesP2C(List<net.minestom.server.item.component.Bee> bees) {
        return bees.stream().map(b -> new Bee(convertData(b.entityData()), b.ticksInHive(), b.minTicksInHive())).toList();
    }

    private static List<net.minestom.server.item.component.Bee> convertBeesC2P(List<Bee> bees) {
        return bees.stream().map(b -> new net.minestom.server.item.component.Bee(convertData(b.entityData()), b.ticksInHive(), b.minTicksInHive())).toList();
    }

    private static CompoundBinaryTag convertData(CustomData d) {
        return convert(d.nbt());
    }

    private static CustomData convertData(CompoundBinaryTag nbt) {
        return new CustomData(adventureSupport().convert(nbt));
    }

    private static CompoundBinaryTag convert(net.kyori.adventure.nbt.CompoundBinaryTag t) {
        return adventureSupport().convert(t);
    }

    private static net.kyori.adventure.nbt.CompoundBinaryTag convert(CompoundBinaryTag t) {
        return adventureSupport().convert(t);
    }

    private static BannerPatterns convert(net.minestom.server.item.component.BannerPatterns patterns) {
        return new BannerPatterns(patterns.layers().stream().map(l -> new BannerPatterns.Layer(adventureSupport().convert(l.pattern().key()), convert(l.color()))).toList());
    }

    private static net.minestom.server.item.component.BannerPatterns convert(BannerPatterns patterns) {
        return new net.minestom.server.item.component.BannerPatterns(patterns.layers().stream().map(l -> new net.minestom.server.item.component.BannerPatterns.Layer(DynamicRegistry.Key.of(l.pattern().asString()), convert(l.color()))).toList());
    }

    private static DyeColor convert(net.minestom.server.color.DyeColor color) {
        return DYE_COLOR_CUSTOM[color.ordinal()];
    }

    private static net.minestom.server.color.DyeColor convert(DyeColor color) {
        return DYE_COLOR_MINESTOM[color.ordinal()];
    }

    private static AttributeList convert(net.minestom.server.item.component.AttributeList attributeList) {
        var modifiers = new ArrayList<AttributeModifier>();
        for (var m : attributeList.modifiers()) {
            var modifier = AttributeModifier.of(Attribute.of(m.attribute()), KeyProvider.get(m.modifier().id()), EquipmentSlotGroup.of(m.slot()), m.modifier().amount(), AttributeModifierOperation.of(m.modifier().operation()));
            modifiers.add(modifier);
        }
        return new AttributeList(modifiers, attributeList.showInTooltip());
    }

    private static net.minestom.server.item.component.AttributeList convert(AttributeList attributeList) {
        var modifiers = new ArrayList<net.minestom.server.item.component.AttributeList.Modifier>();
        for (var m : attributeList.modifiers()) {
            var modifier = new net.minestom.server.item.component.AttributeList.Modifier(((MinestomAttribute) m.attribute()).minestomType(), new net.minestom.server.entity.attribute.AttributeModifier(m.key().asString(), m.amount(), ((MinestomAttributeModifierOperation) m.operation()).minestomType()), ((MinestomEquipmentSlotGroup) m.equipmentSlotGroup()).minestomType());
            modifiers.add(modifier);
        }
        return new net.minestom.server.item.component.AttributeList(modifiers, attributeList.showInTooltip());
    }

    static {
        var m = new ArrayList<Mapping<?, ?>>();
        m.add(new Mapping<>(ATTRIBUTE_MODIFIERS, ItemComponent.ATTRIBUTE_MODIFIERS, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(BANNER_PATTERNS, ItemComponent.BANNER_PATTERNS, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(BASE_COLOR, ItemComponent.BASE_COLOR, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(BEES, ItemComponent.BEES, MinestomItemBuilderImpl::convertBeesC2P, MinestomItemBuilderImpl::convertBeesP2C));
        m.add(new Mapping<>(BLOCK_ENTITY_DATA, ItemComponent.BLOCK_ENTITY_DATA, MinestomItemBuilderImpl::convertData, MinestomItemBuilderImpl::convertData));
        m.add(new Mapping<>(BLOCK_STATE, ItemComponent.BLOCK_STATE, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(BUCKET_ENTITY_DATA, ItemComponent.BUCKET_ENTITY_DATA, MinestomItemBuilderImpl::convertData, MinestomItemBuilderImpl::convertData));
        m.add(new Mapping<>(BUNDLE_CONTENTS, ItemComponent.BUNDLE_CONTENTS, MinestomItemBuilderImpl::convertItemsC2P, MinestomItemBuilderImpl::convertItemsP2C));
        m.add(new Mapping<>(CAN_BREAK, ItemComponent.CAN_BREAK, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(CAN_PLACE_ON, ItemComponent.CAN_PLACE_ON, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(CHARGED_PROJECTILES, ItemComponent.CHARGED_PROJECTILES, MinestomItemBuilderImpl::convertItemsC2P, MinestomItemBuilderImpl::convertItemsP2C));
        m.add(new Mapping<>(CONTAINER, ItemComponent.CONTAINER, MinestomItemBuilderImpl::convertItemsC2P, MinestomItemBuilderImpl::convertItemsP2C));
        m.add(new Mapping<>(CONTAINER_LOOT, ItemComponent.CONTAINER_LOOT, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(CREATIVE_SLOT_LOCK, ItemComponent.CREATIVE_SLOT_LOCK, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(CUSTOM_DATA, ItemComponent.CUSTOM_DATA, MinestomItemBuilderImpl::convertData, MinestomItemBuilderImpl::convertData));
        m.add(new Mapping<>(CUSTOM_MODEL_DATA, ItemComponent.CUSTOM_MODEL_DATA));
        m.add(new Mapping<>(CUSTOM_NAME, ItemComponent.CUSTOM_NAME, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(DAMAGE, ItemComponent.DAMAGE));
        m.add(new Mapping<>(DEBUG_STICK_STATE, ItemComponent.DEBUG_STICK_STATE, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(DYED_COLOR, ItemComponent.DYED_COLOR, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(ENCHANTMENT_GLINT_OVERRIDE, ItemComponent.ENCHANTMENT_GLINT_OVERRIDE));
        m.add(new Mapping<>(ENCHANTMENTS, ItemComponent.ENCHANTMENTS, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(ENTITY_DATA, ItemComponent.ENTITY_DATA, MinestomItemBuilderImpl::convertData, MinestomItemBuilderImpl::convertData));
        m.add(new Mapping<>(FIRE_RESISTANT, ItemComponent.FIRE_RESISTANT, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(FIREWORK_EXPLOSION, ItemComponent.FIREWORK_EXPLOSION, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(FIREWORKS, ItemComponent.FIREWORKS, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(FOOD, ItemComponent.FOOD, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(HIDE_ADDITIONAL_TOOLTIP, ItemComponent.HIDE_ADDITIONAL_TOOLTIP, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(HIDE_TOOLTIP, ItemComponent.HIDE_TOOLTIP, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(INSTRUMENT, ItemComponent.INSTRUMENT));
        m.add(new Mapping<>(INTANGIBLE_PROJECTILE, ItemComponent.INTANGIBLE_PROJECTILE, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(ITEM_NAME, ItemComponent.ITEM_NAME, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(JUKEBOX_PLAYABLE, ItemComponent.JUKEBOX_PLAYABLE, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(LOCK, ItemComponent.LOCK));
        m.add(new Mapping<>(LODESTONE_TRACKER, ItemComponent.LODESTONE_TRACKER, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(LORE, ItemComponent.LORE, MinestomItemBuilderImpl::convertComponentsC2P, MinestomItemBuilderImpl::convertComponentsP2C));
        m.add(new Mapping<>(MAP_COLOR, ItemComponent.MAP_COLOR, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(MAP_DECORATIONS, ItemComponent.MAP_DECORATIONS, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(MAP_ID, ItemComponent.MAP_ID));
        m.add(new Mapping<>(MAP_POST_PROCESSING, ItemComponent.MAP_POST_PROCESSING, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(MAX_DAMAGE, ItemComponent.MAX_DAMAGE));
        m.add(new Mapping<>(MAX_STACK_SIZE, ItemComponent.MAX_STACK_SIZE));
        m.add(new Mapping<>(NOTE_BLOCK_SOUND, ItemComponent.NOTE_BLOCK_SOUND));
        m.add(new Mapping<>(OMINOUS_BOTTLE_AMPLIFIER, ItemComponent.OMINOUS_BOTTLE_AMPLIFIER));
        m.add(new Mapping<>(POT_DECORATIONS, ItemComponent.POT_DECORATIONS, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(POTION_CONTENTS, ItemComponent.POTION_CONTENTS, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(PROFILE, ItemComponent.PROFILE, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(RARITY, ItemComponent.RARITY, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(RECIPES, ItemComponent.RECIPES));
        m.add(new Mapping<>(REPAIR_COST, ItemComponent.REPAIR_COST));
        m.add(new Mapping<>(STORED_ENCHANTMENTS, ItemComponent.STORED_ENCHANTMENTS, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(SUSPICIOUS_STEW_EFFECTS, ItemComponent.SUSPICIOUS_STEW_EFFECTS, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(TOOL, ItemComponent.TOOL, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(TRIM, ItemComponent.TRIM, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(UNBREAKABLE, ItemComponent.UNBREAKABLE, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(WRITABLE_BOOK_CONTENT, ItemComponent.WRITABLE_BOOK_CONTENT, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));
        m.add(new Mapping<>(WRITTEN_BOOK_CONTENT, ItemComponent.WRITTEN_BOOK_CONTENT, MinestomItemBuilderImpl::convert, MinestomItemBuilderImpl::convert));

        MAPPINGS = List.copyOf(m);
    }
}
