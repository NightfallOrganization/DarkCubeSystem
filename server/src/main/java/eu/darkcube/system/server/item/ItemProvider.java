/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.material.Material;

@ApiStatus.Experimental
@Api
public interface ItemProvider {
    static ItemProvider itemProvider() {
        return ItemProviderImpl.itemProvider();
    }

    /**
     * Creates an ItemBuilder for a Material. Null as input will use AIR as Material.
     *
     * @param material the Material to use
     * @return an ItemBuilder for the Material
     */
    @Api
    @NotNull
    ItemBuilder item(@Nullable Material material);

    /**
     * Deserializes a JsonElement to an ItemBuilder.
     *
     * @param json the Json to deserialize
     * @return an ItemBuilder created by the json
     */
    @Api
    @NotNull
    ItemBuilder item(@NotNull JsonElement json);

    /**
     * Tries to construct an ItemBuilder for the given object.
     * The object may be:
     * <ul>
     *     <li>a platform specific Material</li>
     *     <li>a platform specific ItemStack</li>
     *     <li>an {@link ItemBuilder}</li>
     * </ul>
     *
     * @param object the object by which the ItemBuilder should be created.
     * @return a new {@link ItemBuilder}
     */
    @Api
    @NotNull
    ItemBuilder item(@NotNull Object object);
}
