/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.container;

import static eu.darkcube.system.server.inventory.container.ContainerProviderImpl.PROVIDER;

import java.util.List;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public interface Container {
    @Nullable
    ItemBuilder getAt(int slot);

    void setAt(int slot, @Nullable ItemBuilder item);

    void addListener(@NotNull ContainerListener listener);

    void removeListener(@NotNull ContainerListener listener);

    @Api
    @NotNull
    @Unmodifiable
    List<ContainerListener> listeners();

    boolean canPutItem(@NotNull User user, int slot, int putAmount);

    boolean canTakeItem(@NotNull User user, int slot, int takeAmount);

    @ApiStatus.Experimental
    default int getMaxPutAmount(@NotNull User user, int slot, int putAmount) {
        return putAmount;
    }

    @ApiStatus.Experimental
    default int getMaxTakeAmount(@NotNull User user, int slot, int takeAmount) {
        return takeAmount;
    }

    /**
     * Checks if the user can swap the current item with another item.
     * <p>
     * Defaults to {@link #canPutItem(User, int, int)} &amp;&amp; {@link #canTakeItem(User, int, int)}
     */
    default boolean canChangeItem(@NotNull User user, int slot, int takeAmount, int putAmount) {
        return canPutItem(user, slot, putAmount) && canTakeItem(user, slot, takeAmount);
    }

    int size();

    static @NotNull Container simple(int size) {
        return PROVIDER.simple(size);
    }
}
