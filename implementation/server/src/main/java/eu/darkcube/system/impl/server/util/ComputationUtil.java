/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.item.ItemFactory;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public class ComputationUtil {

    public static <Player> Computer<Void, Player> createPlayerComputer(Class<Player> playerType, @NotNull Function<@NotNull User, @Nullable Player> playerProvider) {
        var computer = new Computer<Void, Player>(playerType);
        computer.addInstruction(new ComputerInstruction<>() {
            @Override
            public boolean shouldCompute(@NotNull Object object, @UnknownNullability Void unused) {
                return object instanceof User;
            }

            @Override
            public @Nullable Object compute(@NotNull Object object, @UnknownNullability Void unused) {
                return playerProvider.apply((User) object);
            }
        });
        computer.allowNull();
        return computer;
    }

    public static <ItemStack> Computer<User, ItemStack> createCommonItemComputer(Class<ItemStack> itemStackType) {
        var computer = new Computer<User, ItemStack>(itemStackType);
        computer.addInstruction(new ComputerInstruction<>() {
            @Override
            public boolean shouldCompute(@NotNull Object object, @UnknownNullability User user) {
                return object instanceof ItemBuilder;
            }

            @Override
            public @NotNull Object compute(@NotNull Object object, @UnknownNullability User user) {
                return ((ItemBuilder) object).build();
            }
        });
        computer.addInstruction(new ComputerInstruction<>() {
            @Override
            public boolean shouldCompute(@NotNull Object object, @UnknownNullability User user) {
                return object instanceof ItemFactory && user != null;
            }

            @Override
            public @NotNull Object compute(@NotNull Object object, @UnknownNullability User user) {
                return ((ItemFactory) object).createItem(user);
            }
        });
        computer.addInstruction(new ComputerInstruction<>() {
            @Override
            public boolean shouldCompute(@NotNull Object object, @UnknownNullability User user) {
                return object instanceof Function<?, ?> && user != null;
            }

            @Override
            public @NotNull Object compute(@NotNull Object object, @UnknownNullability User user) {
                return ((Function<User, ?>) object).apply(user);
            }
        });
        computer.addInstruction(new ComputerInstruction<>() {
            @Override
            public boolean shouldCompute(@NotNull Object object, @UnknownNullability User user) {
                return object instanceof Supplier<?>;
            }

            @Override
            public @NotNull Object compute(@NotNull Object object, @UnknownNullability User user) {
                return ((Supplier<?>) object).get();
            }
        });
        return computer;
    }

    public static class Computer<Context, ReturnType> {
        private final Class<ReturnType> type;
        private final ArrayList<ComputerInstruction<Context>> instructions = new ArrayList<>();
        private boolean allowNull = false;

        public Computer(Class<ReturnType> type) {
            this.type = type;
        }

        public Computer<Context, ReturnType> allowNull() {
            allowNull = true;
            return this;
        }

        public void addInstruction(ComputerInstruction<Context> instruction) {
            instructions.add(instruction);
        }

        public void addInstructions(Collection<ComputerInstruction<Context>> instructions) {
            this.instructions.addAll(instructions);
        }

        @NotNull
        @Unmodifiable
        public Collection<ComputerInstruction<Context>> instructions() {
            return List.copyOf(instructions);
        }

        @UnknownNullability
        public ReturnType compute(@UnknownNullability Object object, @UnknownNullability Context context) {
            var current = object;
            loop:
            while (true) {
                if (current != null) {
                    if (type.isInstance(current)) {
                        return type.cast(current);
                    }
                    for (var i = 0; i < instructions.size(); i++) {
                        var instruction = instructions.get(i);
                        if (instruction.shouldCompute(current, context)) {
                            current = instruction.compute(current, context);
                            continue loop;
                        }
                    }
                } else if (allowNull) {
                    return null;
                }
                throw new IllegalArgumentException("Item is a bad type: " + current + ", original: " + object);
            }
        }
    }

    public interface ComputerInstruction<Context> {
        boolean shouldCompute(@NotNull Object object, @UnknownNullability Context context);

        @UnknownNullability
        Object compute(@NotNull Object object, @UnknownNullability Context context);
    }
}
