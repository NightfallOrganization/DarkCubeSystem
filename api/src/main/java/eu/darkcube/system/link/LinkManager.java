/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.link;

import java.util.ArrayList;
import java.util.Collection;

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
        links.forEach(Link::tryLinkAndEnable);
    }

    @Api
    public Collection<Link> links() {
        return links.stream().filter(Link::isLinked).toList();
    }

    @Api
    public void unregisterLinks() {
        this.links.forEach(this::unregisterLink);
    }

    @Api
    public void unregisterLink(Link link) {
        link.disableAndUnlink();
        links.remove(link);
    }

    @Api
    public interface LinkSupplier {
        @Api
        Link get() throws Throwable;
    }
}
