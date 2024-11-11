/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.commandapi.argument;

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
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.enchant.Enchantment;

public class EnchantmentArgument implements ArgumentType<Enchantment> {
    private static final DynamicCommandExceptionType INVALID_INPUT = Messages.INVALID_ENUM.newDynamicCommandExceptionType();

    @SuppressWarnings("PatternValidation")
    @Override
    public Enchantment parse(StringReader reader) throws CommandSyntaxException {
        var input = reader.readUnquotedString();
        if (!Key.parseable(input)) throw INVALID_INPUT.createWithContext(reader, input);
        var key = Key.key(input);
        try {
            return Enchantment.of(key);
        } catch (Throwable t) {
            throw INVALID_INPUT.createWithContext(reader, input);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(Enchantment.values().stream().map(e -> e.key().asString()), builder);
    }
}
