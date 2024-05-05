/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

@Api public interface UserModifier {

    /**
     * <p>Called when a {@link User} is loaded.</p>
     * <p>This is guaranteed to be called before anyone can interact with the user object.</p>
     */
    @Api default void onLoad(User user) {
    }

    /**
     * <p>Called when a {@link User} is explicitly being unloaded.</p>
     * <p>{@link #onUnload(UserData)} will not be called for this user, see {@link #onUnload(UserData) onUnload} docs for more information</p>
     *
     * @see #onLoad(User)
     * @see #onUnload(UserData)
     */
    @Api default void onUnload(User user) {
    }

    /**
     * <p><b>THIS METHOD DOES NOT DO ANYTHING YET</b></p>
     * <p>Called when a user instance has been garbage collected and the {@link UserData} instance is the only thing remaining of the user.</p>
     * <p>{@link #onUnload(User)} will not be called for the user that was collected. There is no guarantee as to when or on which thread this method is called.</p>
     * <p>By the time this method is called for a {@link UserData} instance, the user that this instance was for may have already been loaded again.</p>
     * <p>Any operations mutating the {@code userData} will still work and eventually mutate the {@link UserData} anyone else has loaded.</p>
     *
     * @see #onUnload(User)
     */
    @ApiStatus.Experimental @Api default void onUnload(UserData userData) {
    }
}
