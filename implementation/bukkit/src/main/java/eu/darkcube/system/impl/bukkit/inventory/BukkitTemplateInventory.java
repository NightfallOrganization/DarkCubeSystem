package eu.darkcube.system.impl.bukkit.inventory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.server.inventory.InventoryItemHandler;
import eu.darkcube.system.impl.server.inventory.TemplateInventoryImpl;
import eu.darkcube.system.impl.server.inventory.controller.PagedInventoryControllerImpl;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.controller.PagedInventoryController;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitTemplateInventory extends BukkitInventory implements TemplateInventoryImpl<ItemStack> {
    private final @Nullable Player player;
    private final @NotNull InventoryItemHandler<ItemStack, Player> itemHandler;
    private final @NotNull AtomicInteger animationsStarted = new AtomicInteger();
    private final @NotNull Instant openInstant;
    private final @NotNull PagedInventoryControllerImpl pagedController;

    public BukkitTemplateInventory(@NotNull Component title, @NotNull BukkitInventoryType type, @NotNull BukkitInventoryTemplate template, @Nullable Player player) {
        super(title, type);
        this.player = player;
        for (var listener : template.listeners()) {
            this.addListener(listener);
        }
        this.itemHandler = InventoryItemHandler.simple(this, template);
        this.openInstant = Instant.now();
        this.pagedController = new PagedInventoryControllerImpl(this.itemHandler);
    }

    @Override
    protected void doOpen(@NotNull Player player) {
        if (player != this.player) {
            // Can't open the inventory for someone else than the original player
            return;
        }
        var user = UserAPI.instance().user(player.getUniqueId());
        this.itemHandler.doOpen(player, user);
        for (var i = 0; i < listeners.size(); i++) {
            listeners.get(i).onPreOpen(this, user);
        }
        opened.add(user);
        onMainThread(() -> {
            player.openInventory(inventory);
            for (var i = 0; i < listeners.size(); i++) {
                listeners.get(i).onOpen(this, user);
            }
        });
    }

    @Override
    protected void unregister() {
        super.unregister();
        if (this.player == null) {
            // What the heck?
            return;
        }
        var user = UserAPI.instance().user(player.getUniqueId());
        this.itemHandler.doClose(player, user);
    }

    @Override
    protected void handleClick(int slot, @NotNull ItemStack itemStack, @NotNull ItemBuilder item) {
        this.itemHandler.handleClick(slot, itemStack, item);
    }

    @Override
    public @NotNull Instant openInstant() {
        return this.openInstant;
    }

    @Override
    public void scheduleSetItem(int slot, @NotNull Duration duration, @NotNull ItemStack item) {
        var millis = duration.toMillis();
        if (millis == 0) {
            setItem(slot, item);
        } else {
            animationsStarted.incrementAndGet();
            Bukkit.getScheduler().runTaskLater(DarkCubeSystemBukkit.systemPlugin(), () -> {
                setItem(slot, item);
                if (animationsStarted.decrementAndGet() == 0) {
                    for (var i = 0; i < listeners.size(); i++) {
                        listeners.get(i).onOpenAnimationFinished(this);
                    }
                }
            }, millis / 50L);
        }
    }

    @Override
    public @Nullable ItemStack computeItem(@Nullable User user, @Nullable Object item) {
        return BukkitInventoryUtils.computeItem(user, item);
    }

    @Override
    public void onMainThread(@NotNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(DarkCubeSystemBukkit.systemPlugin(), runnable);
        }
    }

    @Override
    public void setAir(int slot) {
        setItem(slot, new ItemStack(Material.AIR));
    }

    @Override
    public PagedInventoryController pagedController() {
        return pagedController;
    }

    @Override
    public void updateSlotsAtPriority(int priority, int... slots) {
        this.itemHandler.updateSlots(priority, slots);
    }

    @Override
    public void updateSlots(int... slots) {
        this.itemHandler.updateSlots(slots);
    }
}
