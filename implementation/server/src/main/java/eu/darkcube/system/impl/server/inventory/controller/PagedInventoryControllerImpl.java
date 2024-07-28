package eu.darkcube.system.impl.server.inventory.controller;

import java.math.BigInteger;

import eu.darkcube.system.impl.server.inventory.InventoryItemHandler;
import eu.darkcube.system.impl.server.inventory.SimpleItemHandler;
import eu.darkcube.system.impl.server.inventory.paged.PaginationCalculator;
import eu.darkcube.system.impl.server.inventory.paged.StaticPagedInventoryContentProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.controller.PagedInventoryController;
import eu.darkcube.system.server.inventory.controller.PagedInventoryStaticContentController;

public class PagedInventoryControllerImpl implements PagedInventoryController {

    private final @Nullable PaginationCalculator<?, ?> paginationCalculator;
    private final @Nullable PagedInventoryStaticContentControllerImpl staticContent;

    public PagedInventoryControllerImpl(@NotNull InventoryItemHandler<?, ?> itemHandler) {
        this.paginationCalculator = itemHandler instanceof SimpleItemHandler<?, ?> simple ? simple.paginationCalculator() : null;
        this.staticContent = itemHandler instanceof SimpleItemHandler<?, ?> simple && paginationCalculator.pagination().content().provider() instanceof StaticPagedInventoryContentProvider provider ? new PagedInventoryStaticContentControllerImpl(simple, provider) : null;
    }

    @Override
    public @NotNull PagedInventoryStaticContentController staticContent() {
        if (this.staticContent == null) throw new IllegalStateException("This inventory does not have static content!");
        return staticContent;
    }

    @Override
    public @NotNull BigInteger currentPage() {
        if (this.paginationCalculator == null) throw new IllegalStateException("This inventory is not paged!");
        return this.paginationCalculator.currentPage();
    }

    @Override
    public void currentPage(@NotNull BigInteger page) {
        if (this.paginationCalculator == null) throw new IllegalStateException("This inventory is not paged!");
        this.paginationCalculator.loadPage(page);
    }

    @Override
    public void previousPage() {
        if (this.paginationCalculator == null) throw new IllegalStateException("This inventory is not paged!");
        this.paginationCalculator.loadPreviousPage();
    }

    @Override
    public void nextPage() {
        if (this.paginationCalculator == null) throw new IllegalStateException("This inventory is not paged!");
        this.paginationCalculator.loadNextPage();
    }

    @Override
    public void publishUpdateAll() {
        if (this.paginationCalculator == null) throw new IllegalStateException("This inventory is not paged!");
        this.paginationCalculator.updater().updateAll();
    }

    @Override
    public void publishUpdatePage() {
        if (this.paginationCalculator == null) throw new IllegalStateException("This inventory is not paged!");
        this.paginationCalculator.updater().updatePage();
    }

    @Override
    public void publishUpdate(@NotNull BigInteger index) {
        if (this.paginationCalculator == null) throw new IllegalStateException("This inventory is not paged!");
        this.paginationCalculator.updater().update(index);
    }

    @Override
    public void publishUpdateRemoveAt(@NotNull BigInteger index) {
        if (this.paginationCalculator == null) throw new IllegalStateException("This inventory is not paged!");
        this.paginationCalculator.updater().updateRemoveAt(index);
    }

    @Override
    public void publishUpdateInsertBefore(@NotNull BigInteger index) {
        if (this.paginationCalculator == null) throw new IllegalStateException("This inventory is not paged!");
        this.paginationCalculator.updater().updateInsertBefore(index);
    }

    @Override
    public void publishUpdateInsertAfter(@NotNull BigInteger index) {
        if (this.paginationCalculator == null) throw new IllegalStateException("This inventory is not paged!");
        this.paginationCalculator.updater().updateInsertAfter(index);
    }
}
