/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonArray;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class PersistentDataTypes {
    @Api
    public static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Key.class, new TypeAdapter<Key>() {
        @Override
        public void write(JsonWriter out, Key value) throws IOException {
            out.value(value.toString());
        }

        @Override
        public Key read(JsonReader in) throws IOException {
            return Key.key(in.nextString());
        }
    }).create();
    @Api
    public static final PersistentDataType<BigInteger> BIGINTEGER = simpleImmutable(BigInteger.class);
    @Api
    public static final PersistentDataType<UUID> UUID = create(json -> {
        var array = json.getAsJsonArray();
        return new UUID(array.get(0).getAsLong(), array.get(1).getAsLong());
    }, uuid -> {
        var array = new JsonArray(2);
        array.add(uuid.getMostSignificantBits());
        array.add(uuid.getLeastSignificantBits());
        return array;
    }, uuid -> uuid);
    @Api
    public static final PersistentDataType<JsonElement> JSON_ELEMENT = simple(JsonElement.class, JsonElement::deepCopy);
    @Api
    public static final PersistentDataType<JsonObject> JSON_OBJECT = simple(JsonObject.class, JsonObject::deepCopy);
    @Api
    public static final PersistentDataType<String> STRING = simpleImmutable(String.class);
    @Api
    public static final PersistentDataType<byte[]> BYTE_ARRAY = new Builder<byte[]>().addSimpleDeserializer(byte[].class).addDeserializer(json -> Base64.getDecoder().decode(json.getAsString())).addSimpleSerializer(byte[].class).addClone(byte[]::clone).build();
    @Api
    public static final PersistentDataType<Boolean> BOOLEAN = simpleImmutable(Boolean.class);
    @Api
    public static final PersistentDataType<Integer> INTEGER = simpleImmutable(Integer.class);
    @Api
    public static final PersistentDataType<Long> LONG = simpleImmutable(Long.class);
    @Api
    public static final PersistentDataType<Double> DOUBLE = simpleImmutable(Double.class);
    @Api
    public static final PersistentDataType<int[]> INT_ARRAY = new Builder<int[]>().addSimpleDeserializer(int[].class).addDeserializer(json -> {
        var bytes = BYTE_ARRAY.deserialize(json);
        var buf = ByteBuffer.wrap(bytes).asIntBuffer();
        var len = buf.get();
        var ar = new int[len];
        for (var i = 0; buf.remaining() > 0; i++) {
            ar[i] = buf.get();
        }
        return ar;
    }).addSimpleSerializer(int[].class).addClone(int[]::clone).build();

    @Api
    public static <T> PersistentDataType<Set<T>> set(@NotNull PersistentDataType<T> dataType) {
        var l = list(dataType);
        return map(l, List::copyOf, Set::copyOf, set -> {
            var s = new ArrayList<T>();
            for (var t : set) {
                s.add(dataType.clone(t));
            }
            return Set.copyOf(s);
        });
    }

    @Api
    public static <T> PersistentDataType<List<T>> list(@NotNull PersistentDataType<T> dataType) {
        return new Builder<List<T>>().addDeserializer(json -> {
            var obj = json.getAsJsonObject();
            var list = new ArrayList<T>();
            var i = 0;
            while (obj.has(Integer.toString(i))) {
                list.add(dataType.deserialize(obj.get(Integer.toString(i++))));
            }
            return List.copyOf(list);
        }).addDeserializer(json -> {
            var array = json.getAsJsonArray();
            var list = new ArrayList<T>();
            for (var element : array) {
                list.add(dataType.deserialize(element));
            }
            return List.copyOf(list);
        }).addSerializer(list -> {
            var array = new JsonArray();
            for (var t : list) {
                array.add(dataType.serialize(t));
            }
            return array;
        }).addClone(list -> {
            var l = new ArrayList<T>();
            for (var t : list) {
                l.add(dataType.clone(t));
            }
            return List.copyOf(l);
        }).build();
    }

    @Api
    @SuppressWarnings("Convert2MethodRef")
    public static <T> @NotNull PersistentDataType<T[]> array(@NotNull PersistentDataType<T> dataType, @NotNull Class<T> type) {
        return map(list(dataType), Arrays::asList, l -> l.toArray((T[]) Array.newInstance(type, 0)), a -> a.clone());
    }

    @Api
    public static <T extends Enum<T>> @NotNull PersistentDataType<T> enumType(@NotNull Class<T> cls) {
        return simpleImmutable(cls);
    }

    @Api
    public static <T> @NotNull PersistentDataType<T> simpleImmutable(@NotNull Type type) {
        return simple(type, t -> t);
    }

    @Api
    public static <T> Function<@NotNull JsonElement, @NotNull T> simpleDeserializer(@NotNull Type type) {
        return json -> GSON.fromJson(json, type);
    }

    @Api
    public static <T> Function<@NotNull T, @NotNull JsonElement> simpleSerialize(@NotNull Type type) {
        return data -> GSON.toJsonTree(data, type);
    }

    @Api
    public static <T> PersistentDataType<T> simple(@NotNull Type type, @NotNull Function<@NotNull T, @NotNull T> clone) {
        return create(simpleDeserializer(type), simpleSerialize(type), clone);
    }

    @Api
    public static <T> PersistentDataType<T> create(@NotNull Function<@NotNull JsonElement, @NotNull T> deserialize, @NotNull Function<@NotNull T, @NotNull JsonElement> serialize, @NotNull Function<@NotNull T, @NotNull T> clone) {
        return new Builder<T>().addDeserializer(deserialize).addSerializer(serialize).addClone(clone).build();
    }

    @Api
    public static <T, V> PersistentDataType<T> map(@NotNull PersistentDataType<V> type, @NotNull Function<@NotNull T, @NotNull V> serialize, @NotNull Function<@NotNull V, @NotNull T> deserialize, Function<@NotNull T, @NotNull T> clone) {
        return create(json -> deserialize.apply(type.deserialize(json)), data -> type.serialize(serialize.apply(data)), clone);
    }

    @Api
    public static class Builder<T> {
        private final List<Function<T, JsonElement>> serializers = new ArrayList<>();
        private final List<Function<JsonElement, T>> deserializers = new ArrayList<>();
        private final List<Function<T, T>> clones = new ArrayList<>();

        @Api
        public Builder<T> addSerializer(@NotNull Function<@NotNull T, @NotNull JsonElement> serializer) {
            serializers.add(serializer);
            return this;
        }

        @Api
        public Builder<T> addSimpleSerializer(@NotNull Type type) {
            return addSerializer(simpleSerialize(type));
        }

        @Api
        public Builder<T> addDeserializer(@NotNull Function<@NotNull JsonElement, @NotNull T> deserializer) {
            deserializers.add(deserializer);
            return this;
        }

        @Api
        public Builder<T> addSimpleDeserializer(@NotNull Type type) {
            return addDeserializer(simpleDeserializer(type));
        }

        @Api
        public Builder<T> addClone(Function<T, T> clone) {
            clones.add(clone);
            return this;
        }

        @Api
        public PersistentDataType<T> build() {
            var s = List.copyOf(serializers);
            var d = List.copyOf(deserializers);
            if (s.isEmpty()) throw new IllegalStateException("Serializers may not be empty");
            if (d.isEmpty()) throw new IllegalStateException("Deserializers may not be empty");
            var des = combine(d);
            var ser = combine(s);
            var c = clones.isEmpty() ? List.of((Function<T, T>) t -> des.apply(ser.apply(t))) : List.copyOf(clones);
            var clo = combine(c);
            return new PersistentDataType<>() {
                @Override
                public @NotNull T deserialize(@NotNull JsonElement json) {
                    return des.apply(json);
                }

                @Override
                public @NotNull JsonElement serialize(@NotNull T data) {
                    return ser.apply(data);
                }

                @Override
                public @NotNull T clone(@NotNull T object) {
                    return clo.apply(object);
                }
            };
        }

        private static <T, V> @NotNull Function<@NotNull T, @NotNull V> combine(@NotNull List<@NotNull Function<@NotNull T, @NotNull V>> functions) {
            if (functions.size() == 1) return functions.getFirst();
            return t -> {
                Throwable exception = null;
                for (var i = 0; i < functions.size(); i++) {
                    var function = functions.get(i);
                    try {
                        return function.apply(t);
                    } catch (Throwable throwable) {
                        if (exception == null) {
                            exception = throwable;
                        } else {
                            exception.addSuppressed(throwable);
                        }
                    }
                }
                switch (exception) {
                    case null -> throw new NoSuchMethodError("No deserializer registered for " + t);
                    case RuntimeException runtime -> throw runtime;
                    case Error error -> throw error;
                    default -> throw new Error(exception);
                }
            };
        }
    }
}
