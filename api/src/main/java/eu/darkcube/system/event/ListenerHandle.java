/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.event;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a key to a listenable event, retrievable from {@link EventNode#getHandle(Class)}.
 * Useful to avoid map lookups.
 * <p>
 * It is recommended to store instances of this class in {@code static final} fields.
 *
 * @param <E> the event type
 */
@ApiStatus.Experimental
public interface ListenerHandle<E> {
    /**
     * Calls the given event.
     * Will try to fast exit the execution when possible if {@link #hasListener()} return {@code false}.
     * <p>
     * Anonymous and subclasses are not supported, events must have the exact type {@code E}.
     *
     * @param event the event to call
     */
    void call(@NotNull E event);

    /**
     * Gets if any listener has been registered for the given handle.
     * May trigger an update if the cached data is not correct.
     * <p>
     * Useful if you are able to avoid expensive computation in the case where
     * the event is unused. Be aware that {@link #call(E)}
     * has similar optimization built-in.
     *
     * @return true if the event has 1 or more listeners
     */
    boolean hasListener();
}
