/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.Language;

public interface CommandExecutor extends Audience {

    default void sendMessage(@NotNull BaseMessage message, @NotNull Object @NotNull ... args) {
        this.sendMessage(message.getMessage(this, args));
    }

    default void sendActionBar(@NotNull BaseMessage message, @NotNull Object @NotNull ... args) {
        this.sendActionBar(message.getMessage(this, args));
    }

    /**
     * Queries the language.
     *
     * @deprecated {@link #language()}
     */
    @Deprecated(forRemoval = true)
    @NotNull
    default Language getLanguage() {
        return language();
    }

    /**
     * Sets the language.
     *
     * @deprecated {@link #language(Language)}
     */
    @Deprecated(forRemoval = true)
    default void setLanguage(@NotNull Language language) {
        language(language);
    }

    @NotNull
    Language language();

    void language(@NotNull Language language);

    @NotNull
    default String commandPrefix() {
        return "";
    }

    @NotNull
    @Deprecated(forRemoval = true)
    default String getCommandPrefix() {
        return commandPrefix();
    }
}
