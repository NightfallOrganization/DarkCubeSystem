package eu.darkcube.system.impl.server.inventory;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.animated.AnimatedTemplateSettings;
import eu.darkcube.system.server.inventory.container.ContainerViewFactory;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.item.ItemTemplate;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.inventory.paged.PagedTemplateSettings;
import eu.darkcube.system.userapi.User;

public class LazyInventoryTemplate implements InventoryTemplate {
    private final @NotNull Supplier<@NotNull InventoryTemplate> supplier;
    private InventoryTemplate handle;

    public LazyInventoryTemplate(@NotNull Supplier<@NotNull InventoryTemplate> supplier) {
        this.supplier = supplier;
    }

    @Api
    @NotNull
    public static InventoryTemplate lazy(@NotNull Supplier<@NotNull InventoryTemplate> supplier) {
        return InventoryTemplate.lazy(supplier);
    }

    public InventoryTemplate handle() {
        if (handle != null) return handle;
        synchronized (this) {
            if (handle != null) return handle;
            handle = supplier.get();
            return handle;
        }
    }

    @Override
    @NotNull
    public InventoryType type() {
        return handle().type();
    }

    @Override
    public int size() {
        return handle().size();
    }

    @Override
    public void addContainerFactory(@NotNull ContainerViewFactory factory) {
        handle().addContainerFactory(factory);
    }

    @Override
    public void removeContainerFactory(@NotNull ContainerViewFactory factory) {
        handle().removeContainerFactory(factory);
    }

    @Override
    public @NotNull @Unmodifiable List<@NotNull ContainerViewFactory> containerFactories() {
        return handle().containerFactories();
    }

    @Override
    @NotNull
    public AnimatedTemplateSettings animation() {
        return handle().animation();
    }

    @Override
    @NotNull
    public PagedTemplateSettings pagination() {
        return handle().pagination();
    }

    @Override
    public void title(@Nullable Object title) {
        handle().title(title);
    }

    @Override
    public @Unmodifiable @NotNull Collection<InventoryListener> listeners() {
        return handle().listeners();
    }

    @Override
    @Api
    public void addListener(@NotNull InventoryListener listener) {
        handle().addListener(listener);
    }

    @Override
    @Api
    public void removeListener(@NotNull InventoryListener listener) {
        handle().removeListener(listener);
    }

    @Override
    @Api
    public void addListener(@NotNull TemplateInventoryListener listener) {
        handle().addListener(listener);
    }

    @Override
    @Api
    public void removeListener(@NotNull TemplateInventoryListener listener) {
        handle().removeListener(listener);
    }

    @Override
    @Api
    @NotNull
    public ItemReference setItem(int priority, int slot, @NotNull Object item) {
        return handle().setItem(priority, slot, item);
    }

    @Override
    @Api
    @NotNull
    public ItemReference setItem(int priority, int slot, @NotNull Supplier<@NotNull ?> supplier) {
        return handle().setItem(priority, slot, supplier);
    }

    @Override
    @Api
    public ItemReference setItem(int priority, int slot, @NotNull Function<User, ?> itemFunction) {
        return handle().setItem(priority, slot, itemFunction);
    }

    @Override
    @Api
    public void setItems(int priority, @NotNull ItemTemplate template) {
        handle().setItems(priority, template);
    }

    @Override
    @Api
    @NotNull
    public Inventory open(@NotNull Object player) {
        return handle().open(player);
    }

    @Override
    @NotNull
    public Key key() {
        return handle().key();
    }
}
