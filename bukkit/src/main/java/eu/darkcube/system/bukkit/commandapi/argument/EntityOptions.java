/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.util.MathHelper;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.commandapi.util.MinMaxBounds;
import eu.darkcube.system.commandapi.util.MinMaxBoundsWrapped;
import eu.darkcube.system.libs.com.mojang.brigadier.Message;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntityOptions {

    public static final DynamicCommandExceptionType UNKNOWN_ENTITY_OPTION = Messages.UNKNOWN_ENTITY_OPTION.newDynamicCommandExceptionType();
    public static final DynamicCommandExceptionType INAPPLICABLE_ENTITY_OPTION = Messages.INAPPLICABLE_ENTITY_OPTION.newDynamicCommandExceptionType();
    public static final SimpleCommandExceptionType NEGATIVE_DISTANCE = Messages.NEGATIVE_DISTANCE.newSimpleCommandExceptionType();
    public static final SimpleCommandExceptionType NEGATIVE_LEVEL = Messages.NEGATIVE_LEVEL.newSimpleCommandExceptionType();
    public static final SimpleCommandExceptionType NONPOSITIVE_LIMIT = Messages.NONPOSITIVE_LIMIT.newSimpleCommandExceptionType();
    public static final DynamicCommandExceptionType INVALID_SORT = Messages.INVALID_SORT.newDynamicCommandExceptionType();
    public static final DynamicCommandExceptionType INVALID_GAME_MODE = Messages.INVALID_GAME_MODE.newDynamicCommandExceptionType();
    public static final DynamicCommandExceptionType INVALID_ENTITY_TYPE = Messages.INVALID_ENTITY_TYPE.newDynamicCommandExceptionType();
    private static final Map<String, OptionHandler> REGISTRY = Maps.newHashMap();

    public static void register(String id, IFilter handler, Predicate<EntitySelectorParser> filter, Message tooltip) {
        EntityOptions.REGISTRY.put(id, new OptionHandler(handler, filter, tooltip));
    }

    public static void registerOptions() {
        if (EntityOptions.REGISTRY.isEmpty()) {
            EntityOptions.register("name", (parser) -> {
                var i = parser.getReader().getCursor();
                var flag = parser.shouldInvertValue();
                var s = parser.getReader().readString();
                if (parser.hasNameNotEquals() && !flag) {
                    parser.getReader().setCursor(i);
                    throw EntityOptions.INAPPLICABLE_ENTITY_OPTION.createWithContext(parser.getReader(), "name");
                }
                if (flag) {
                    parser.setHasNameNotEquals(true);
                } else {
                    parser.setHasNameEquals(true);
                }

                parser.addFilter((entity) -> (entity.getCustomName() == null ? entity.getName() : entity.getCustomName()).equals(s) != flag);
            }, (parser) -> !parser.hasNameEquals(), null);
            EntityOptions.register("distance", (parser) -> {
                var i = parser.getReader().getCursor();
                var minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromReader(parser.getReader());
                if ((minmaxbounds$floatbound.getMin() == null || !(minmaxbounds$floatbound.getMin() < 0.0F)) && (minmaxbounds$floatbound.getMax() == null || !(minmaxbounds$floatbound.getMax() < 0.0F))) {
                    parser.setDistance(minmaxbounds$floatbound);
                    parser.setCurrentWorldOnly();
                } else {
                    parser.getReader().setCursor(i);
                    throw EntityOptions.NEGATIVE_DISTANCE.createWithContext(parser.getReader());
                }
            }, (parser) -> parser.getDistance().isUnbounded(), null);
            EntityOptions.register("level", (parser) -> {
                var i = parser.getReader().getCursor();
                var minmaxbounds$intbound = MinMaxBounds.IntBound.fromReader(parser.getReader());
                if ((minmaxbounds$intbound.getMin() == null || minmaxbounds$intbound.getMin() >= 0) && (minmaxbounds$intbound.getMax() == null || minmaxbounds$intbound.getMax() >= 0)) {
                    parser.setLevel(minmaxbounds$intbound);
                    parser.setIncludeNonPlayers(false);
                } else {
                    parser.getReader().setCursor(i);
                    throw EntityOptions.NEGATIVE_LEVEL.createWithContext(parser.getReader());
                }
            }, (parser) -> parser.getLevel().isUnbounded(), null);
            EntityOptions.register("x", (parser) -> {
                parser.setCurrentWorldOnly();
                parser.setX(parser.getReader().readDouble());
            }, (parser) -> parser.getX() == null, null);
            EntityOptions.register("y", (parser) -> {
                parser.setCurrentWorldOnly();
                parser.setY(parser.getReader().readDouble());
            }, (parser) -> parser.getY() == null, null);
            EntityOptions.register("z", (parser) -> {
                parser.setCurrentWorldOnly();
                parser.setZ(parser.getReader().readDouble());
            }, (parser) -> parser.getZ() == null, null);
            EntityOptions.register("dx", (parser) -> {
                parser.setCurrentWorldOnly();
                parser.setDx(parser.getReader().readDouble());
            }, (parser) -> parser.getDx() == null, null);
            EntityOptions.register("dy", (parser) -> {
                parser.setCurrentWorldOnly();
                parser.setDy(parser.getReader().readDouble());
            }, (parser) -> parser.getDy() == null, null);
            EntityOptions.register("dz", (parser) -> {
                parser.setCurrentWorldOnly();
                parser.setDz(parser.getReader().readDouble());
            }, (parser) -> parser.getDz() == null, null);
            EntityOptions.register("x_rotation", (parser) -> parser.setXRotation(MinMaxBoundsWrapped.fromReader(parser.getReader(), true, MathHelper::wrapDegrees)), (parser) -> parser.getXRotation() == MinMaxBoundsWrapped.UNBOUNDED, null);
            EntityOptions.register("y_rotation", (parser) -> parser.setYRotation(MinMaxBoundsWrapped.fromReader(parser.getReader(), true, MathHelper::wrapDegrees)), (parser) -> parser.getYRotation() == MinMaxBoundsWrapped.UNBOUNDED, null);
            EntityOptions.register("limit", (parser) -> {
                var i = parser.getReader().getCursor();
                var j = parser.getReader().readInt();
                if (j < 1) {
                    parser.getReader().setCursor(i);
                    throw EntityOptions.NONPOSITIVE_LIMIT.createWithContext(parser.getReader());
                }
                parser.setLimit(j);
                parser.setLimited(true);
            }, (parser) -> !parser.isCurrentEntity() && !parser.isLimited(), null);
            EntityOptions.register("sort", (parser) -> {
                var i = parser.getReader().getCursor();
                var s = parser.getReader().readUnquotedString();
                parser.setSuggestionHandler((builder, p_202056_1_) -> ISuggestionProvider.suggest(Arrays.asList("nearest", "furthest", "random", "arbitrary"), builder));
                var biconsumer = switch (s) {
                    case "nearest" -> EntitySelectorParser.NEAREST;
                    case "furthest" -> EntitySelectorParser.FURTHEST;
                    case "random" -> EntitySelectorParser.RANDOM;
                    case "arbitrary" -> EntitySelectorParser.ARBITRARY;
                    default -> {
                        parser.getReader().setCursor(i);
                        throw EntityOptions.INVALID_SORT.createWithContext(parser.getReader(), s);
                    }
                };

                parser.setSorter(biconsumer);
                parser.setSorted(true);
            }, (parser) -> !parser.isCurrentEntity() && !parser.isSorted(), null);
            EntityOptions.register("gamemode", (parser) -> {
                parser.setSuggestionHandler((suggestionBuilder, p_202018_2_) -> {
                    var s1 = suggestionBuilder.getRemaining().toLowerCase(Locale.ROOT);
                    var flag1 = !parser.hasGamemodeNotEquals();
                    var flag2 = true;
                    if (!s1.isEmpty()) {
                        if (s1.charAt(0) == '!') {
                            flag1 = false;
                            s1 = s1.substring(1);
                        } else {
                            flag2 = false;
                        }
                    }

                    for (var gamemode : GameMode.values()) {
                        if (gamemode.name().toLowerCase(Locale.ROOT).startsWith(s1)) {
                            if (flag2) {
                                suggestionBuilder.suggest('!' + gamemode.name().toLowerCase(Locale.ROOT));
                            }

                            if (flag1) {
                                suggestionBuilder.suggest(gamemode.name().toLowerCase(Locale.ROOT));
                            }
                        }
                    }

                    return suggestionBuilder.buildFuture();
                });
                var i = parser.getReader().getCursor();
                var flag = parser.shouldInvertValue();
                if (parser.hasGamemodeNotEquals() && !flag) {
                    parser.getReader().setCursor(i);
                    throw EntityOptions.INAPPLICABLE_ENTITY_OPTION.createWithContext(parser.getReader(), "gamemode");
                }
                var s = parser.getReader().readUnquotedString();
                GameMode gametype = null;
                for (var mode : GameMode.values()) {
                    if (s.equals(mode.name().toLowerCase(Locale.ROOT))) {
                        gametype = mode;
                    }
                }
                final var gamemode = gametype;
                if (gametype == null) {
                    parser.getReader().setCursor(i);
                    throw EntityOptions.INVALID_GAME_MODE.createWithContext(parser.getReader(), s);
                }
                parser.setIncludeNonPlayers(false);
                parser.addFilter((entity) -> {
                    if (!(entity instanceof Player)) {
                        return false;
                    }
                    var gameMode = ((Player) entity).getGameMode();
                    return flag == (gameMode != gamemode);
                });
                if (flag) {
                    parser.setHasGamemodeNotEquals(true);
                } else {
                    parser.setHasGamemodeEquals(true);
                }
            }, (parser) -> !parser.hasGamemodeEquals(), null);
            EntityOptions.register("type", (parser) -> {
                parser.setSuggestionHandler((suggestionBuilder, unused) -> {
                    ISuggestionProvider.suggest(Arrays.stream(EntityType.values()).map(e -> e.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList()), suggestionBuilder, String.valueOf('!'));
                    if (!parser.isTypeLimitedInversely()) {
                        ISuggestionProvider.suggest(Arrays.stream(EntityType.values()).map(e -> e.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList()), suggestionBuilder);
                    }
                    return suggestionBuilder.buildFuture();
                });
                var i = parser.getReader().getCursor();
                var inverted = parser.shouldInvertValue();
                if (parser.isTypeLimitedInversely() && !inverted) {
                    parser.getReader().setCursor(i);
                    throw EntityOptions.INAPPLICABLE_ENTITY_OPTION.createWithContext(parser.getReader(), "type");
                }
                if (inverted) {
                    parser.setTypeLimitedInversely();
                }

                var resourcelocation1 = EntityOptions.read(parser.getReader());
                var entitytype = Arrays.stream(EntityType.values()).filter(e -> e.name().toLowerCase(Locale.ROOT).equals(resourcelocation1.toLowerCase(Locale.ROOT))).findAny().orElseThrow(() -> {
                    parser.getReader().setCursor(i);
                    return EntityOptions.INVALID_ENTITY_TYPE.createWithContext(parser.getReader(), resourcelocation1);
                });
                if (Objects.equal(EntityType.PLAYER, entitytype) && !inverted) {
                    parser.setIncludeNonPlayers(false);
                }

                parser.addFilter((entity) -> Objects.equal(entitytype, entity.getType()) != inverted);
                if (!inverted) {
                    parser.setEntityType(entitytype);
                }
            }, (parser) -> !parser.isTypeLimited(), null);
        }
    }

    public static String read(StringReader reader) {
        var i = reader.getCursor();

        while (reader.canRead() && EntityOptions.isValidPathCharacter(reader.peek())) {
            reader.skip();
        }

        return reader.getString().substring(i, reader.getCursor());
    }

    public static boolean isValidPathCharacter(char charIn) {
        return charIn >= '0' && charIn <= '9' || charIn >= 'a' && charIn <= 'z' || charIn == '_' || charIn == ':' || charIn == '/' || charIn == '.' || charIn == '-';
    }

    public static IFilter get(EntitySelectorParser parser, String id, int cursor) throws CommandSyntaxException {
        var entityoptions$optionhandler = EntityOptions.REGISTRY.get(id);
        if (entityoptions$optionhandler != null) {
            if (entityoptions$optionhandler.canHandle.test(parser)) {
                return entityoptions$optionhandler.handler;
            }
            throw EntityOptions.INAPPLICABLE_ENTITY_OPTION.createWithContext(parser.getReader(), id);
        }
        parser.getReader().setCursor(cursor);
        throw EntityOptions.UNKNOWN_ENTITY_OPTION.createWithContext(parser.getReader(), id);
    }

    public static void suggestOptions(EntitySelectorParser parser, SuggestionsBuilder builder) {
        var s = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (var entry : EntityOptions.REGISTRY.entrySet()) {
            if ((entry.getValue()).canHandle.test(parser) && entry.getKey().toLowerCase(Locale.ROOT).startsWith(s)) {
                builder.suggest(entry.getKey() + '=', (entry.getValue()).tooltip);
            }
        }

    }

    public interface IFilter {
        void handle(EntitySelectorParser parser) throws CommandSyntaxException;
    }

    public static class OptionHandler {

        public final IFilter handler;
        public final Predicate<EntitySelectorParser> canHandle;
        public final Message tooltip;

        private OptionHandler(IFilter handlerIn, Predicate<EntitySelectorParser> canHandle, Message tooltipIn) {
            this.handler = handlerIn;
            this.canHandle = canHandle;
            this.tooltip = tooltipIn;
        }

    }

}
