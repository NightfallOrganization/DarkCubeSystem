/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.paged;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContentProvider;

public class PagedInventoryContentImpl implements PagedInventoryContent {
    private boolean staticUsable = true;
    private boolean staticUsed = false;
    private PagedInventoryContentProvider provider = new StaticPagedInventoryContentProvider();
    private Updater updater = null;

    public PagedInventoryContentImpl() {
    }

    private PagedInventoryContentImpl(boolean staticUsable, boolean staticUsed, PagedInventoryContentProvider provider, Updater updater) {
        this.staticUsable = staticUsable;
        this.staticUsed = staticUsed;
        this.provider = provider;
        this.updater = updater;
    }

    @Override
    public PagedInventoryContentImpl clone() {
        return new PagedInventoryContentImpl(staticUsable, staticUsed, provider, updater);
    }

    @Override
    public @NotNull ItemReference addStaticItem(@NotNull Object item) {
        if (!staticUsable) throw new IllegalStateException("Static Items can't be used with a custom content provider");
        staticUsed = true;
        return ((StaticPagedInventoryContentProvider) provider).addItem(item);
    }

    @Override
    public @NotNull PagedInventoryContentProvider provider() {
        return provider;
    }

    @Override
    public void provider(@NotNull PagedInventoryContentProvider provider) {
        if (staticUsed) throw new IllegalStateException("Custom content providers can't be used with static items");
        staticUsable = false;
        this.provider = provider;
    }

    public void updater(Updater updater) {
        this.updater = updater;
    }

    @Override
    public void publishUpdate(int index) {
        if (updater != null) updater.update(index);
    }

    @Override
    public void publishUpdatePage() {
        if (updater != null) updater.updatePage();
    }

    @Override
    public void publishUpdateAll() {
        if (updater != null) updater.updateAll();
    }

    @Override
    public void publishUpdateRemoveAt(int index) {
        if (updater != null) updater.updateRemoveAt(index);
    }

    @Override
    public void publishUpdateInsertBefore(int index) {
        if (updater != null) updater.updateInsertBefore(index);
    }

    @Override
    public void publishUpdateInsertAfter(int index) {
        if (updater != null) updater.updateInsertAfter(index);
    }

    public interface Updater {
        void update(int index);

        void updatePage();

        void updateAll();

        void updateRemoveAt(int index);

        void updateInsertBefore(int index);

        void updateInsertAfter(int index);
    }
}
