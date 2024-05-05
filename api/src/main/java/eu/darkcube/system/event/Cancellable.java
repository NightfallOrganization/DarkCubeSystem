/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.event;

public interface Cancellable {

    boolean cancelled();

    void cancelled(boolean cancelled);

    default void cancel() {
        this.cancelled(true);
    }

    class Event implements Cancellable {
        private boolean cancelled = false;

        public Event() {
        }

        @Override
        public boolean cancelled() {
            return cancelled;
        }

        @Override
        public void cancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }
}
