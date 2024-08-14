/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import static eu.darkcube.system.libs.net.kyori.adventure.nbt.StringBinaryTag.stringBinaryTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTagType;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTagTypes;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ByteBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.FloatBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.IntBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ListBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.NumberBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.StringBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.TagStringIOExt;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;

public interface BinaryTagSerializer<T> {

    static <T> @NotNull BinaryTagSerializer<T> recursive(@NotNull Function<BinaryTagSerializer<T>, BinaryTagSerializer<T>> self) {
        return new BinaryTagSerializer<>() {
            private BinaryTagSerializer<T> serializer = null;

            @Override
            public @NotNull BinaryTag write(@UnknownNullability T value) {
                return serializer().write(value);
            }

            @Override
            public @NotNull T read(@UnknownNullability BinaryTag tag) {
                return serializer().read(tag);
            }

            private BinaryTagSerializer<T> serializer() {
                if (serializer == null) serializer = self.apply(this);
                return serializer;
            }
        };
    }

    static <T> @NotNull BinaryTagSerializer<T> lazy(@NotNull Supplier<BinaryTagSerializer<T>> self) {
        return new BinaryTagSerializer<>() {
            private BinaryTagSerializer<T> serializer = null;

            @Override
            public @NotNull BinaryTag write(@UnknownNullability T value) {
                return serializer().write(value);
            }

            @Override
            public @NotNull T read(@UnknownNullability BinaryTag tag) {
                return serializer().read(tag);
            }

            private BinaryTagSerializer<T> serializer() {
                if (serializer == null) serializer = self.get();
                return serializer;
            }
        };
    }

    static <T extends BinaryTag> @NotNull BinaryTagSerializer<T> coerced(@NotNull BinaryTagType<T> type) {
        return new BinaryTagSerializer<>() {
            @Override
            public @NotNull BinaryTag write(@UnknownNullability T value) {
                return value;
            }

            @Override
            public @NotNull T read(@UnknownNullability BinaryTag tag) {
                if (tag.type() == type) {
                    return (T) tag;
                }

                if (tag instanceof StringBinaryTag string) {
                    try {
                        tag = TagStringIOExt.readTag(string.value());
                        if (tag.type() == type) {
                            return (T) tag;
                        }
                    } catch (IOException e) {
                        // Ignored, we'll throw a more useful exception below
                    }
                }

                throw new IllegalArgumentException("Expected " + type + " but got " + tag);
            }
        };
    }

    static <E extends Enum<E>> @NotNull BinaryTagSerializer<E> fromEnumStringable(@NotNull Class<E> enumClass) {
        final var values = enumClass.getEnumConstants();
        final var nameMap = Arrays.stream(values).collect(Collectors.toMap(e -> e.name().toLowerCase(Locale.ROOT), Function.identity()));
        return new BinaryTagSerializer<>() {
            @Override
            public @NotNull BinaryTag write(@UnknownNullability E value) {
                return stringBinaryTag(value.name().toLowerCase(Locale.ROOT));
            }

            @Override
            public @NotNull E read(@UnknownNullability BinaryTag tag) {
                return switch (tag) {
                    case IntBinaryTag intBinaryTag -> values[intBinaryTag.value()];
                    case StringBinaryTag string -> nameMap.getOrDefault(string.value().toLowerCase(Locale.ROOT), values[0]);
                    default -> values[0];
                };
            }
        };
    }

    BinaryTagSerializer<Unit> UNIT = new BinaryTagSerializer<>() {
        @Override
        public @NotNull BinaryTag write(@UnknownNullability Unit value) {
            return CompoundBinaryTag.empty();
        }

        @Override
        public @NotNull Unit read(@UnknownNullability BinaryTag tag) {
            return Unit.INSTANCE;
        }
    };

    BinaryTagSerializer<Byte> BYTE = new BinaryTagSerializer<>() {
        @Override
        public @NotNull BinaryTag write(@UnknownNullability Byte value) {
            return ByteBinaryTag.byteBinaryTag(value);
        }

        @Override
        public @NotNull Byte read(@UnknownNullability BinaryTag tag) {
            return tag instanceof ByteBinaryTag byteBinaryTag ? byteBinaryTag.value() : 0;
        }
    };

