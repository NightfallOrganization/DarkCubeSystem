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
import eu.darkcube.system.util.Language;

public interface BaseMessage {
    @NotNull
    static BaseMessage empty() {
        return BaseMessageImpl.EMPTY;
    }

    @NotNull
    static BaseMessage string(@NotNull String string) {
        return new BaseMessage() {
            @Override
            public @NotNull String key() {
                return "";
            }

            @Override
            public @NotNull Component getMessage(@NotNull Language language, @NotNull String @NotNull [] prefixes, Object @NotNull ... args) {
                return Component.text(string);
            }
        };
    }

    @NotNull
    default String getPrefixModifier() {
        return "";
    }

    @NotNull
    default BaseMessage append(@NotNull Object message) {
        return and(message);
    }

    @NotNull
    default BaseMessage append(@NotNull Object message, Object @NotNull ... args) {
        return and(message, args);
    }

    @NotNull
    default BaseMessage and(@NotNull Object message) {
        return and(message, new Object[0]);
    }

    @NotNull
    default BaseMessage and(@NotNull Object message, Object @NotNull ... args) {
        var outer = this;
        var paramArgs = args;
        return new Transforming() {
            @Override
            public @NotNull Component getMessage(@NotNull Language language, String @NotNull [] prefixes, Object @NotNull ... args) {
                var first = outer.getMessage(language, prefixes, args);
                return first.append(BaseMessage.getMessage(language, message, paramArgs));
            }
        };
    }

    @NotNull
    default BaseMessage prepend(@NotNull Object message) {
        return prepend(message, new Object[0]);
    }

    @NotNull
    default BaseMessage prepend(@NotNull Object message, Object @NotNull ... args) {
        var outer = this;
        var paramArgs = args;
        return new Transforming() {
            @Override
            public @NotNull Component getMessage(@NotNull Language language, @NotNull String @NotNull [] prefixes, Object @NotNull ... args) {
                var last = outer.getMessage(language, prefixes, args);
                return BaseMessage.getMessage(language, message, paramArgs).append(last);
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
        return LegacyComponentSerializer.legacySection().deserialize(string);
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

    interface Transforming extends BaseMessage {
        @Override
        default @NotNull String key() {
            throw new UnsupportedOperationException();
        }

        @Override
        @NotNull
        Component getMessage(@NotNull Language language, @NotNull String @NotNull [] prefixes, Object @NotNull ... args);
    }
}
