package eu.darkcube.system.server.inventory.listener;

import java.util.function.Supplier;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface InventoryListenerProvider {
    @NotNull
    InventoryListener ofStateful(@NotNull Supplier<@NotNull InventoryListener> supplier);

    @NotNull
    TemplateInventoryListener ofStatefulTemplate(@NotNull Supplier<@NotNull TemplateInventoryListener> supplier);
}
