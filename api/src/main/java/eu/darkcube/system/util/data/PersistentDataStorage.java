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
    @UnmodifiableView
    @NotNull
    @Api
    PersistentDataStorage unmodifiable();

    @Unmodifiable
    @NotNull
    @Api
    Collection<@NotNull Key> keys();

    @NotNull
    @Api
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

    default <T> @NotNull CompletableFuture<Void> setAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        return runAsync(() -> set(key, type, data), virtualService());
    }

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

    @NotNull
    @Api
    default <T> CompletableFuture<@Nullable T> removeAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return supplyAsync(() -> remove(key, type), virtualService());
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

    @NotNull
    @Api
    default <T> CompletableFuture<@Nullable T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return supplyAsync(() -> get(key, type), virtualService());
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
    @NotNull
    @Api
    <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue);

    @NotNull
    @Api
    default <T> CompletableFuture<@NotNull T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        return supplyAsync(() -> get(key, type, defaultValue), virtualService());
    }

    /**
     * @param key  the key
     * @param type the type
     * @param data the data
     * @param <T>  the data type
     */
    @Api
    <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data);

    @NotNull
    @Api
    default <T> CompletableFuture<Void> setIfNotPresentAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        return runAsync(() -> setIfNotPresent(key, type, data), virtualService());
    }

    /**
     * @param key the key
     * @return whether data is present for the given key
     */
    @Api
    boolean has(@NotNull Key key);

    @NotNull
    @Api
    default CompletableFuture<@NotNull Boolean> hasAsync(@NotNull Key key) {
        return supplyAsync(() -> has(key), virtualService());
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
    @NotNull
    @Api
    JsonObject storeToJsonObject();

    @NotNull
    @Api
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
