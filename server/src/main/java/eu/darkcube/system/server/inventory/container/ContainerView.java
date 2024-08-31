package eu.darkcube.system.server.inventory.container;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.TemplateInventory;

public interface ContainerView {
    @NotNull
    TemplateInventory inventory();

    /**
     * @return the container these settings are for
     */
    @NotNull
    Container container();

    int priority();

    void slots(int @NotNull ... slots);

    int @NotNull [] slots();
}
