/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.inventoryapi.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.util.AsyncExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AnimatedInventory extends AbstractInventory {

    protected final AtomicBoolean animationRunning = new AtomicBoolean(false);
    protected final AtomicLong animationStarted = new AtomicLong();
    protected final AtomicReference<AnimationRunnable> animation = new AtomicReference<>();
    protected final List<AnimationInformation> informations = new CopyOnWriteArrayList<>();
    private final BooleanSupplier instant;

    public AnimatedInventory(InventoryType inventoryType, Component title, int size, BooleanSupplier instant) {
        super(inventoryType, title, size);
        this.instant = instant;
    }

    public boolean isInstant() {
        return this.instant.getAsBoolean();
    }

    protected void tick() {
    }

    protected void offerAnimations(final Collection<AnimationInformation> informations) {
        AsyncExecutor.service().submit(() -> {
            try {
                this.asyncOfferAnimations(informations);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    protected void asyncOfferAnimations(final Collection<AnimationInformation> informations) {
    }

    protected void startAnimation() {
        if (this.animation.get() == null && this.animationRunning.compareAndSet(false, true)) {
            this.offerAnimations(this.informations);
            AnimationRunnable runnable = new AnimationRunnable();
            this.animation.set(runnable);
            runnable.runTaskTimer(DarkCubePlugin.systemPlugin(), 0, 1);
            this.animationStarted.set(System.currentTimeMillis());
        }
    }

    public void recalculate() {
        if (this.animationRunning.get()) {
            this.offerAnimations(this.informations);
        }
    }

    private void stopAnimation() {
        if (this.animationRunning.compareAndSet(true, false)) {
            this.animation.get().cancel();
        }
    }

    @Override
    protected void destroy() {
        super.destroy();
        this.stopAnimation();
    }

    public static class AnimationInformation {

        public final long showAfter;
        public final int slot;
        public final ItemStack item;

        public AnimationInformation(long showAfter, int slot, ItemStack item) {
            this.showAfter = showAfter;
            this.slot = slot;
            this.item = item;
        }

        public AnimationInformation(int slot, ItemStack item) {
            this(-1, slot, item);
        }

    }

    protected class AnimationRunnable extends BukkitRunnable {

        @Override
        public void run() {
            boolean updated = false;
            final long started = animationStarted.get();
            final long time = System.currentTimeMillis();
            tick();
            List<AnimationInformation> toRemove = new ArrayList<>();
            for (AnimationInformation information : informations) {
                if (information.showAfter + started > time) {
                    continue;
                }
                toRemove.add(information);
                handle.setItem(information.slot, information.item);
                updated = true;
            }
            informations.removeAll(toRemove);
            if (updated) {
                opened.stream().filter(Player.class::isInstance).map(Player.class::cast).forEach(Player::updateInventory);
            }
        }

    }

}
