/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

public class WrapperPersistentDataStorage implements PersistentDataStorage {
    private final PersistentDataStorage handle;

    public WrapperPersistentDataStorage(PersistentDataStorage handle) {
        this.handle = handle;
    }

    @Override
    @Api
    @UnmodifiableView
    @NotNull
    public PersistentDataStorage unmodifiable() {
        return handle.unmodifiable();
    }

    @Override
    @Api
    @Unmodifiable
    @NotNull
    public Collection<@NotNull Key> keys() {
        return handle.keys();
    }

    @Override
    @Api
    public @NotNull CompletableFuture<@Unmodifiable @NotNull Collection<@NotNull Key>> keysAsync() {
        return handle.keysAsync();
    }

    @Override
    @Api
    public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        handle.set(key, type, data);
    }

    @Override
    public @NotNull <T> CompletableFuture<Void> setAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        return handle.setAsync(key, type, data);
    }

    @Override
    public void remove(@NotNull Key key) {
        handle.remove(key);
    }

    @Override
    @Api
    public <T> @Nullable T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return handle.remove(key, type);
    }

    @Override
    @Api
    public @NotNull <T> CompletableFuture<@Nullable T> removeAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return handle.removeAsync(key, type);
    }

    @Override
    @Api
    public <T> @Nullable T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return handle.get(key, type);
    }

    @Override
    @Api
    public @NotNull <T> CompletableFuture<@Nullable T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return handle.getAsync(key, type);
    }

    @Override
    @Api
    public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        return handle.get(key, type, defaultValue);
    }

    @Override
    @Api
    public @NotNull <T> CompletableFuture<@NotNull T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        return handle.getAsync(key, type, defaultValue);
    }

    @Override
    @Api
    public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        handle.setIfNotPresent(key, type, data);
    }

    @Override
    @Api
    public @NotNull <T> CompletableFuture<Void> setIfNotPresentAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        return handle.setIfNotPresentAsync(key, type, data);
    }

    @Override
    @Api
    public boolean has(@NotNull Key key) {
        return handle.has(key);
    }

    @Override
    @Api
    public @NotNull CompletableFuture<@NotNull Boolean> hasAsync(@NotNull Key key) {
        return handle.hasAsync(key);
    }

    @Override
    @Api
    public void clear() {
        handle.clear();
    }

    @Override
    @Api
    public @NotNull CompletableFuture<Void> clearAsync() {
        return handle.clearAsync();
    }

    @Override
    @Api
    public void loadFromJsonObject(@NotNull JsonObject object) {
        handle.loadFromJsonObject(object);
    }

    @Override
    @Api
    public @NotNull CompletableFuture<Void> loadFromJsonObjectAsync(@NotNull JsonObject object) {
        return handle.loadFromJsonObjectAsync(object);
    }

    @Override
    @Api
    @NotNull
    public JsonObject storeToJsonObject() {
        return handle.storeToJsonObject();
    }

    @Override
    @Api
    public @NotNull CompletableFuture<@NotNull JsonObject> storeToJsonObjectAsync() {
        return handle.storeToJsonObjectAsync();
    }

    @Override
    @Api
    @UnmodifiableView
    @NotNull
    public Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return handle.updateNotifiers();
    }

    @Override
    @ApiStatus.Experimental
    public void clearCache() {
        handle.clearCache();
    }

    @Override
    @ApiStatus.Experimental
    public @NotNull CompletableFuture<Void> clearCacheAsync() {
        return handle.clearCacheAsync();
    }

    @Override
    @Api
    public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        handle.addUpdateNotifier(notifier);
    }

    @Override
    @Api
    public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        handle.removeUpdateNotifier(notifier);
    }
}
