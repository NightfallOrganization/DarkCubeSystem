/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import java.util.Collection;
import java.util.function.Supplier;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

/**
 * @author DasBabyPixel
 */
public interface PersistentDataStorage {

    /**
     * @return an unmodifiable view of this storage
     */
    @NotNull
    @UnmodifiableView
    PersistentDataStorage unmodifiable();

    @NotNull
    @Unmodifiable
    Collection<Key> keys();

    /**
     * Saves some data
     *
     * @param key  the key
     * @param type the type
     * @param data the data
     * @param <T>  the data type
     */
    <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data);

    /**
     * Removes some data
     *
     * @param key  the key
     * @param type the type
     * @param <T>  the data type
     * @return the removed data
     */
    @Nullable
    <T> T remove(@NotNull Key key, @SuppressWarnings("NullableProblems") @NotNull PersistentDataType<T> type);

    /**
     * @param key  the key
     * @param type the type
     * @param <T>  the data type
     * @return saved data, null if not present
     */
    @Nullable
    <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type);

    /**
     * @param key          the key
     * @param type         the type
     * @param defaultValue the default value
     * @param <T>          the data type
     * @return the saved data, defaultValue if not present
     */
    @NotNull
    <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue);

    /**
     * @param key  the key
     * @param type the type
     * @param data the data
     * @param <T>  the data type
     */
    <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data);

    /**
     * @param key the key
     * @return whether data is present for the given key
     */
    boolean has(@NotNull Key key);

    /**
     * Clears this storage
     */
    void clear();

    /**
     * Loads all the data from a {@link Document}<br>
     * <b>This WILL be cleared, previous data will be REMOVED</b>
     *
     * @param document the document to load the data from
     */
    void loadFromJsonDocument(Document document);

    /**
     * @return a jsonDocument with all the data
     */
    Document storeToJsonDocument();

    /**
     * @return an unmodifiable view of all {@link UpdateNotifier}s
     */
    @UnmodifiableView
    @NotNull
    Collection<@NotNull UpdateNotifier> updateNotifiers();

    void clearCache();

    /**
     * Adds an {@link UpdateNotifier} to this storage
     *
     * @param notifier the notifier
     */
    void addUpdateNotifier(@NotNull UpdateNotifier notifier);

    /**
     * Removes an {@link UpdateNotifier} from this storage
     *
     * @param notifier the notifier
     */
    void removeUpdateNotifier(@NotNull UpdateNotifier notifier);

    /**
     * This will be notified whenever the data of a {@link PersistentDataStorage} updates
     */
    interface UpdateNotifier {
        void notify(PersistentDataStorage storage);
    }
}