    BinaryTagSerializer<Boolean> BOOLEAN = BYTE.map(b -> b != 0, b -> (byte) (b ? 1 : 0));

    BinaryTagSerializer<Integer> INT = new BinaryTagSerializer<>() {
        @Override
        public @NotNull BinaryTag write(@UnknownNullability Integer value) {
            return IntBinaryTag.intBinaryTag(value);
        }

        @Override
        public @NotNull Integer read(@UnknownNullability BinaryTag tag) {
            return tag instanceof NumberBinaryTag numberTag ? numberTag.intValue() : 0;
        }
    };

    BinaryTagSerializer<Float> FLOAT = new BinaryTagSerializer<>() {
        @Override
        public @NotNull BinaryTag write(@UnknownNullability Float value) {
            return FloatBinaryTag.floatBinaryTag(value);
        }

        @Override
        public @NotNull Float read(@UnknownNullability BinaryTag tag) {
            return tag instanceof NumberBinaryTag numberTag ? numberTag.floatValue() : 0f;
        }
    };

    BinaryTagSerializer<String> STRING = new BinaryTagSerializer<>() {
        @Override
        public @NotNull BinaryTag write(@UnknownNullability String value) {
            return stringBinaryTag(value);
        }

        @Override
        public @NotNull String read(@UnknownNullability BinaryTag tag) {
            return tag instanceof StringBinaryTag stringBinaryTag ? stringBinaryTag.value() : "";
        }
    };

    BinaryTagSerializer<CompoundBinaryTag> COMPOUND = new BinaryTagSerializer<>() {
        @Override
        public @NotNull BinaryTag write(@UnknownNullability CompoundBinaryTag value) {
            return value;
        }

        @Override
        public @NotNull CompoundBinaryTag read(@UnknownNullability BinaryTag tag) {
            return tag instanceof CompoundBinaryTag compoundBinaryTag ? compoundBinaryTag : CompoundBinaryTag.empty();
        }
    };
    BinaryTagSerializer<CompoundBinaryTag> COMPOUND_COERCED = coerced(BinaryTagTypes.COMPOUND);

    BinaryTagSerializer<Component> JSON_COMPONENT = STRING.map(s -> GsonComponentSerializer.gson().deserialize(s), c -> GsonComponentSerializer.gson().serialize(c));

    @UnknownNullability
    BinaryTag write(@UnknownNullability T value);

    @UnknownNullability
    T read(@UnknownNullability BinaryTag tag);

    default BinaryTagSerializer<@Nullable T> optional() {
        return optional(null);
    }

    default BinaryTagSerializer<@UnknownNullability T> optional(@Nullable T defaultValue) {
        return new BinaryTagSerializer<>() {
            @Override
            public @UnknownNullability BinaryTag write(@UnknownNullability T value) {
                return value == null || value.equals(defaultValue) ? null : BinaryTagSerializer.this.write(value);
            }

            @Override
            public @UnknownNullability T read(@Nullable BinaryTag tag) {
                return tag == null ? defaultValue : BinaryTagSerializer.this.read(tag);
            }
        };

    }

    default <S> BinaryTagSerializer<S> map(@NotNull Function<T, S> to, @NotNull Function<S, T> from) {
        return new BinaryTagSerializer<>() {
            @Override
            public @NotNull BinaryTag write(@UnknownNullability S value) {
                return BinaryTagSerializer.this.write(from.apply(value));
            }

            @Override
            public @NotNull S read(@UnknownNullability BinaryTag tag) {
                return to.apply(BinaryTagSerializer.this.read(tag));
            }
        };
    }

    default BinaryTagSerializer<List<T>> list() {
        return new BinaryTagSerializer<>() {
            @Override
            public @NotNull BinaryTag write(@UnknownNullability List<T> value) {
                var builder = ListBinaryTag.builder();
                for (var t : value) {
                    var entry = BinaryTagSerializer.this.write(t);
                    builder.add(entry);
                }
                return builder.build();
            }

            @Override
            public @NotNull List<T> read(@UnknownNullability BinaryTag tag) {
                if (!(tag instanceof ListBinaryTag listBinaryTag)) return List.of();
                List<T> list = new ArrayList<>();
                for (var element : listBinaryTag)
                    list.add(BinaryTagSerializer.this.read(element));
                return List.copyOf(list);
            }
        };
    }
}
