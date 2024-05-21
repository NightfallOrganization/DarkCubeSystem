/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.common.userapi;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserData;
import eu.darkcube.system.util.Language;

public abstract class CommonUser implements User, UserData.Forwarding {
    private final CommonUserData userData;

    public CommonUser(CommonUserData userData) {
        this.userData = userData;
    }

    /**
     * @return the audience corresponding to this user
     */
    @Override
    public abstract @NotNull Audience audience();

    @Override
    public CommonUserData userData() {
        return userData;
    }

    @Override
    public @NotNull Language language() {
        return Forwarding.super.language();
    }

    @Override
    public void language(@NotNull Language language) {
        Forwarding.super.language(language);
    }
}
