/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import static eu.darkcube.system.libs.net.kyori.adventure.nbt.ByteArrayBinaryTag.byteArrayBinaryTag;
import static eu.darkcube.system.libs.net.kyori.adventure.nbt.ByteBinaryTag.byteBinaryTag;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import eu.darkcube.system.bukkit.util.ReflectionUtils;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
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
import eu.darkcube.system.libs.net.kyori.adventure.nbt.LongBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ShortBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.StringBinaryTag;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagByteArray;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagEnd;
import net.minecraft.server.v1_8_R3.NBTTagFloat;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagIntArray;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagLong;
import net.minecraft.server.v1_8_R3.NBTTagShort;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CustomDataMapper implements Mapper<CompoundBinaryTag> {
    private static final Field CraftMetaItem$unhandledTags = ReflectionUtils.getField(ReflectionUtils.getClass("CraftMetaItem", ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY), true, "unhandledTags");
    private static final MethodHandle NBTBase$createTag;

    @Override
    public void apply(CompoundBinaryTag mapping, ItemStack item, ItemMeta meta) {
        var tags = (Map<String, NBTBase>) ReflectionUtils.getValue(meta, CraftMetaItem$unhandledTags);
        var compound = convert(mapping);
        for (var key : compound.c()) {
            tags.put(key, compound.get(key));
        }
    }

    @Override
    public @Nullable CompoundBinaryTag convert(ItemStack item, ItemMeta meta) {
        var tags = (Map<String, NBTBase>) ReflectionUtils.getValue(meta, CraftMetaItem$unhandledTags);
        if (tags.isEmpty()) return null;
        var compound = new NBTTagCompound();
        tags.forEach(compound::set);
        return convert(compound);
    }

    private BinaryTag convert(NBTBase nbt) {
        return switch (nbt) {
            case NBTTagByte b -> byteBinaryTag(b.f());
            case NBTTagByteArray b -> byteArrayBinaryTag(b.c());
            case NBTTagCompound c -> convert(c);
            case NBTTagDouble d -> DoubleBinaryTag.doubleBinaryTag(d.g());
            case NBTTagFloat f -> FloatBinaryTag.floatBinaryTag(f.h());
            case NBTTagInt i -> IntBinaryTag.intBinaryTag(i.d());
            case NBTTagIntArray i -> IntArrayBinaryTag.intArrayBinaryTag(i.c());
            case NBTTagLong l -> LongBinaryTag.longBinaryTag(l.c());
            case NBTTagShort s -> ShortBinaryTag.shortBinaryTag(s.e());
            case NBTTagString s -> StringBinaryTag.stringBinaryTag(s.a_());
            case NBTTagList l -> convert(l);
            case NBTTagEnd _ -> EndBinaryTag.endBinaryTag();
            case null -> throw new NullPointerException("Can't use null as input");
            default -> throw new IllegalArgumentException("Tag type " + nbt.getClass().getSimpleName() + " is not supported");
        };
    }

    private NBTBase convert(BinaryTag tag) {
        return switch (tag) {
            case CompoundBinaryTag c -> convert(c);
            case StringBinaryTag s -> new NBTTagString(s.value());
            case ByteBinaryTag b -> new NBTTagByte(b.value());
            case IntBinaryTag i -> new NBTTagInt(i.value());
            case LongBinaryTag l -> new NBTTagLong(l.value());
            case ShortBinaryTag s -> new NBTTagShort(s.value());
            case ByteArrayBinaryTag b -> new NBTTagByteArray(b.value());
            case IntArrayBinaryTag i -> new NBTTagIntArray(i.value());
            case DoubleBinaryTag d -> new NBTTagDouble(d.value());
            case FloatBinaryTag f -> new NBTTagFloat(f.value());
            case ListBinaryTag l -> convert(l);
            case EndBinaryTag _ -> createEnd();
            case null -> throw new NullPointerException("Can't use null as input");
            default -> throw new IllegalArgumentException("Tag type " + tag.type() + " is not supported");
        };
    }

    private NBTTagList convert(ListBinaryTag tag) {
        var list = new NBTTagList();
        for (var binaryTag : tag) {
            list.add(convert(binaryTag));
        }
        return list;
    }

    private ListBinaryTag convert(NBTTagList l) {
        var tags = new ArrayList<BinaryTag>();
        for (var i = 0; i < l.size(); i++) {
            var tag = l.g(i);
            tags.add(convert(tag));
        }
        return ListBinaryTag.from(tags);
    }

    private CompoundBinaryTag convert(NBTTagCompound compound) {
        var builder = CompoundBinaryTag.builder();
        for (var key : compound.c()) {
            builder.put(key, convert(compound.get(key)));
        }
        return builder.build();
    }

    private NBTTagCompound convert(CompoundBinaryTag tag) {
        var t = new NBTTagCompound();
        for (var key : tag.keySet()) {
            t.set(key, convert(tag.get(key)));
        }
        return t;
    }

    private static NBTTagEnd createEnd() {
        try {
            return (NBTTagEnd) (NBTBase) NBTBase$createTag.invokeExact((byte) 0);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            var lookup = MethodHandles.lookup();
            NBTBase$createTag = lookup.findStatic(NBTBase.class, "createTag", MethodType.methodType(NBTBase.class, byte.class));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
