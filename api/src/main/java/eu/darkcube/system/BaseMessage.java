/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system;

import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.commandapi.util.Messages.MessageWrapper;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.util.Language;

public interface BaseMessage {

    default String getPrefixModifier() {
        return "";
    }

    default MessageWrapper newWrapper(Object... components) {
        return new MessageWrapper(this, components);
    }

    default SimpleCommandExceptionType newSimpleCommandExceptionType() {
        return new SimpleCommandExceptionType(new MessageWrapper(this));
    }

    default DynamicCommandExceptionType newDynamicCommandExceptionType() {
        return new DynamicCommandExceptionType(o -> {
            if (!(o instanceof Object[])) {
                o = new Object[]{o};
            }
            var components = (Object[]) o;
            return new MessageWrapper(this, components);
        });
    }

    default Dynamic2CommandExceptionType newDynamic2CommandExceptionType() {
        return new Dynamic2CommandExceptionType(this::newWrapper);
    }

    default Dynamic3CommandExceptionType newDynamic3CommandExceptionType() {
        return new Dynamic3CommandExceptionType(this::newWrapper);
    }

    default Dynamic4CommandExceptionType newDynamic4CommandExceptionType() {
        return new Dynamic4CommandExceptionType(this::newWrapper);
    }

    String key();

    default String getMessageString(CommandExecutor executor, Object... args) {
        return LegacyComponentSerializer.legacySection().serialize(getMessage(executor, new String[0], args));
    }

    default Component getMessage(CommandExecutor executor, Object... args) {
        return getMessage(executor, new String[0], args);
    }

    default Component getMessage(Language language, Object... args) {
        return getMessage(language, new String[0], args);
    }

    default Component getMessage(Language language, String[] prefixes, Object... args) {
        return language.getMessage(getPrefixModifier() + String.join("", prefixes) + key(), args);
    }

    default Component getMessage(CommandExecutor executor, String[] prefixes, Object... args) {
        return getMessage(executor.language(), prefixes, args);
    }
}
