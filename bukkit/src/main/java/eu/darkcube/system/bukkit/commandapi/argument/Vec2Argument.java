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
import eu.darkcube.system.commandapi.util.Vector2f;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class Vec2Argument implements ArgumentType<ILocationArgument> {
    public static final SimpleCommandExceptionType VEC2_INCOMPLETE = Messages.VEC2_INCOMPLETE.newSimpleCommandExceptionType();
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
    private final boolean centerIntegers;

    public Vec2Argument(boolean centerIntegersIn) {
        this.centerIntegers = centerIntegersIn;
    }

    public static Vec2Argument vec2() {
        return new Vec2Argument(true);
    }

    public static Vector2f getVec2f(CommandContext<CommandSource> context, String name) {
        var vector3d = context.getArgument(name, ILocationArgument.class).getPosition(context.getSource());
        return new Vector2f((float) vector3d.x, (float) vector3d.z);
    }

    @Override
    public ILocationArgument parse(StringReader reader) throws CommandSyntaxException {
        var i = reader.getCursor();
        if (!reader.canRead()) {
            throw Vec2Argument.VEC2_INCOMPLETE.createWithContext(reader);
        }
        var locationPart = LocationPart.parseDouble(reader, this.centerIntegers);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            var locationPart2 = LocationPart.parseDouble(reader, this.centerIntegers);
            return new LocationInput(locationPart, new LocationPart(true, 0.0D), locationPart2);
        }
        reader.setCursor(i);
        throw Vec2Argument.VEC2_INCOMPLETE.createWithContext(reader);
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

        return ISuggestionProvider.suggestVec2(s, collection, builder, Commands.predicate(this::parse));
    }

    @Override
    public Collection<String> getExamples() {
        return Vec2Argument.EXAMPLES;
    }
}
