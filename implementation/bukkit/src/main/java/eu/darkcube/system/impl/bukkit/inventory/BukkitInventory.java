package eu.darkcube.system.impl.bukkit.inventory;

import static eu.darkcube.system.impl.server.inventory.InventoryAPIUtils.LOGGER;

import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.server.inventory.AbstractInventory;
import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class BukkitInventory extends AbstractInventory<ItemStack> {
    protected final AtomicInteger openCount = new AtomicInteger(0);
    protected final Inventory inventory;
    private final Holder holder = new Holder();
    private final InventoryListener listener = new InventoryListener();
    private volatile boolean modified = false;
    private volatile BukkitTask updateScheduler;

    public BukkitInventory(@Nullable Component title, @NotNull BukkitInventoryType type) {
        super(title, type, type.size());
        this.inventory = createInventory(type, title);
    }

    private Inventory createInventory(BukkitInventoryType type, Component title) {
        if (type instanceof ChestInventoryType(var size)) {
            return Bukkit.createInventory(holder, size, KyoriAdventureSupport.adventureSupport().convert(title));
        }
        return Bukkit.createInventory(holder, type.bukkitType(), KyoriAdventureSupport.adventureSupport().convert(title));
    }

    @Override
    protected void setItem0(int slot, @NotNull ItemStack item) {
        if (!Bukkit.isPrimaryThread()) {
            LOGGER.error("Access to inventory from outside primary thread: {}", Thread.currentThread().getName());
            LOGGER.error("Dump:", new Exception());
        }
        inventory.setItem(slot, item);
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onSlotUpdate(this, slot);
        }
        modified = true;
    }

    @Override
    protected ItemStack getItem0(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public final void open(@Nullable Object player) {
        var bukkitPlayer = BukkitInventoryUtils.player(player);
        if (bukkitPlayer == null) {
            // player not online, do not open
            return;
        }
        if (openCount.getAndIncrement() == 0) {
            register();
        }
        doOpen(bukkitPlayer);
    }

    protected void register() {
        Bukkit.getPluginManager().registerEvents(this.listener, DarkCubeSystemBukkit.systemPlugin());
        updateScheduler = Bukkit.getScheduler().runTaskTimer(DarkCubeSystemBukkit.systemPlugin(), () -> {
            if (modified) {
                modified = false;
                for (var i = 0; i < listeners.size(); i++) {
                    listeners.get(i).onUpdate(this);
                }
            }
        }, 0, 1);
    }

    protected void unregister() {
        HandlerList.unregisterAll(this.listener);
        updateScheduler.cancel();
    }

    protected void doOpen(@NotNull Player player) {
        var user = UserAPI.instance().user(player.getUniqueId());
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onPreOpen(this, user);
        }
        opened.add(user);
        player.openInventory(inventory);
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onOpen(this, user);
        }
    }

    @Override
    public final boolean opened(@NotNull Object player) {
        while (true) {
            if (player instanceof Player bukkitPlayer) {
                player = UserAPI.instance().user(bukkitPlayer.getUniqueId());
            } else {
                break;
            }
        }
        var user = (User) player;
        return opened.contains(user);
    }

    private void handleClick(InventoryClickEvent event) {
        var bukkitInventory = event.getClickedInventory();
        if (bukkitInventory == null) return;
        var cancel = false;

        if (bukkitInventory.getHolder() instanceof Holder holder) {
            if (holder == this.holder) {
                // They clicked into this inventory
                cancel = true;
                handleClick1(event);
            } else {
                return;
            }
        } else {
            // They might still try to shift items into this inventory
            // or sth else :)
            var topInventory = event.getView().getTopInventory();
            if (topInventory != this.inventory) {
                // our inventory isn't open
                return;
            }

            var clickType = event.getClick();
            switch (clickType) {
                case LEFT, CREATIVE, CONTROL_DROP, DROP, RIGHT, WINDOW_BORDER_LEFT, WINDOW_BORDER_RIGHT, NUMBER_KEY, MIDDLE -> {
                }
                case SHIFT_LEFT, SHIFT_RIGHT, DOUBLE_CLICK, SWAP_OFFHAND, UNKNOWN -> cancel = true;
            }
        }
        if (cancel) {
            event.setCancelled(true);
        }
    }

    private void handleClick1(InventoryClickEvent event) {
        event.setCancelled(true);
        var user = UserAPI.instance().user(event.getWhoClicked().getUniqueId());
        var clickType = event.getClick();
        var slot = switch (clickType) {
            case LEFT, SHIFT_LEFT, RIGHT, SHIFT_RIGHT, DROP, CONTROL_DROP, NUMBER_KEY -> event.getSlot();
            case WINDOW_BORDER_LEFT, WINDOW_BORDER_RIGHT -> -1;
            case MIDDLE -> -1; // Middle mouse ignored
            case DOUBLE_CLICK -> 0; // TODO test this
            default -> {
                LOGGER.error("Click not supported by InventoryAPI: {}", clickType);
                yield -1;
            }
        };
        var itemStack = event.getCurrentItem();
        var item = itemStack == null || itemStack.getType().isAir() ? ItemBuilder.item() : ItemBuilder.item(itemStack);
        handleClick(slot, itemStack == null ? new ItemStack(Material.AIR) : itemStack, item);
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onClick(this, user, slot, item);
        }
    }

    private void handleDrag(InventoryDragEvent event) {
        var view = event.getView();
        for (var slot : event.getRawSlots()) {
            var inventory = view.getInventory(slot);
            if (inventory == this.inventory) {
                event.setCancelled(true);
                break;
            }
        }
    }

    protected void handleClick(int slot, @NotNull ItemStack itemStack, @NotNull ItemBuilder item) {
    }

    private void handleClose(InventoryCloseEvent event) {
        var bukkitInventory = event.getInventory();
        if (!(bukkitInventory.getHolder() instanceof Holder holder)) return;
        if (holder != this.holder) return;
        var user = UserAPI.instance().user(event.getPlayer().getUniqueId());
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onClose(this, user);
        }
        if (openCount.decrementAndGet() == 0) {
            unregister();
        }
    }

    private class Holder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }

    private class InventoryListener implements Listener {
        @EventHandler
        public void handle(InventoryCloseEvent event) {
            handleClose(event);
        }

        @EventHandler
        public void handle(InventoryClickEvent event) {
            handleClick(event);
        }

        @EventHandler
        public void handle(InventoryDragEvent event) {
            handleDrag(event);
        }
    }
}
