/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.paged;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContentProvider;

public class PagedInventoryContentImpl implements PagedInventoryContent {
    private boolean staticUsable = true;
    private boolean staticUsed = false;
    private boolean async = false;
    private final List<Updater> updaters = new CopyOnWriteArrayList<>();
    private PagedInventoryContentProvider provider = new StaticPagedInventoryContentProvider();

    public PagedInventoryContentImpl() {
    }

    private PagedInventoryContentImpl(boolean staticUsable, boolean staticUsed, boolean async, PagedInventoryContentProvider provider, List<Updater> updaters) {
        this.staticUsable = staticUsable;
        this.staticUsed = staticUsed;
        this.async = async;
        this.provider = provider;
        this.updaters.addAll(updaters);
    }

    @Override
    public PagedInventoryContentImpl clone() {
        return new PagedInventoryContentImpl(staticUsable, staticUsed, async, provider, updaters);
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    @Override
    public void makeAsync() {
        async = true;
    }

    @Override
    public void makeSync() {
        async = false;
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

    public void addUpdater(Updater updater) {
        this.updaters.add(updater);
    }

    public void removeUpdater(Updater updater) {
        this.updaters.remove(updater);
    }

    @Override
    public void publishUpdate(BigInteger index) {
        for (var i = 0; i < updaters.size(); i++) {
            updaters.get(i).update(index);
        }
    }

    @Override
    public void publishUpdatePage() {
        for (var i = 0; i < updaters.size(); i++) {
            updaters.get(i).updatePage();
        }
    }

    @Override
    public void publishUpdateAll() {
        for (var i = 0; i < updaters.size(); i++) {
            updaters.get(i).updateAll();
        }
    }

    @Override
    public void publishUpdateRemoveAt(BigInteger index) {
        for (var i = 0; i < updaters.size(); i++) {
            updaters.get(i).updateRemoveAt(index);
        }
    }

    @Override
    public void publishUpdateInsertBefore(BigInteger index) {
        for (var i = 0; i < updaters.size(); i++) {
            updaters.get(i).updateInsertBefore(index);
        }
    }

    @Override
    public void publishUpdateInsertAfter(BigInteger index) {
        for (var i = 0; i < updaters.size(); i++) {
            updaters.get(i).updateInsertAfter(index);
        }
    }

    public interface Updater {
        void update(BigInteger index);

        void updatePage();

        void updateAll();

        void updateRemoveAt(BigInteger index);

        void updateInsertBefore(BigInteger index);

        void updateInsertAfter(BigInteger index);
    }
}
