/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util;

import static eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport.adventureSupport;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.ByteArrayBinaryTag.byteArrayBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.ByteBinaryTag.byteBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.DoubleBinaryTag.doubleBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.EndBinaryTag.endBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.FloatBinaryTag.floatBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.IntArrayBinaryTag.intArrayBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.IntBinaryTag.intBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.LongArrayBinaryTag.longArrayBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.LongBinaryTag.longBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.ShortBinaryTag.shortBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.StringBinaryTag.stringBinaryTag;
import static net.minecraft.world.item.DyeColor.*;

import java.util.Collection;
import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ByteArrayBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ByteBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.DoubleBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.EndBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.FloatBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.IntArrayBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.IntBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ListBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.LongArrayBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.LongBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ShortBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.StringBinaryTag;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.components.BlockPredicates;
import eu.darkcube.system.server.item.component.components.util.CustomPotionEffect;
import eu.darkcube.system.server.item.component.components.util.DyeColor;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapperUtil.class);

    public static Component convert(eu.darkcube.system.libs.net.kyori.adventure.text.Component component) {
        return PaperAdventure.asVanilla(adventureSupport().convert(component));
    }

    public static eu.darkcube.system.libs.net.kyori.adventure.text.Component convert(Component component) {
        return adventureSupport().convert(PaperAdventure.asAdventure(component));
    }

    public static MobEffectInstance convert(CustomPotionEffect e) {
        var id = ResourceLocation.parse(e.id().asString());
        var holder = BuiltInRegistries.MOB_EFFECT.getHolder(id).orElseThrow();
        var s = e.settings();
        return new MobEffectInstance(holder, s.duration(), s.amplifier(), s.isAmbient(), s.showParticles(), s.showIcon());
    }

    public static CustomPotionEffect convert(MobEffectInstance e) {
        return new CustomPotionEffect(Key.key(e.getEffect().unwrapKey().orElseThrow().location().toString()), new CustomPotionEffect.Settings(e.getAmplifier(), e.getDuration(), e.isAmbient(), e.isVisible(), e.showIcon()));
    }

    public static AdventureModePredicate convert(BlockPredicates predicates) {
        var compound = BlockPredicates.SERIALIZER.write(predicates);
        var result = AdventureModePredicate.CODEC.decode(NbtOps.INSTANCE, convert(compound));
        if (result.isSuccess()) return result.getOrThrow().getFirst();
        LOGGER.error("Error converting BlockPredicates for ItemBuilder: {}", result.error().orElseThrow().message());
        return new AdventureModePredicate(List.of(), false);
    }

    public static BlockPredicates convert(AdventureModePredicate predicate) {
        var result = AdventureModePredicate.CODEC.encodeStart(NbtOps.INSTANCE, predicate);
        if (result.isSuccess()) return BlockPredicates.SERIALIZER.read(convert(result.getOrThrow()));
        LOGGER.error("Error converting BlockPredicates for ItemBuilder: {}", result.error().orElseThrow().message());
        return BlockPredicates.NEVER;
    }

    public static List<ItemStack> convertItemsToMinecraft(Collection<ItemBuilder> items) {
        return items.stream().map(MapperUtil::convert).toList();
    }

    public static List<ItemBuilder> convertItemsToBuilder(Collection<ItemStack> items) {
        return items.stream().map(MapperUtil::convert).toList();
    }

    public static ItemBuilder convert(ItemStack item) {
        return ItemBuilder.item(CraftItemStack.asCraftMirror(item));
    }

    public static ItemStack convert(ItemBuilder builder) {
        return CraftItemStack.unwrap(builder.build());
    }

    public static CustomData convertData(CompoundBinaryTag tag) {
        return CustomData.of(convert(tag));
    }

    public static CompoundBinaryTag convertData(CustomData data) {
        return convert(data.copyTag());
    }

    public static CompoundTag convert(CompoundBinaryTag tag) {
        var c = new CompoundTag();
        for (var key : tag.keySet()) {
            c.put(key, convert(tag.get(key)));
        }
        return c;
    }

    public static CompoundBinaryTag convert(CompoundTag tag) {
        var b = CompoundBinaryTag.builder();
        for (var key : tag.getAllKeys()) {
            b.put(key, convert(tag.get(key)));
        }
        return b.build();
    }

    public static ListTag convert(ListBinaryTag tag) {
        var list = new ListTag();
        for (var binaryTag : tag) {
            list.add(convert(binaryTag));
        }
        return list;
    }

    public static ListBinaryTag convert(ListTag tag) {
        var b = ListBinaryTag.builder();
        for (var t : tag) {
            b.add(convert(t));
        }
        return b.build();
    }

    public static BinaryTag convert(Tag tag) {
        return switch (tag) {
            case CompoundTag c -> convert(c);
            case StringTag s -> stringBinaryTag(s.getAsString());
            case ByteTag b -> byteBinaryTag(b.getAsByte());
            case ShortTag s -> shortBinaryTag(s.getAsShort());
            case IntTag i -> intBinaryTag(i.getAsInt());
            case LongTag l -> longBinaryTag(l.getAsLong());
            case ByteArrayTag b -> byteArrayBinaryTag(b.getAsByteArray());
            case IntArrayTag i -> intArrayBinaryTag(i.getAsIntArray());
            case LongArrayTag l -> longArrayBinaryTag(l.getAsLongArray());
            case DoubleTag d -> doubleBinaryTag(d.getAsDouble());
            case FloatTag f -> floatBinaryTag(f.getAsFloat());
            case ListTag l -> convert(l);
            case EndTag _ -> endBinaryTag();
            case null -> throw new NullPointerException("Can't use null as input");
            default -> throw new IllegalArgumentException("Tag type " + tag.getType().getPrettyName() + " is not supported");
        };
    }

    public static Tag convert(BinaryTag tag) {
        return switch (tag) {
            case CompoundBinaryTag c -> convert(c);
            case StringBinaryTag s -> StringTag.valueOf(s.value());
            case ByteBinaryTag b -> ByteTag.valueOf(b.value());
            case ShortBinaryTag s -> ShortTag.valueOf(s.value());
            case IntBinaryTag i -> IntTag.valueOf(i.value());
            case LongBinaryTag l -> LongTag.valueOf(l.value());
            case ByteArrayBinaryTag b -> new ByteArrayTag(b.value());
            case IntArrayBinaryTag i -> new IntArrayTag(i.value());
            case LongArrayBinaryTag l -> new LongArrayTag(l.value());
            case DoubleBinaryTag d -> DoubleTag.valueOf(d.value());
            case FloatBinaryTag f -> FloatTag.valueOf(f.value());
            case ListBinaryTag l -> convert(l);
            case EndBinaryTag _ -> EndTag.INSTANCE;
            case null -> throw new NullPointerException("Can't use null as input");
            default -> throw new IllegalArgumentException("Tag type " + tag.type() + " is not supported");
        };
    }

    public static net.minecraft.world.item.DyeColor convert(DyeColor color) {
        return switch (color) {
            case WHITE -> WHITE;
            case ORANGE -> ORANGE;
            case MAGENTA -> MAGENTA;
            case LIGHT_BLUE -> LIGHT_BLUE;
            case YELLOW -> YELLOW;
            case LIME -> LIME;
            case PINK -> PINK;
            case GRAY -> GRAY;
            case LIGHT_GRAY -> LIGHT_GRAY;
            case CYAN -> CYAN;
            case PURPLE -> PURPLE;
            case BLUE -> BLUE;
            case BROWN -> BROWN;
            case GREEN -> GREEN;
            case RED -> RED;
            case BLACK -> BLACK;
        };
    }

    public static DyeColor convert(net.minecraft.world.item.DyeColor color) {
        return switch (color) {
            case WHITE -> DyeColor.WHITE;
            case ORANGE -> DyeColor.ORANGE;
            case MAGENTA -> DyeColor.MAGENTA;
            case LIGHT_BLUE -> DyeColor.LIGHT_BLUE;
            case YELLOW -> DyeColor.YELLOW;
            case LIME -> DyeColor.LIME;
            case PINK -> DyeColor.PINK;
            case GRAY -> DyeColor.GRAY;
            case LIGHT_GRAY -> DyeColor.LIGHT_GRAY;
            case CYAN -> DyeColor.CYAN;
            case PURPLE -> DyeColor.PURPLE;
            case BLUE -> DyeColor.BLUE;
            case BROWN -> DyeColor.BROWN;
            case GREEN -> DyeColor.GREEN;
            case RED -> DyeColor.RED;
            case BLACK -> DyeColor.BLACK;
        };
    }
}