package eu.darkcube.system.impl.server.inventory.container;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerView;

public class SimpleContainerView implements ContainerView {
    private final @NotNull TemplateInventory inventory;
    private final @NotNull Container container;
    private final int priority;
    private final int @NotNull [] slots;

    public SimpleContainerView(@NotNull TemplateInventory inventory, @NotNull Container container, int priority) {
        this.inventory = inventory;
        this.container = container;
        this.slots = new int[container.size()];
        this.priority = priority;
        for (var i = 0; i < this.slots.length; i++) {
            this.slots[i] = i;
        }
    }

    @Override
    public @NotNull TemplateInventory inventory() {
        return inventory;
    }

    @Override
    public @NotNull Container container() {
        return container;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public void slots(int @NotNull ... slots) {
        if (slots.length != this.slots.length) throw new IllegalArgumentException("Slots must be exactly " + this.slots.length + " long");
        System.arraycopy(slots, 0, this.slots, 0, this.slots.length);
    }

    @Override
    public int @NotNull [] slots() {
        return this.slots.clone();
    }
}
