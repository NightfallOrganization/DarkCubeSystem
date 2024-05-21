/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import com.google.common.primitives.Doubles;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.bukkit.commandapi.BoundingBox;
import eu.darkcube.system.bukkit.commandapi.BukkitVector3d;
import eu.darkcube.system.commandapi.util.MathHelper;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.commandapi.util.MinMaxBounds;
import eu.darkcube.system.commandapi.util.MinMaxBoundsWrapped;
import eu.darkcube.system.commandapi.util.Vector3d;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntitySelectorParser {

    public static final DynamicCommandExceptionType UNKNOWN_SELECTOR_TYPE = Messages.UNKNOWN_COMMAND_EXCEPTION_TYPE.newDynamicCommandExceptionType();
    public static final SimpleCommandExceptionType INVALID_ENTITY_NAME_OR_UUID = Messages.INVALID_ENTITY_NAME_OR_UUID.newSimpleCommandExceptionType();
    public static final SimpleCommandExceptionType SELECTOR_NOT_ALLOWED = Messages.SELECTOR_NOT_ALLOWED.newSimpleCommandExceptionType();
    public static final SimpleCommandExceptionType SELECTOR_TYPE_MISSING = Messages.SELECTOR_TYPE_MISSING.newSimpleCommandExceptionType();
    public static final SimpleCommandExceptionType EXPECTED_END_OF_OPTIONS = Messages.EXPECTED_END_OF_OPTIONS.newSimpleCommandExceptionType();
    public static final DynamicCommandExceptionType EXPECTED_VALUE_FOR_OPTION = Messages.EXPECTED_VALUE_FOR_OPTION.newDynamicCommandExceptionType();
    public static final BiConsumer<Vector3d, List<? extends Entity>> ARBITRARY = (vec, entities) -> {
    };
    public static final BiConsumer<Vector3d, List<? extends Entity>> NEAREST = (vec, entities) -> entities.sort((e1, e2) -> Doubles.compare(BukkitVector3d.position(e1.getLocation()).squareDistanceTo(vec), BukkitVector3d.position(e2.getLocation()).squareDistanceTo(vec)));
    public static final BiConsumer<Vector3d, List<? extends Entity>> FURTHEST = (vec, entities) -> entities.sort((e1, e2) -> Doubles.compare(BukkitVector3d.position(e1.getLocation()).squareDistanceTo(vec), BukkitVector3d.position(e2.getLocation()).squareDistanceTo(vec)));
    public static final BiConsumer<Vector3d, List<? extends Entity>> RANDOM = (vec, entities) -> Collections.shuffle(entities);
    public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NONE = (p_201342_0_, p_201342_1_) -> p_201342_0_.buildFuture();

    private final StringReader reader;
    private final boolean hasPermission;
    private int limit;
    private boolean includeNonPlayers;
    private boolean currentWorldOnly;
    private MinMaxBounds.FloatBound distance = MinMaxBounds.FloatBound.UNBOUNDED;
    private MinMaxBounds.IntBound level = MinMaxBounds.IntBound.UNBOUNDED;
    private Double x;
    private Double y;
    private Double z;
    private Double dx;
    private Double dy;
    private Double dz;
    private MinMaxBoundsWrapped xRotation = MinMaxBoundsWrapped.UNBOUNDED;
    private MinMaxBoundsWrapped yRotation = MinMaxBoundsWrapped.UNBOUNDED;
    private Predicate<Entity> filter = entity -> true;
    private BiConsumer<Vector3d, List<? extends Entity>> sorter = EntitySelectorParser.ARBITRARY;
    private boolean self;
    private String username;
    private int cursorStart;
    private UUID uuid;
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionHandler = EntitySelectorParser.SUGGEST_NONE;
    private boolean hasNameEquals;
    private boolean hasNameNotEquals;
    private boolean isLimited;
    private boolean isSorted;
    private boolean hasGamemodeEquals;
    private boolean hasGamemodeNotEquals;
    private boolean hasTeamEquals;
    private EntityType type;
    private boolean typeInverse;
    private boolean hasScores;
    private boolean hasAdvancements;

    public EntitySelectorParser(StringReader readerIn) {
        this(readerIn, true);
    }

    public EntitySelectorParser(StringReader readerIn, boolean hasPermissionIn) {
        this.reader = readerIn;
        this.hasPermission = hasPermissionIn;
    }

    private static void fillSelectorSuggestions(SuggestionsBuilder suggestionBuilder) {
        suggestionBuilder.suggest("@p", Messages.SELECTOR_NEAREST_PLAYER.newWrapper());
        suggestionBuilder.suggest("@a", Messages.SELECTOR_ALL_PLAYERS.newWrapper());
        suggestionBuilder.suggest("@r", Messages.SELECTOR_RANDOM_PLAYER.newWrapper());
        suggestionBuilder.suggest("@s", Messages.SELECTOR_SELF.newWrapper());
        suggestionBuilder.suggest("@e", Messages.SELECTOR_ALL_ENTITIES.newWrapper());
    }

    public EntitySelector build() {
        BoundingBox bb;
        if (this.dx == null && this.dy == null && this.dz == null) {
            if (this.distance.getMax() != null) {
                float f = this.distance.getMax();
                bb = new BoundingBox(-f, -f, -f, f + 1, f + 1, f + 1);
            } else {
                bb = null;
            }
        } else {
            bb = this.createAABB(this.dx == null ? 0.0D : this.dx, this.dy == null ? 0.0D : this.dy, this.dz == null ? 0.0D : this.dz);
        }
        Function<Vector3d, Vector3d> function;
        if (this.x == null && this.y == null && this.z == null) {
            function = vec -> vec;
        } else {
            function = (vec) -> new Vector3d(this.x == null ? vec.x : this.x, this.y == null ? vec.y : this.y, this.z == null ? vec.z : this.z);
        }
        return new EntitySelector(this.limit, this.includeNonPlayers, this.currentWorldOnly, this.filter, this.distance, function, bb, this.sorter, this.self, this.username, this.uuid, this.type);
    }

    private BoundingBox createAABB(double sizeX, double sizeY, double sizeZ) {
        var flag = sizeX < 0.0D;
        var flag1 = sizeY < 0.0D;
        var flag2 = sizeZ < 0.0D;
        var d0 = flag ? sizeX : 0.0D;
        var d1 = flag1 ? sizeY : 0.0D;
        var d2 = flag2 ? sizeZ : 0.0D;
        var d3 = (flag ? 0.0D : sizeX) + 1.0D;
        var d4 = (flag1 ? 0.0D : sizeY) + 1.0D;
        var d5 = (flag2 ? 0.0D : sizeZ) + 1.0D;
        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    private void updateFilter() {
        if (this.xRotation != MinMaxBoundsWrapped.UNBOUNDED) {
            this.filter = this.filter.and(this.createRotationPredicate(this.xRotation, (entity) -> entity.getLocation().getYaw()));
        }

        if (this.yRotation != MinMaxBoundsWrapped.UNBOUNDED) {
            this.filter = this.filter.and(this.createRotationPredicate(this.yRotation, (entity) -> entity.getLocation().getPitch()));
        }

        if (!this.level.isUnbounded()) {
            this.filter = this.filter.and((entity) -> entity instanceof Player && this.level.test(((Player) entity).getLevel()));
        }

    }

    private Predicate<Entity> createRotationPredicate(MinMaxBoundsWrapped angleBounds, ToDoubleFunction<Entity> angleFunc) {
        double d0 = MathHelper.wrapDegrees(angleBounds.min() == null ? 0.0F : angleBounds.min());
        double d1 = MathHelper.wrapDegrees(angleBounds.max() == null ? 359.0F : angleBounds.max());
        return (p_197374_5_) -> {
            var d2 = MathHelper.wrapDegrees(angleFunc.applyAsDouble(p_197374_5_));
            if (d0 > d1) {
                return d2 >= d0 || d2 <= d1;
            }
            return d2 >= d0 && d2 <= d1;
        };
    }

    protected void parseSelector() throws CommandSyntaxException {
        this.suggestionHandler = this::suggestSelector;
        if (!this.reader.canRead()) {
            throw EntitySelectorParser.SELECTOR_TYPE_MISSING.createWithContext(this.reader);
        }
        var i = this.reader.getCursor();
        var c0 = this.reader.read();
        if (c0 == 'p') {
            this.limit = 1;
            this.includeNonPlayers = false;
            this.sorter = EntitySelectorParser.NEAREST;
            this.setEntityType(EntityType.PLAYER);
        } else if (c0 == 'a') {
            this.limit = Integer.MAX_VALUE;
            this.includeNonPlayers = false;
            this.sorter = EntitySelectorParser.ARBITRARY;
            this.setEntityType(EntityType.PLAYER);
        } else if (c0 == 'r') {
            this.limit = 1;
            this.includeNonPlayers = false;
            this.sorter = EntitySelectorParser.RANDOM;
            this.setEntityType(EntityType.PLAYER);
        } else if (c0 == 's') {
            this.limit = 1;
            this.includeNonPlayers = true;
            this.self = true;
        } else {
            if (c0 != 'e') {
                this.reader.setCursor(i);
                throw EntitySelectorParser.UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, '@' + String.valueOf(c0));
            }

            this.limit = Integer.MAX_VALUE;
            this.includeNonPlayers = true;
            this.sorter = EntitySelectorParser.ARBITRARY;
            this.filter = e -> !e.isDead() && e.isValid();
        }

        this.suggestionHandler = this::suggestOpenBracket;
        if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestionHandler = this::suggestOptionsOrEnd;
            this.parseArguments();
        }
    }

    protected void parseSingleEntity() throws CommandSyntaxException {
        if (this.reader.canRead()) {
            this.suggestionHandler = this::suggestName;
        }

        var i = this.reader.getCursor();
        var s = this.reader.readString();

        try {
            this.uuid = UUID.fromString(s);
            this.includeNonPlayers = true;
        } catch (IllegalArgumentException illegalargumentexception) {
            if (s.isEmpty() || s.length() > 16) {
                this.reader.setCursor(i);
                throw EntitySelectorParser.INVALID_ENTITY_NAME_OR_UUID.createWithContext(this.reader);
            }

            this.includeNonPlayers = false;
            this.username = s;
        }

        this.limit = 1;
    }

    protected void parseArguments() throws CommandSyntaxException {
        this.suggestionHandler = this::suggestOptions;
        this.reader.skipWhitespace();

        while (true) {
            if (this.reader.canRead() && this.reader.peek() != ']') {
                this.reader.skipWhitespace();
                var i = this.reader.getCursor();
                var s = this.reader.readString();
                var entityoptions$ifilter = EntityOptions.get(this, s, i);
                this.reader.skipWhitespace();
                if (!this.reader.canRead() || this.reader.peek() != '=') {
                    this.reader.setCursor(i);
                    throw EntitySelectorParser.EXPECTED_VALUE_FOR_OPTION.createWithContext(this.reader, s);
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestionHandler = EntitySelectorParser.SUGGEST_NONE;
                entityoptions$ifilter.handle(this);
                this.reader.skipWhitespace();
                this.suggestionHandler = this::suggestCommaOrEnd;
                if (!this.reader.canRead()) {
                    continue;
                }

                if (this.reader.peek() == ',') {
                    this.reader.skip();
                    this.suggestionHandler = this::suggestOptions;
                    continue;
                }

                if (this.reader.peek() != ']') {
                    throw EntitySelectorParser.EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
                }
            }

            if (this.reader.canRead()) {
                this.reader.skip();
                this.suggestionHandler = EntitySelectorParser.SUGGEST_NONE;
                return;
            }

            throw EntitySelectorParser.EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
        }
    }

    @Api
    public boolean shouldInvertValue() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '!') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    @Api
    public StringReader getReader() {
        return this.reader;
    }

    @Api
    public void addFilter(Predicate<Entity> filterIn) {
        this.filter = this.filter.and(filterIn);
    }

    @Api
    public void setCurrentWorldOnly() {
        this.currentWorldOnly = true;
    }

    @Api
    public MinMaxBounds.FloatBound getDistance() {
        return this.distance;
    }

    @Api
    public void setDistance(MinMaxBounds.FloatBound distanceIn) {
        this.distance = distanceIn;
    }

    @Api
    public MinMaxBounds.IntBound getLevel() {
        return this.level;
    }

    @Api
    public void setLevel(MinMaxBounds.IntBound levelIn) {
        this.level = levelIn;
    }

    @Api
    public MinMaxBoundsWrapped getXRotation() {
        return this.xRotation;
    }

    @Api
    public void setXRotation(MinMaxBoundsWrapped xRotationIn) {
        this.xRotation = xRotationIn;
    }

    @Api
    public MinMaxBoundsWrapped getYRotation() {
        return this.yRotation;
    }

    @Api
    public void setYRotation(MinMaxBoundsWrapped yRotationIn) {
        this.yRotation = yRotationIn;
    }

    @Api
    public Double getX() {
        return this.x;
    }

    @Api
    public void setX(double xIn) {
        this.x = xIn;
    }

    @Api
    public Double getY() {
        return this.y;
    }

    @Api
    public void setY(double yIn) {
        this.y = yIn;
    }

    @Api
    public Double getZ() {
        return this.z;
    }

    @Api
    public void setZ(double zIn) {
        this.z = zIn;
    }

    @Api
    public Double getDx() {
        return this.dx;
    }

    @Api
    public void setDx(double dxIn) {
        this.dx = dxIn;
    }

    @Api
    public Double getDy() {
        return this.dy;
    }

    @Api
    public void setDy(double dyIn) {
        this.dy = dyIn;
    }

    @Api
    public Double getDz() {
        return this.dz;
    }

    @Api
    public void setDz(double dzIn) {
        this.dz = dzIn;
    }

    @Api
    public void setLimit(int limitIn) {
        this.limit = limitIn;
    }

    @Api
    public void setIncludeNonPlayers(boolean includeNonPlayersIn) {
        this.includeNonPlayers = includeNonPlayersIn;
    }

    @Api
    public void setSorter(BiConsumer<Vector3d, List<? extends Entity>> sorterIn) {
        this.sorter = sorterIn;
    }

    @Api
    public EntitySelector parse() throws CommandSyntaxException {
        this.cursorStart = this.reader.getCursor();
        this.suggestionHandler = this::suggestNameOrSelector;
        if (this.reader.canRead() && this.reader.peek() == '@') {
            if (!this.hasPermission) {
                throw EntitySelectorParser.SELECTOR_NOT_ALLOWED.createWithContext(this.reader);
            }

            this.reader.skip();
            this.parseSelector();
        } else {
            this.parseSingleEntity();
        }

        this.updateFilter();
        return this.build();
    }

    private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder suggestionBuilder, Consumer<SuggestionsBuilder> consumer) {
        consumer.accept(suggestionBuilder);
        if (this.hasPermission) {
            EntitySelectorParser.fillSelectorSuggestions(suggestionBuilder);
        }

        return suggestionBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        var suggestionsbuilder = builder.createOffset(this.cursorStart);
        consumer.accept(suggestionsbuilder);
        return builder.add(suggestionsbuilder).buildFuture();
    }

    private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        var suggestionsbuilder = builder.createOffset(builder.getStart() - 1);
        EntitySelectorParser.fillSelectorSuggestions(suggestionsbuilder);
        builder.add(suggestionsbuilder);
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenBracket(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        builder.suggest(String.valueOf('['));
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsOrEnd(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        builder.suggest(String.valueOf(']'));
        EntityOptions.suggestOptions(this, builder);
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        EntityOptions.suggestOptions(this, builder);
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestCommaOrEnd(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        builder.suggest(String.valueOf(','));
        builder.suggest(String.valueOf(']'));
        return builder.buildFuture();
    }

    @Api
    public boolean isCurrentEntity() {
        return this.self;
    }

    @Api
    public void setSuggestionHandler(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionHandlerIn) {
        this.suggestionHandler = suggestionHandlerIn;
    }

    @Api
    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        return this.suggestionHandler.apply(builder.createOffset(this.reader.getCursor()), consumer);
    }

    @Api
    public boolean hasNameEquals() {
        return this.hasNameEquals;
    }

    @Api
    public void setHasNameEquals(boolean value) {
        this.hasNameEquals = value;
    }

    @Api
    public boolean hasNameNotEquals() {
        return this.hasNameNotEquals;
    }

    @Api
    public void setHasNameNotEquals(boolean value) {
        this.hasNameNotEquals = value;
    }

    @Api
    public boolean isLimited() {
        return this.isLimited;
    }

    @Api
    public void setLimited(boolean value) {
        this.isLimited = value;
    }

    @Api
    public boolean isSorted() {
        return this.isSorted;
    }

    @Api
    public void setSorted(boolean value) {
        this.isSorted = value;
    }

    @Api
    public boolean hasGamemodeEquals() {
        return this.hasGamemodeEquals;
    }

    @Api
    public void setHasGamemodeEquals(boolean value) {
        this.hasGamemodeEquals = value;
    }

    @Api
    public boolean hasGamemodeNotEquals() {
        return this.hasGamemodeNotEquals;
    }

    @Api
    public void setHasGamemodeNotEquals(boolean value) {
        this.hasGamemodeNotEquals = value;
    }

    @Api
    public boolean hasTeamEquals() {
        return this.hasTeamEquals;
    }

    @Api
    public void setHasTeamEquals(boolean value) {
        this.hasTeamEquals = value;
    }

    @Api
    public void setHasTeamNotEquals(boolean value) {
    }

    @Api
    public void setEntityType(EntityType type) {
        this.type = type;
    }

    @Api
    public void setTypeLimitedInversely() {
        this.typeInverse = true;
    }

    @Api
    public boolean isTypeLimited() {
        return this.type != null;
    }

    @Api
    public boolean isTypeLimitedInversely() {
        return this.typeInverse;
    }

    @Api
    public boolean hasScores() {
        return this.hasScores;
    }

    @Api
    public void setHasScores(boolean value) {
        this.hasScores = value;
    }

    @Api
    public boolean hasAdvancements() {
        return this.hasAdvancements;
    }

    @Api
    public void setHasAdvancements(boolean value) {
        this.hasAdvancements = value;
    }

}
