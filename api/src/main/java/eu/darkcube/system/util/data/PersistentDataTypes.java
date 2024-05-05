/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import eu.cloudnetservice.driver.document.Document;

public class PersistentDataTypes {
    public static final PersistentDataType<BigInteger> BIGINTEGER = new PersistentDataType<>() {
        @Override
        public BigInteger deserialize(Document doc, String key) {
            return doc.readObject(key, BigInteger.class);
        }

        @Override
        public void serialize(Document.Mutable doc, String key, BigInteger data) {
            doc.append(key, data);
        }

        @Override
        public BigInteger clone(BigInteger object) {
            return object;
        }
    };
    public static final PersistentDataType<UUID> UUID = new PersistentDataType<>() {
        @Override
        public UUID deserialize(Document doc, String key) {
            var a = doc.readObject(key, long[].class);
            return new UUID(a[0], a[1]);
        }

        @Override
        public void serialize(Document.Mutable doc, String key, UUID data) {
            doc.append(key, new long[]{data.getMostSignificantBits(), data.getLeastSignificantBits()});
        }

        @Override
        public UUID clone(UUID object) {
            return object;
        }
    };
    public static final PersistentDataType<Document> DOCUMENT = new PersistentDataType<>() {
        @Override
        public Document deserialize(Document doc, String key) {
            return doc.readDocument(key);
        }

        @Override
        public void serialize(Document.Mutable doc, String key, Document data) {
            doc.append(key, data);
        }

        @Override
        public Document clone(Document object) {
            return object.immutableCopy();
        }
    };
    public static final PersistentDataType<String> STRING = new PersistentDataType<>() {
        @Override
        public String deserialize(Document doc, String key) {
            return doc.getString(key);
        }

        @Override
        public void serialize(Document.Mutable doc, String key, String data) {
            doc.append(key, data);
        }

        @Override
        public String clone(String object) {
            return object;
        }
    };
    public static final PersistentDataType<byte[]> BYTE_ARRAY = new PersistentDataType<>() {
        @Override
        public byte[] deserialize(Document doc, String key) {
            try {
                return doc.readObject(key, byte[].class);
            } catch (Throwable throwable) {
                try {
                    return Base64.getDecoder().decode(doc.getString(key));
                } catch (Throwable ignored) {
                    throw throwable;
                }
            }
        }

        @Override
        public void serialize(Document.Mutable doc, String key, byte[] data) {
            doc.append(key, data);
        }

        @Override
        public byte[] clone(byte[] object) {
            return object.clone();
        }
    };
    public static final PersistentDataType<Boolean> BOOLEAN = new PersistentDataType<>() {
        @Override
        public Boolean deserialize(Document doc, String key) {
            return doc.getBoolean(key);
        }

        @Override
        public void serialize(Document.Mutable doc, String key, Boolean data) {
            doc.append(key, data);
        }

        @Override
        public Boolean clone(Boolean object) {
            return object;
        }
    };
    public static final PersistentDataType<Integer> INTEGER = new PersistentDataType<>() {

        @Override
        public Integer deserialize(Document doc, String key) {
            return doc.getInt(key);
        }

        @Override
        public void serialize(Document.Mutable doc, String key, Integer data) {
            doc.append(key, data);
        }

        @Override
        public Integer clone(Integer object) {
            return object;
        }
    };
    public static final PersistentDataType<Long> LONG = new PersistentDataType<>() {
        @Override
        public Long deserialize(Document doc, String key) {
            return doc.getLong(key);
        }

        @Override
        public void serialize(Document.Mutable doc, String key, Long data) {
            doc.append(key, data);
        }

        @Override
        public Long clone(Long object) {
            return object;
        }
    };
    public static final PersistentDataType<Double> DOUBLE = new PersistentDataType<>() {
        @Override
        public Double deserialize(Document doc, String key) {
            return doc.getDouble(key);
        }

        @Override
        public void serialize(Document.Mutable doc, String key, Double data) {
            doc.append(key, data);
        }

        @Override
        public Double clone(Double object) {
            return object;
        }
    };
    public static final PersistentDataType<int[]> INT_ARRAY = new PersistentDataType<int[]>() {
        @Override
        public int[] deserialize(Document doc, String key) {
            try {
                return doc.readObject(key, int[].class);
            } catch (Throwable throwable) {
                try {
                    var bytes = BYTE_ARRAY.deserialize(doc, key);
                    var buf = ByteBuffer.wrap(bytes).asIntBuffer();
                    var len = buf.get();
                    var ar = new int[len];
                    for (var i = 0; buf.remaining() > 0; i++) {
                        ar[i] = buf.get();
                    }
                    return ar;
                } catch (Throwable ignored) {
                    throw throwable;
                }
            }
        }

        @Override
        public void serialize(Document.Mutable doc, String key, int[] data) {
            doc.append(key, data);
        }

        @Override
        public int[] clone(int[] object) {
            return object.clone();
        }
    };

