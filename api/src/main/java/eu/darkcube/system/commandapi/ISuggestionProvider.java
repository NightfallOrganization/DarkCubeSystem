/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

@Api public interface ISuggestionProvider {
    @Api static CompletableFuture<Suggestions> suggestVec3(String input, Collection<Coordinates> coordinates, SuggestionsBuilder builder, Predicate<String> filter) {
        List<String> list = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            for (var coordinate : coordinates) {
                var s = coordinate.x + " " + coordinate.y + " " + coordinate.z;
                if (filter.test(s)) {
                    list.add(coordinate.x);
                    list.add(coordinate.x + " " + coordinate.y);
                    list.add(s);
                }
            }
        } else {
            var args = input.split(" ");
            if (args.length == 1) {
                for (var coordinate : coordinates) {
                    var s1 = args[0] + " " + coordinate.y + " " + coordinate.z;
                    if (filter.test(s1)) {
                        list.add(args[0] + " " + coordinate.y);
                        list.add(s1);
                    }
                }
            } else if (args.length == 2) {
                for (var coordinate : coordinates) {
                    var s2 = args[0] + " " + args[1] + " " + coordinate.z;
                    if (filter.test(s2)) {
                        list.add(s2);
                    }
                }
            }
        }

        return suggest(list, builder);
    }

    @Api static CompletableFuture<Suggestions> suggestVec2(String input, Collection<Coordinates> coordinates, SuggestionsBuilder builder, Predicate<String> filter) {
        List<String> list = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            for (var coordinate : coordinates) {
                var s = coordinate.x + " " + coordinate.z;
                if (filter.test(s)) {
                    list.add(coordinate.x);
                    list.add(s);
                }
            }
        } else {
            var args = input.split(" ");
            if (args.length == 1) {
                for (var coordinate : coordinates) {
                    var s1 = args[0] + " " + coordinate.z;
                    if (filter.test(s1)) {
                        list.add(s1);
                    }
                }
            }
        }

        return suggest(list, builder);
    }

    @Api static CompletableFuture<Suggestions> suggest(Iterable<String> suggestions, SuggestionsBuilder builder) {
        var prefix = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (var suggestion : suggestions) {
            if (test(prefix, suggestion.toLowerCase(Locale.ROOT))) {
                builder.suggest(suggestion);
            }
        }
        return builder.buildFuture();
    }

    @Api static CompletableFuture<Suggestions> suggest(Stream<String> suggestions, SuggestionsBuilder builder) {
        var input = builder.getRemaining().toLowerCase(Locale.ROOT);
        suggestions.filter((suggestion) -> test(input, suggestion.toLowerCase(Locale.ROOT))).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Api static CompletableFuture<Suggestions> suggest(Iterable<String> suggestions, SuggestionsBuilder builder, String prefix) {
        var input = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (var suggestion : suggestions) {
            var suggest = prefix + suggestion;
            if (test(input, suggest.toLowerCase(Locale.ROOT))) {
                builder.suggest(suggest);
            }
        }
        return builder.buildFuture();
    }

    @Api static CompletableFuture<Suggestions> suggest(String[] suggestions, SuggestionsBuilder builder) {
        var prefix = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (var suggestion : suggestions) {
            if (test(prefix, suggestion.toLowerCase(Locale.ROOT))) {
                builder.suggest(suggestion);
            }
        }

        return builder.buildFuture();
    }

    @Api static boolean test(String input, String suggestion) {
        for (var i = 0; !suggestion.startsWith(input, i); ++i) {
            i = suggestion.indexOf('_', i);
            if (i < 0) {
                return false;
            }
        }
        return true;
    }

    @Api Collection<String> getPlayerNames();

    @Api Collection<UUID> getPlayerUniqueIds();

    @Api default Collection<String> getTargetedEntity() {
        return Collections.emptyList();
    }

    @Api default Collection<Coordinates> getCoordinates() {
        return Collections.singleton(Coordinates.DEFAULT_GLOBAL);
    }

    @Api class Coordinates {
        @Api
        public static final Coordinates DEFAULT_LOCAL = new Coordinates("^", "^", "^");
        @Api
        public static final Coordinates DEFAULT_GLOBAL = new Coordinates("~", "~", "~");
        public final String x;
        public final String y;
        public final String z;

        @Api public Coordinates(String xIn, String yIn, String zIn) {
            this.x = xIn;
            this.y = yIn;
            this.z = zIn;
        }

        @Override public String toString() {
            return "Coordinates [x=" + x + ", y=" + y + ", z=" + z + "]";
        }

    }
}
