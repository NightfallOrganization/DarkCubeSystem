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
import eu.darkcube.system.libs.net.kyori.adventure.text.ComponentLike;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Language;

public interface BaseMessage {
    @NotNull
    default String getPrefixModifier() {
        return "";
    }

    @SuppressWarnings({"DataFlowIssue", "ConstantValue"})
    class S {
        static {

            User u = null;
            BaseMessage m = null, m1 = null;

            u.sendMessage(m, 5);
            u.sendMessage(m1, 3);
            u.sendMessage(m.and(m1, 3), 5);
            u.sendMessage(m1, 3, m1.and(m1, 3));

            u.sendMessage(m.and("\n").and(m1, 3), 5);

        }
    }

    @NotNull
    default BaseMessage and(@NotNull Object message) {
        return and(message, new Object[0]);
    }

    @NotNull
    default BaseMessage and(@NotNull Object message, Object @NotNull ... args) {
        var outer = this;
        var paramArgs = args;
        return new BaseMessage() {
            @Override
            public @NotNull String key() {
                throw new UnsupportedOperationException();
            }

            @Override
            public @NotNull Component getMessage(@NotNull Language language, String @NotNull [] prefixes, Object @NotNull ... args) {
                var first = outer.getMessage(language, prefixes, args);
                return first.append(BaseMessage.getMessage(language, message, paramArgs));
            }
        };
    }

    @NotNull
    private static Component getMessage(@NotNull Language language, @NotNull Object message, Object @NotNull ... args) {
        if (message instanceof ComponentLike c) {
            if (args.length != 0) throw new IllegalArgumentException("Arguments must be empty if message is a Component");
            return c.asComponent();
        }
        if (message instanceof BaseMessage m) {
            return m.getMessage(language, args);
        }
        if (args.length != 0) throw new IllegalArgumentException("This message can't have arguments to replace");
        var string = String.valueOf(message);
        return Component.text(string);
    }

    @NotNull
    default MessageWrapper newWrapper(Object @NotNull ... args) {
        return new MessageWrapper(this, args);
    }

    @NotNull
    default SimpleCommandExceptionType newSimpleCommandExceptionType() {
        return new SimpleCommandExceptionType(new MessageWrapper(this));
    }

    @NotNull
    default DynamicCommandExceptionType newDynamicCommandExceptionType() {
        return new DynamicCommandExceptionType(o -> {
            if (!(o instanceof Object[])) {
                o = new Object[]{o};
            }
            var components = (Object[]) o;
            return new MessageWrapper(this, components);
        });
    }

    @NotNull
    default Dynamic2CommandExceptionType newDynamic2CommandExceptionType() {
        return new Dynamic2CommandExceptionType(this::newWrapper);
    }

    @NotNull
    default Dynamic3CommandExceptionType newDynamic3CommandExceptionType() {
        return new Dynamic3CommandExceptionType(this::newWrapper);
    }

    @NotNull
    default Dynamic4CommandExceptionType newDynamic4CommandExceptionType() {
        return new Dynamic4CommandExceptionType(this::newWrapper);
    }

    @NotNull
    String key();

    /**
     * Please use kyori
     */
    @Deprecated(forRemoval = true)
    default String getMessageString(@NotNull CommandExecutor executor, Object @NotNull ... args) {
        return LegacyComponentSerializer.legacySection().serialize(getMessage(executor, new String[0], args));
    }

    @NotNull
    default Component getMessage(@NotNull CommandExecutor executor, Object @NotNull ... args) {
        return getMessage(executor, new String[0], args);
    }

    @NotNull
    default Component getMessage(@NotNull Language language, Object @NotNull ... args) {
        return getMessage(language, new String[0], args);
    }

    @NotNull
    default Component getMessage(@NotNull CommandExecutor executor, String @NotNull [] prefixes, Object @NotNull ... args) {
        return getMessage(executor.language(), prefixes, args);
    }

    @NotNull
    default Component getMessage(@NotNull Language language, @NotNull String @NotNull [] prefixes, Object @NotNull ... args) {
        return language.getMessage(getPrefixModifier() + String.join("", prefixes) + key(), args);
    }
}
