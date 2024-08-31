package eu.darkcube.system.impl.server.inventory.container;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerListener;
import eu.darkcube.system.server.item.ItemBuilder;

public class SimpleContainer implements Container {
    private final @NotNull List<ContainerListener> listeners = new CopyOnWriteArrayList<>();
    private final @Nullable ItemBuilder @NotNull [] items;

    public SimpleContainer(int size) {
        this.items = new ItemBuilder[size];
    }

    @Override
    public @Nullable ItemBuilder getAt(int slot) {
        return items[slot];
    }

    @Override
    public void setAt(int slot, @Nullable ItemBuilder item) {
        var old = items[slot];
        if (item == old) return;
        items[slot] = item;
        if (old == null) {
            for (var i = 0; i < listeners.size(); i++) {
                listeners.get(i).onItemAdded(slot, item);
            }
        } else if (item == null) {
            for (var i = 0; i < listeners.size(); i++) {
                listeners.get(i).onItemRemoved(slot, old);
            }
        } else {
            for (var i = 0; i < listeners.size(); i++) {
                listeners.get(i).onItemChanged(slot, old, item);
            }
        }
    }

    @Override
    public void addListener(@NotNull ContainerListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull ContainerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public int size() {
        return items.length;
    }
}
