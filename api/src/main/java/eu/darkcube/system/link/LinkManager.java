/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import eu.darkcube.system.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Api
public class LinkManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("LinkManager");
    private final Collection<Link> links = new ArrayList<>();

    @Api
    public boolean addLink(LinkSupplier supplier) {
        var success = false;
        try {
            this.links.add(supplier.get());
            success = true;
        } catch (Throwable t) {
            LOGGER.warn("Failed to create link", t);
        }
        return success;
    }

    @Api
    public void enableLinks() {
        links.forEach(link -> {
            try {
                link.link();
            } catch (Throwable e) {
                LOGGER.warn(e.getLocalizedMessage());
            }
        });
        links.forEach(Link::enable);
    }

    @Api
    public Collection<Link> links() {
        return Collections.unmodifiableCollection(links);
    }

    @Api
    public void unregisterLinks() {
        new ArrayList<>(this.links).forEach(this::unregisterLink);
    }

    @Api
    public void unregisterLink(Link link) {
        link.disable();
        link.unlink();
        links.remove(link);
    }

    @Api
    public interface LinkSupplier {
        @Api
        Link get() throws Throwable;
    }
}
