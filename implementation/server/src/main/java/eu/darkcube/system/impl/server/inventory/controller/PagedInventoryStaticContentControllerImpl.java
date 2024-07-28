package eu.darkcube.system.impl.server.inventory.controller;

import java.math.BigInteger;
import java.util.List;

import eu.darkcube.system.impl.server.inventory.SimpleItemHandler;
import eu.darkcube.system.impl.server.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.impl.server.inventory.paged.StaticPagedInventoryContentProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.controller.PagedInventoryStaticContentController;
import eu.darkcube.system.server.inventory.item.ItemReference;

public class PagedInventoryStaticContentControllerImpl implements PagedInventoryStaticContentController {
    private final @NotNull SimpleItemHandler<?, ?> simple;
    private final @NotNull StaticPagedInventoryContentProvider provider;

    public PagedInventoryStaticContentControllerImpl(@NotNull SimpleItemHandler<?, ?> simple, @NotNull StaticPagedInventoryContentProvider provider) {
        this.simple = simple;
        this.provider = provider;
    }

    @Override
    public ItemReferenceImpl addItem(@NotNull Object item) {
        var lastIndex = this.provider.size().subtract(BigInteger.ONE);
        var reference = this.provider.addItem(item);
        this.simple.paginationCalculator().pagination().content().publishUpdateInsertAfter(lastIndex);
        return reference;
    }

    @Override
    public void removeItem(@NotNull ItemReference item) {
        this.provider.removeItem((ItemReferenceImpl) item);
    }

    @Override
    public @NotNull @Unmodifiable List<? extends ItemReference> items() {
        return List.copyOf(this.provider.items());
    }
}
