/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.link;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Link {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private final AtomicBoolean link = new AtomicBoolean(true);
    private final AtomicBoolean linked = new AtomicBoolean(false);

    public Link() {
    }

    boolean isLinked() {
        return linked.get();
    }

    private void link0() throws Throwable {
        if (link.compareAndSet(true, false)) {
            link();
            logger.info("Linked link {}", getClass().getSimpleName());
            linked.set(true);
        }
    }

    protected abstract void link() throws Throwable;

    private void enable() {
        if (!linked.get()) return;
        if (enabled.compareAndSet(false, true)) {
            onEnable();
            logger.info("Enabled link {}", getClass().getSimpleName());
        }
    }

    private void disable() {
        if (!linked.get()) return;
        if (enabled.compareAndSet(true, false)) {
            logger.info("Disabling link {}", getClass().getSimpleName());
            onDisable();
        }
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    private void unlink0() {
        if (linked.compareAndSet(true, false)) {
            logger.info("Unlinking link {}", getClass().getSimpleName());
            unlink();
        }
    }

    protected abstract void unlink();

    protected final void disableAndUnlink() {
        disable();
        unlink0();
    }

    protected final void tryLinkAndEnable() {
        try {
            link0();
            enable();
        } catch (Throwable t) {
            logger.warn("Failed to enable link {}", getClass().getSimpleName(), t);
        }
    }
}
