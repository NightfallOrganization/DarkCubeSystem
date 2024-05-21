/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.commandapi.util.Vector3d;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class Vec3Argument implements ArgumentType<ILocationArgument> {
    public static final SimpleCommandExceptionType POS_INCOMPLETE = Messages.POS_INCOMPLETE.newSimpleCommandExceptionType();
    public static final SimpleCommandExceptionType POS_MIXED_TYPES = Messages.POS_MIXED_TYPES.newSimpleCommandExceptionType();
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
    private final boolean centerIntegers;

    private Vec3Argument(boolean centerIntegersIn) {
        this.centerIntegers = centerIntegersIn;
    }

    public static Vec3Argument vec3() {
        return new Vec3Argument(true);
    }

    public static Vec3Argument vec3(boolean centerIntegersIn) {
        return new Vec3Argument(centerIntegersIn);
    }

    public static Vector3d getVec3(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, ILocationArgument.class).getPosition(context.getSource());
    }

    public static ILocationArgument getLocation(CommandContext<?> context, String name) {
        return context.getArgument(name, ILocationArgument.class);
    }

    @Override
    public ILocationArgument parse(StringReader reader) throws CommandSyntaxException {
        return reader.canRead() && reader.peek() == '^' ? LocalLocationArgument.parse(reader) : LocationInput.parseDouble(reader, this.centerIntegers);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> source, SuggestionsBuilder builder) {
        if (!(source.getSource() instanceof ISuggestionProvider)) {
            return Suggestions.empty();
        }
        var s = builder.getRemaining();
        Collection<ISuggestionProvider.Coordinates> collection;
        if (!s.isEmpty() && s.charAt(0) == '^') {
            collection = Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_LOCAL);
        } else {
            collection = ((ISuggestionProvider) source.getSource()).getCoordinates();
        }
        return ISuggestionProvider.suggestVec3(s, collection, builder, Commands.predicate(this::parse));
    }

    @Override
    public Collection<String> getExamples() {
        return Vec3Argument.EXAMPLES;
    }
}
