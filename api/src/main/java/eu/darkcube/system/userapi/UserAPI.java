/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import java.util.UUID;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api public interface UserAPI {

    static @NotNull UserAPI instance() {
        return UserAPIHolder.instance();
    }

    /**
     * Old method for acquiring the instance.
     *
     * @deprecated {@link #instance()}
     */
    @Deprecated(forRemoval = true) static @NotNull UserAPI getInstance() {
        return instance();
    }

    /**
     * Queries a user via his UUID. This will always return a valid User instance. Also works if the User is offline
     *
     * @param uniqueId the player uniqueId
     * @return the {@link User}
     */
    @NotNull User user(UUID uniqueId);

    /**
     * Old method for acquiring a user.
     *
     * @deprecated {@link #user(UUID)}
     */
    @Deprecated(forRemoval = true) default @NotNull User getUser(UUID uuid) {
        return user(uuid);
    }

    @Api void addModifier(UserModifier modifier);

    @Api void removeModifier(UserModifier modifier);
}
