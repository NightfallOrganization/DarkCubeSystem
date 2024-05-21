/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.link;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Link implements Closeable {

    private final AtomicBoolean enabled = new AtomicBoolean(false);

    public Link() {
    }

    protected abstract void link() throws Throwable;

    public final void enable() {
        if (enabled.compareAndSet(false, true)) onEnable();
    }

    public final void disable() {
        if (enabled.compareAndSet(true, false)) onDisable();
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    protected abstract void unlink();

    @Override
    public final void close() {
        unlink();
    }
}
