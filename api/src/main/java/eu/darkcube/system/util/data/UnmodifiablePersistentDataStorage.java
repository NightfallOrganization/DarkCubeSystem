/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

public class UnmodifiablePersistentDataStorage implements PersistentDataStorage {
    private final PersistentDataStorage storage;

    public UnmodifiablePersistentDataStorage(PersistentDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public @UnmodifiableView @NotNull PersistentDataStorage unmodifiable() {
        return this;
    }

    @Override
    public @Unmodifiable @NotNull Collection<Key> keys() {
        return storage.keys();
    }

    @Override
    public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return storage.get(key, type);
    }

    @Override
    public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean has(@NotNull Key key) {
        return storage.has(key);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadFromJsonObject(@NotNull JsonObject object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull JsonObject storeToJsonObject() {
        return storage.storeToJsonObject();
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return storage.updateNotifiers();
    }

    @Override
    public void clearCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull <T> CompletableFuture<Void> setIfAbsentAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void setIfAbsent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull <T> CompletableFuture<Void> setAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(@NotNull Key key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull CompletableFuture<Void> loadFromJsonObjectAsync(@NotNull JsonObject object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull <T> CompletableFuture<@Nullable T> removeAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        storage.addUpdateNotifier(notifier);
    }

    @Override
    public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        storage.removeUpdateNotifier(notifier);
    }
}
