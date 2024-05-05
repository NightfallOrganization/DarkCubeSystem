/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.argument;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class UUIDArgument implements ArgumentType<UUID> {

    private static final DynamicCommandExceptionType INVALID_UUID = Messages.INVALID_UUID.newDynamicCommandExceptionType();

    public static UUIDArgument uuid() {
        return new UUIDArgument();
    }

    public static UUID getUUID(CommandContext<?> context, String name) {
        return context.getArgument(name, UUID.class);
    }

    @Override public UUID parse(StringReader r) throws CommandSyntaxException {
        var s = r.readUnquotedString();
        try {
            return UUID.fromString(s);
        } catch (Exception ex) {
            throw INVALID_UUID.createWithContext(r, s);
        }
    }

    @Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof ISuggestionProvider)) return builder.buildFuture();

        return ISuggestionProvider.suggest(((ISuggestionProvider) context.getSource()).getPlayerUniqueIds().stream().map(UUID::toString), builder);
    }
}
