package eu.darkcube.system.server.inventory.controller;

import java.util.List;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.item.ItemReference;

public interface PagedInventoryStaticContentController {
    @Api
    ItemReference addItem(@NotNull Object item);

    @Api
    void removeItem(@NotNull ItemReference item);

    @Api
    @NotNull
    @Unmodifiable
    List<? extends ItemReference> items();
}
