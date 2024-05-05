/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public interface EventBinding<E> {

    static <E, T> @NotNull FilteredBuilder<E, T> filtered(@NotNull EventFilter<E, T> filter, @NotNull Predicate<T> predicate) {
        return new FilteredBuilder<>(filter, predicate);
    }

    @NotNull Collection<Class<?>> eventTypes();

    @NotNull Consumer<@NotNull E> consumer(@NotNull Class<?> eventType);

    class FilteredBuilder<E, T> {
        private final EventFilter<E, T> filter;
        private final Predicate<T> predicate;
        private final Map<Class<?>, BiConsumer<Object, E>> mapped = new HashMap<>();

        FilteredBuilder(EventFilter<E, T> filter, Predicate<T> predicate) {
            this.filter = filter;
            this.predicate = predicate;
        }

        public <M extends E> FilteredBuilder<E, T> map(@NotNull Class<M> eventType, @NotNull BiConsumer<@NotNull T, @NotNull M> consumer) {
            this.mapped.put(eventType, (BiConsumer<Object, E>) consumer);
            return this;
        }

        public @NotNull EventBinding<E> build() {
            final var copy = Map.copyOf(mapped);
            final var eventTypes = copy.keySet();

            Map<Class<?>, Consumer<E>> consumers = new HashMap<>(eventTypes.size());
            for (var eventType : eventTypes) {
                final var consumer = copy.get(eventType);
                consumers.put(eventType, event -> {
                    final T handler = filter.getHandler(event);
                    if (!predicate.test(handler)) return;
                    consumer.accept(handler, event);
                });
            }
            return new EventBinding<>() {
                @Override
                public @NotNull Collection<Class<?>> eventTypes() {
                    return eventTypes;
                }

                @Override
                public @NotNull Consumer<E> consumer(@NotNull Class<?> eventType) {
                    return consumers.get(eventType);
                }
            };
        }
    }
}
