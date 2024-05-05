/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import java.util.Objects;

import eu.darkcube.system.libs.net.kyori.adventure.key.Namespaced;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public final class Key implements eu.darkcube.system.libs.net.kyori.adventure.key.Key {

    private final @NotNull String namespace;
    private final @NotNull String value;

    public Key(@NotNull Namespaced namespaced, @NotNull String value) {
        this(namespaced.namespace(), value);
    }

    public Key(@NotNull Named named, @NotNull String value) {
        this(named.getName(), value);
    }

    public Key(@NotNull String namespace, @NotNull String value) {
        this.namespace = namespace;
        this.value = value;
    }

    public static Key fromString(@NotNull String string) {
        var a = string.split(":", 2);
        return new Key(a[0], a[1]);
    }

    @Override
    public @NotNull String namespace() {
        return namespace;
    }

    @Override
    public @NotNull String value() {
        return value;
    }

    @Override
    public @NotNull String asString() {
        return toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, namespace);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        var other = (Key) obj;
        return Objects.equals(this.value, other.value) && Objects.equals(this.namespace, other.namespace);
    }

    @Override
    public String toString() {
        return namespace + ":" + value;
    }

    public interface Named extends Namespaced {
        @NotNull
        String getName();

        @Override
        default @NotNull String namespace() {
            return getName();
        }
    }
}