    public static <T> PersistentDataType<Set<T>> set(PersistentDataType<T> dataType) {
        var l = list(dataType);
        return new PersistentDataType<Set<T>>() {
            @Override
            public Set<T> deserialize(Document doc, String key) {
                return new HashSet<>(l.deserialize(doc, key));
            }

            @Override
            public void serialize(Document.Mutable doc, String key, Set<T> data) {
                l.serialize(doc, key, new ArrayList<>(data));
            }

            @Override
            public Set<T> clone(Set<T> object) {
                Set<T> set = new HashSet<>();
                for (var value : object) {
                    set.add(dataType.clone(value));
                }
                return set;
            }
        };
    }

    public static <T> PersistentDataType<List<T>> list(PersistentDataType<T> dataType) {
        return new PersistentDataType<>() {
            @Override
            public List<T> deserialize(Document doc, String key) {
                List<T> l = new ArrayList<>();
                var i = 0;
                var d = doc.readDocument(key);
                while (d.contains(Integer.toString(i))) {
                    l.add(dataType.deserialize(d, Integer.toString(i++)));
                }
                return l;
            }

            @Override
            public void serialize(Document.Mutable doc, String key, List<T> data) {
                var d = Document.newJsonDocument();
                var i = 0;
                for (var t : data) {
                    dataType.serialize(d, Integer.toString(i++), t);
                }
                doc.append(key, d);
            }

            @Override
            public List<T> clone(List<T> object) {
                List<T> list = new ArrayList<>();
                for (var value : object) {
                    list.add(dataType.clone(value));
                }
                return list;
            }
        };
    }

    @SuppressWarnings("Convert2MethodRef")
    public static <T> PersistentDataType<T[]> array(PersistentDataType<T> dataType, Class<T> type) {
        return map(list(dataType), Arrays::asList, l -> l.toArray((T[]) Array.newInstance(type, 0)), a -> a.clone());
    }

    public static <T extends Enum<T>> PersistentDataType<T> enumType(Class<T> cls) {
        return new PersistentDataType<T>() {
            @Override
            public T deserialize(Document doc, String key) {
                return doc.readObject(key, cls);
            }

            @Override
            public void serialize(Document.Mutable doc, String key, T data) {
                doc.append(key, data);
            }

            @Override
            public T clone(T object) {
                return object;
            }
        };
    }

    public static <T, V> PersistentDataType<T> map(PersistentDataType<V> type, Function<T, V> serialize, Function<V, T> deserialize, Function<T, T> clone) {
        return new PersistentDataType<T>() {
            @Override
            public T deserialize(Document doc, String key) {
                return deserialize.apply(type.deserialize(doc, key));
            }

            @Override
            public void serialize(Document.Mutable doc, String key, T data) {
                type.serialize(doc, key, serialize.apply(data));
            }

            @Override
            public T clone(T object) {
                return clone.apply(object);
            }
        };
    }

}
