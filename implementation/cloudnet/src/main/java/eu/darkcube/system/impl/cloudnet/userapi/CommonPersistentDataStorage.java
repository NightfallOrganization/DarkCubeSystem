/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.userapi;

import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.PersistentDataStorage;

@ApiStatus.Internal
public interface CommonPersistentDataStorage extends PersistentDataStorage {
    /**
     * Called when the node sends a remove update
     */
    void remove(@NotNull Key key);

    JsonElement getOrDefault(@NotNull Key key, @NotNull JsonElement json);

    void set(@NotNull Key key, @NotNull JsonElement json);

    void merge(@NotNull JsonObject json);

    void update(@NotNull JsonObject json);
}
