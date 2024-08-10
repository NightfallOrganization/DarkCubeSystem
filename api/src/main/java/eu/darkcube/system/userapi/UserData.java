/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import java.math.BigInteger;
import java.util.UUID;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.MetaDataStorage;
import eu.darkcube.system.util.data.PersistentDataStorage;

public interface UserData {
    /**
     * @return this user's minecraft {@link UUID}
     */
    @Api
    @NotNull
    UUID uniqueId();

    /**
     * @return the last known name for this user
     */
    @Api
    @NotNull
    String name();

    /**
     * @return the user's cubes
     */
    @Api
    @NotNull
    BigInteger cubes();

    /**
     * Sets the user's cubes
     *
     * @param cubes the cubes
     */
    @Api
    void cubes(@NotNull BigInteger cubes);

    /**
     * @return the user's language
     */
    @Api
    @NotNull
    Language language();

    /**
     * Sets the user's language
     *
     * @param language the language
     */
    @Api
    void language(@NotNull Language language);

    @Api
    @NotNull
    UserSettings settings();

    /**
     * @return the user's {@link MetaDataStorage}. This only stores data until the user is unloaded,
     *         then all data is forgotten. This will NOT synchronize over all servers
     */
    @Api
    @NotNull
    MetaDataStorage metadata();

    /**
     * @return the user's {@link PersistentDataStorage}. This will also synchronize over all servers
     */
    @Api
    @NotNull
    PersistentDataStorage persistentData();

    /**
     * @return true
     * @deprecated As long as this instance exists, the User is loaded. There may not be multiple instances of a User, so calling this always returns true
     */
    @Contract("->true")
    @Deprecated(forRemoval = true)
    default boolean isLoaded() {
        return true;
    }

    /**
     * @deprecated {@link #persistentData()}
     */
    @Deprecated(forRemoval = true)
    default PersistentDataStorage getPersistentDataStorage() {
        return persistentData();
    }

    /**
     * @deprecated {@link #metadata()}
     */
    @Deprecated(forRemoval = true)
    default MetaDataStorage getMetaDataStorage() {
        return metadata();
    }

    /**
     * @deprecated {@link #uniqueId()}
     */
    @Deprecated(forRemoval = true)
    default UUID getUniqueId() {
        return uniqueId();
    }

    /**
     * @deprecated {@link #cubes()}
     */
    @Deprecated(forRemoval = true)
    default BigInteger getCubes() {
        return cubes();
    }

    /**
     * @deprecated {@link #cubes(BigInteger)}
     */
    @Deprecated(forRemoval = true)
    default void setCubes(BigInteger cubes) {
        cubes(cubes);
    }

    /**
     * @deprecated {@link #name()}
     */
    @Deprecated(forRemoval = true)
    default String getName() {
        return name();
    }

    interface Forwarding extends UserData {
        UserData userData();

        @Override
        default @NotNull UserSettings settings() {
            return userData().settings();
        }

        @Override
        default @NotNull UUID uniqueId() {
            return userData().uniqueId();
        }

        @Override
        default @NotNull String name() {
            return userData().name();
        }

        @Override
        default @NotNull BigInteger cubes() {
            return userData().cubes();
        }

        @Override
        default @NotNull Language language() {
            return userData().language();
        }

        @Override
        default void language(@NotNull Language language) {
            userData().language(language);
        }

        @Override
        default void cubes(@NotNull BigInteger cubes) {
            userData().cubes(cubes);
        }

        @Override
        default @NotNull MetaDataStorage metadata() {
            return userData().metadata();
        }

        @Override
        default @NotNull PersistentDataStorage persistentData() {
            return userData().persistentData();
        }
    }
}
