/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import com.google.common.collect.Maps;
import eu.darkcube.system.bukkit.commandapi.BukkitVector3d;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.commandapi.util.Vector3d;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class EntityAnchorArgument implements ArgumentType<EntityAnchorArgument.Type> {
    private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");

    private static final DynamicCommandExceptionType ANCHOR_INVALID = Messages.ANCHOR_INVALID.newDynamicCommandExceptionType();

    public static Type getEntityAnchor(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, Type.class);
    }

    public static EntityAnchorArgument entityAnchor() {
        return new EntityAnchorArgument();
    }

    @Override
    public Type parse(StringReader reader) throws CommandSyntaxException {
        var i = reader.getCursor();
        var s = reader.readUnquotedString();
        var entityanchorargument$type = Type.getByName(s);
        if (entityanchorargument$type == null) {
            reader.setCursor(i);
            throw EntityAnchorArgument.ANCHOR_INVALID.createWithContext(reader, s);
        }
        return entityanchorargument$type;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(Type.BY_NAME.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EntityAnchorArgument.EXAMPLES;
    }

    public enum Type {
        FEET("feet", (pos, entity) -> pos),
        EYES("eyes", (pos, entity) -> new Vector3d(pos.x, pos.y + (entity instanceof LivingEntity ? ((LivingEntity) entity).getEyeHeight() : 0), pos.z));

        private static final Map<String, Type> BY_NAME = Maps.newHashMap();

        static {
            for (var type : Type.values()) {
                Type.BY_NAME.put(type.name, type);
            }
        }

        private final String name;
        private final BiFunction<Vector3d, Entity, Vector3d> offsetFunc;

        Type(String nameIn, BiFunction<Vector3d, Entity, Vector3d> offsetFuncIn) {
            this.name = nameIn;
            this.offsetFunc = offsetFuncIn;
        }

        public static Type getByName(String nameIn) {
            return Type.BY_NAME.get(nameIn);
        }

        public Vector3d apply(Entity entityIn) {
            return this.offsetFunc.apply(BukkitVector3d.position(entityIn.getLocation()), entityIn);
        }

        public Vector3d apply(CommandSource sourceIn) {
            var entity = sourceIn.getEntity();
            return entity == null ? sourceIn.getPos() : this.offsetFunc.apply(sourceIn.getPos(), entity);
        }
    }
}
