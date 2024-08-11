/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.common.userapi;

import java.math.BigInteger;
import java.util.UUID;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.UserData;
import eu.darkcube.system.userapi.UserSettings;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import eu.darkcube.system.util.data.MetaDataStorage;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class CommonUserData implements UserData {

    private static final Key CUBES_KEY = CommonUserAPI.key("cubes");
    private static final PersistentDataType<BigInteger> CUBES_TYPE = PersistentDataTypes.BIGINTEGER;
    private final UUID uniqueId;
    private volatile String name;
    private final UserSettings settings;
    private final MetaDataStorage metadata;
    private final PersistentDataStorage persistentData;

    public CommonUserData(UUID uniqueId, String name, PersistentDataStorage persistentData) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.persistentData = persistentData;
        this.metadata = new BasicMetaDataStorage();
        this.settings = new CommonUserSettings(persistentData);
    }

    @Override
    public @NotNull UUID uniqueId() {
        return uniqueId;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull BigInteger cubes() {
        return persistentData().get(CUBES_KEY, CUBES_TYPE, () -> BigInteger.valueOf(1000));
    }

    @Override
    public void cubes(@NotNull BigInteger cubes) {
        persistentData().set(CUBES_KEY, CUBES_TYPE, cubes);
    }

    @Override
    public @NotNull Language language() {
        return settings().language();
    }

    @Override
    public void language(@NotNull Language language) {
        settings().language(language);
    }

    @Override
    public @NotNull UserSettings settings() {
        return settings;
    }

    @Override
    public @NotNull MetaDataStorage metadata() {
        return metadata;
    }

    @Override
    public @NotNull PersistentDataStorage persistentData() {
        return persistentData;
    }

    public void name(String name) {
        this.name = name;
    }
}
