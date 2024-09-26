/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import static eu.darkcube.system.util.AsyncExecutor.virtualService;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

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

/**
 * @author DasBabyPixel
 */
@Api
public interface PersistentDataStorage {

    /**
     * @return an unmodifiable view of this storage
     */
    @Api
    @NotNull
    @UnmodifiableView
    default PersistentDataStorage unmodifiable() {
        return new UnmodifiablePersistentDataStorage(this);
    }

    @Api
    @NotNull
    @Unmodifiable
    Collection<@NotNull Key> keys();

    @Api
    @NotNull
    default CompletableFuture<@Unmodifiable @NotNull Collection<@NotNull Key>> keysAsync() {
        return supplyAsync(this::keys, virtualService());
    }

    /**
     * Saves some data
     *
     * @param key  the key
     * @param type the type
     * @param data the data
     * @param <T>  the data type
     */
    @Api
    <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data);

    @Api
    default <T> @NotNull CompletableFuture<Void> setAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        return runAsync(() -> set(key, type, data), virtualService());
    }

    @Api
    default <T> void set(@NotNull DataKey<T> key, @NotNull T data) {
        set(key.key(), key.dataType(), data);
    }

    @Api
    default <T> @NotNull CompletableFuture<Void> setAsync(@NotNull DataKey<T> key, @NotNull T data) {
        return setAsync(key.key(), key.dataType(), data);
    }

    void remove(@NotNull Key key);

    /**
     * Removes some data
     *
     * @param key  the key
     * @param type the type
     * @param <T>  the data type
     * @return the removed data
     */
    @Api
    @Nullable
    <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type);

    @Api
    @NotNull
    default <T> CompletableFuture<@Nullable T> removeAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return supplyAsync(() -> remove(key, type), virtualService());
    }

    @Api
    @Nullable
    default <T> T remove(@NotNull DataKey<T> key) {
        return remove(key.key(), key.dataType());
    }

    @Api
    @NotNull
    default <T> CompletableFuture<@Nullable T> removeAsync(@NotNull DataKey<T> key) {
        return removeAsync(key.key(), key.dataType());
    }

    /**
     * @param key  the key
     * @param type the type
     * @param <T>  the data type
     * @return saved data, null if not present
     */
    @Api
    @Nullable
    <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type);

    @Api
    @NotNull
    default <T> CompletableFuture<@Nullable T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return supplyAsync(() -> get(key, type), virtualService());
    }

    @Api
    @Nullable
    default <T> T get(@NotNull DataKey<T> key) {
        return get(key.key(), key.dataType());
    }

    @Api
    @NotNull
    default <T> CompletableFuture<@Nullable T> getAsync(@NotNull DataKey<T> key) {
        return getAsync(key.key(), key.dataType());
    }

    /**
     * Gets the data at the specified {@code key}, setting it to the return value of {@code defaultValue} if not present and returning that value
     *
     * @param key          the key
     * @param type         the type
     * @param defaultValue the default value
     * @param <T>          the data type
     * @return the saved data, defaultValue if not present
     */
    @Api
    @NotNull
    <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue);

    @Api
    @NotNull
    default <T> CompletableFuture<@NotNull T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        return supplyAsync(() -> get(key, type, defaultValue), virtualService());
    }

    @Api
    @NotNull
    default <T> T get(@NotNull DataKey<T> key, @NotNull Supplier<@NotNull T> defaultValue) {
        return get(key.key(), key.dataType(), defaultValue);
    }

    @Api
    @NotNull
    default <T> CompletableFuture<@NotNull T> getAsync(@NotNull DataKey<T> key, @NotNull Supplier<@NotNull T> defaultValue) {
        return getAsync(key.key(), key.dataType(), defaultValue);
    }

    /**
     * @param key  the key
     * @param type the type
     * @param data the data
     * @param <T>  the data type
     * @deprecated use
     */
    @Api
    @Deprecated(forRemoval = true)
    default <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        setIfAbsent(key, type, data);
    }

    @Api
    @NotNull
    @Deprecated(forRemoval = true)
    default <T> CompletableFuture<Void> setIfNotPresentAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        return setIfAbsentAsync(key, type, data);
    }

    <T> void setIfAbsent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data);

    @Api
    @NotNull
    default <T> CompletableFuture<Void> setIfAbsentAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        return runAsync(() -> setIfAbsent(key, type, data), virtualService());
    }

    @Api
    default <T> void setIfAbsent(@NotNull DataKey<T> key, @NotNull T data) {
        setIfAbsent(key.key(), key.dataType(), data);
    }

    @Api
    @NotNull
    default <T> CompletableFuture<Void> setIfAbsentAsync(@NotNull DataKey<T> key, @NotNull T data) {
        return setIfAbsentAsync(key.key(), key.dataType(), data);
    }

    @Api
    @Deprecated(forRemoval = true)
    default <T> void setIfNotPresent(@NotNull DataKey<T> key, @NotNull T data) {
        setIfNotPresent(key.key(), key.dataType(), data);
    }

    @Api
    @NotNull
    @Deprecated(forRemoval = true)
    default <T> CompletableFuture<Void> setIfNotPresentAsync(@NotNull DataKey<T> key, @NotNull T data) {
        return setIfNotPresentAsync(key.key(), key.dataType(), data);
    }

    /**
     * @param key the key
     * @return whether data is present for the given key
     */
    @Api
    boolean has(@NotNull Key key);

    @Api
    @NotNull
    default CompletableFuture<@NotNull Boolean> hasAsync(@NotNull Key key) {
        return supplyAsync(() -> has(key), virtualService());
    }

    @Api
    default <T> boolean has(@NotNull DataKey<T> key) {
        return has(key.key());
    }

    @Api
    @NotNull
    default <T> CompletableFuture<@NotNull Boolean> hasAsync(@NotNull DataKey<T> key) {
        return hasAsync(key.key());
    }

    /**
     * Clears this storage
     */
    @Api
    void clear();

    @NotNull
    @Api
    default CompletableFuture<Void> clearAsync() {
        return runAsync(this::clear, virtualService());
    }

    /**
     * Loads all the data from a {@link JsonObject}<br>
     * <b>This WILL be cleared, previous data will be REMOVED</b>
     *
     * @param object the object to load the data from
     */
    @Api
    void loadFromJsonObject(@NotNull JsonObject object);

    @NotNull
    @Api
    default CompletableFuture<Void> loadFromJsonObjectAsync(@NotNull JsonObject object) {
        return runAsync(() -> loadFromJsonObject(object), virtualService());
    }

    /**
     * @return a jsonObject with all the data
     */
    @Api
    @NotNull
    JsonObject storeToJsonObject();

    @Api
    @NotNull
    default CompletableFuture<@NotNull JsonObject> storeToJsonObjectAsync() {
        return supplyAsync(this::storeToJsonObject, virtualService());
    }

    /**
     * @return an unmodifiable view of all {@link UpdateNotifier}s
     */
    @UnmodifiableView
    @NotNull
    @Api
    Collection<@NotNull UpdateNotifier> updateNotifiers();

    @ApiStatus.Experimental
    void clearCache();

    @ApiStatus.Experimental
    @NotNull
    default CompletableFuture<Void> clearCacheAsync() {
        return runAsync(this::clearCache, virtualService());
    }

    /**
     * Adds an {@link UpdateNotifier} to this storage
     *
     * @param notifier the notifier
     */
    @Api
    void addUpdateNotifier(@NotNull UpdateNotifier notifier);

    /**
     * Removes an {@link UpdateNotifier} from this storage
     *
     * @param notifier the notifier
     */
    @Api
    void removeUpdateNotifier(@NotNull UpdateNotifier notifier);

    /**
     * This will be notified whenever the data of a {@link PersistentDataStorage} updates
     */
    @Api
    interface UpdateNotifier {
        @Api
        void notify(@NotNull PersistentDataStorage storage);
    }
}
