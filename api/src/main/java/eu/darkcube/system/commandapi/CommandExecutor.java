/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.util.Language;

public interface CommandExecutor extends Audience {

    default void sendMessage(BaseMessage message, Object... components) {
        this.sendMessage(message.getMessage(this, components));
    }

    default void sendActionBar(BaseMessage message, Object... components) {
        this.sendActionBar(message.getMessage(this, components));
    }

    /**
     * Queries the language.
     *
     * @deprecated {@link #language()}
     */
    @Deprecated(forRemoval = true) default Language getLanguage() {
        return language();
    }

    /**
     * Sets the language.
     *
     * @deprecated {@link #language(Language)}
     */
    @Deprecated(forRemoval = true) default void setLanguage(Language language) {
        language(language);
    }

    Language language();

    void language(Language language);

    default String commandPrefix() {
        return "";
    }

    @Deprecated(forRemoval = true) default String getCommandPrefix() {
        return commandPrefix();
    }
}
