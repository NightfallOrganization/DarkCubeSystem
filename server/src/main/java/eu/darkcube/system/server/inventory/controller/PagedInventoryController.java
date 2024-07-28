package eu.darkcube.system.server.inventory.controller;

import java.math.BigInteger;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * The controller for a specific paged inventory. The inventory is visible to a player.
 */
public interface PagedInventoryController {
    @Api
    @NotNull
    PagedInventoryStaticContentController staticContent();

    @Api
    @NotNull
    BigInteger currentPage();

    @Api
    void currentPage(@NotNull BigInteger page);

    @Api
    void previousPage();

    @Api
    void nextPage();

    @Api
    void publishUpdateAll();

    @Api
    void publishUpdatePage();

    @Api
    void publishUpdate(@NotNull BigInteger index);

    @Api
    void publishUpdateRemoveAt(@NotNull BigInteger index);

    @Api
    void publishUpdateInsertBefore(@NotNull BigInteger index);

    @Api
    void publishUpdateInsertAfter(@NotNull BigInteger index);
}
