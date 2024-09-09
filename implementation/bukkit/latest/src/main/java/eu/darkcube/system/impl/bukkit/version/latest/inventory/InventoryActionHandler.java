/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.inventory;

import static eu.darkcube.system.impl.server.inventory.InventoryAPIUtils.LOGGER;
import static eu.darkcube.system.server.item.ItemBuilder.item;
import static java.lang.Math.min;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import eu.darkcube.system.impl.bukkit.inventory.BukkitTemplateInventory;
import eu.darkcube.system.impl.server.inventory.InventoryItemHandler;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.container.ContainerView;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class InventoryActionHandler {
    static boolean handleCustomClickTop(BukkitTemplateInventory inventory, InventoryClickEvent event) {
        var itemHandler = inventory.itemHandler;
        var user = inventory.user;
        var rawSlot = event.getRawSlot();
        var view = event.getView();
        var action = event.getAction();
        return switch (action) {
            case PICKUP_ALL, PICKUP_HALF, PICKUP_ONE -> {
                var entry = itemHandler.findContainer(view.convertSlot(rawSlot));
                if (entry == null) yield false;
                var containerView = entry.getKey();
                var containerSlot = entry.getValue();
                var itemToTake = view.getItem(rawSlot);
                if (itemToTake == null) yield false;
                var container = containerView.container();
                var tryTakeAmount = takeAmount(view, rawSlot, itemToTake, action);
                var takeAmount = min(tryTakeAmount, container.getMaxTakeAmount(user, containerSlot, tryTakeAmount));
                var isVanillaAction = tryTakeAmount == takeAmount;
                if (!container.canTakeItem(user, containerSlot, takeAmount)) yield false;
                var containerItem = container.getAt(containerSlot);
                if (containerItem == null) yield false;
                if (!containerItem.build().equals(itemToTake)) yield false;
                var newContainerItem = containerItem.amount() == takeAmount ? null : containerItem.amount(containerItem.amount() - takeAmount);
                if (isVanillaAction) itemHandler.startContainerTransaction(containerView);
                container.setAt(containerSlot, newContainerItem);
                if (isVanillaAction) itemHandler.finishContainerTransaction(containerView);
                yield isVanillaAction;
            }
            case SWAP_WITH_CURSOR -> {
                var entry = itemHandler.findContainer(view.convertSlot(rawSlot));
                if (entry == null) yield false;
                var containerView = entry.getKey();
                var containerSlot = entry.getValue();
                var itemInSlot = view.getItem(rawSlot);
                if (itemInSlot == null) yield false;
                var itemInCursor = view.getCursor();
                var container = containerView.container();
                var tryTakeAmount = itemInSlot.getAmount();
                var takeAmount = min(tryTakeAmount, container.getMaxTakeAmount(user, containerSlot, tryTakeAmount));
                if (tryTakeAmount != takeAmount) yield false;
                var tryPutAmount = itemInCursor.getAmount();
                var putAmount = min(tryPutAmount, container.getMaxPutAmount(user, containerSlot, tryPutAmount));
                if (tryPutAmount != putAmount) yield false;
                if (!container.canChangeItem(user, containerSlot, takeAmount, putAmount)) yield false;
                itemHandler.startContainerTransaction(containerView);
                container.setAt(containerSlot, item(itemInCursor));
                itemHandler.finishContainerTransaction(containerView);
                yield true;
            }
            case PLACE_ALL, PLACE_ONE, PLACE_SOME -> {
                var entry = itemHandler.findContainer(view.convertSlot(rawSlot));
                if (entry == null) yield false;
                var containerView = entry.getKey();
                var containerSlot = entry.getValue();
                var itemInSlot = view.getItem(rawSlot);
                var itemToPlace = view.getCursor();
                var container = containerView.container();
                var tryPlaceAmount = placeAmount(view, rawSlot, itemToPlace, itemInSlot, action);
                var placeAmount = min(tryPlaceAmount, container.getMaxPutAmount(user, containerSlot, tryPlaceAmount));
                var isVanillaAction = tryPlaceAmount == placeAmount;
                if (!container.canPutItem(user, containerSlot, placeAmount)) yield false;
                var containerItem = container.getAt(containerSlot);
                var newContainerItem = containerItem == null ? item(itemToPlace).amount(0) : containerItem;
                newContainerItem.amount(newContainerItem.amount() + placeAmount);
                if (isVanillaAction) itemHandler.startContainerTransaction(containerView);
                container.setAt(containerSlot, newContainerItem);
                if (isVanillaAction) itemHandler.finishContainerTransaction(containerView);
                yield isVanillaAction;
            }
            case MOVE_TO_OTHER_INVENTORY -> {
                var entry = itemHandler.findContainer(view.convertSlot(rawSlot));
                if (entry == null) yield false;
                var containerView = entry.getKey();
                var containerSlot = entry.getValue();
                var moveResult = calculateQuickMove(user, itemHandler, containerView, containerSlot, view, rawSlot);
                if (moveResult == MoveResult.DENY) yield false;
                var isVanillaAction = moveResult.sameAsVanilla && !moveResult.newItems.isEmpty();
                if (isVanillaAction) itemHandler.startContainerTransaction(containerView);
                containerView.container().setAt(containerSlot, moveResult.newItemInSlot == null ? null : item(moveResult.newItemInSlot));
                if (!isVanillaAction) {
                    for (var e : moveResult.newItems.entrySet()) {
                        view.setItem(e.getKey(), e.getValue());
                    }
                }
                if (isVanillaAction) itemHandler.finishContainerTransaction(containerView);
                yield isVanillaAction;
            }
            case NOTHING -> false;
            default -> {
                LOGGER.info("Unsupported action: {}", action);
                yield false;
            }
        };
    }

    static boolean handleCustomClickBottom(BukkitTemplateInventory inventory, InventoryClickEvent event) {
        return false;
    }

    private record MoveResult(@Nullable ItemStack newItemInSlot, @NotNull Map<Integer, ItemStack> newItems, boolean sameAsVanilla) {
        public static final MoveResult DENY = new MoveResult(null, Map.of(), false);
    }

    private static MoveResult calculateQuickMove(User user, InventoryItemHandler<ItemStack, Player> itemHandler, ContainerView containerView, int containerSlot, InventoryView inventoryView, int rawSlot) {
        var itemInSlot = inventoryView.getItem(rawSlot);
        if (isEmpty(itemInSlot)) {
            LOGGER.warn("Initial item in raw slot {} was empty", rawSlot);
            return MoveResult.DENY;
        }

        var type = inventoryView.getType();
        if (type == InventoryType.CHEST) {
            return calculateQuickMoveChest(user, itemHandler, containerView, containerSlot, inventoryView, rawSlot);
        }
        LOGGER.warn("Unsupported action quick-move items for inventory type {}", type);
        return MoveResult.DENY;
    }

    private static MoveResult calculateQuickMoveChest(User user, InventoryItemHandler<ItemStack, Player> itemHandler, @Nullable ContainerView containerView, int containerSlot, InventoryView inventoryView, int rawSlot) {
        Move move;
        if (containerView == null) {
            move = calculateMoveChest(user, itemHandler, inventoryView, rawSlot);
            if (!move.sameAsVanilla) {
                var itemInSlot = inventoryView.getItem(rawSlot);
                if (!isEmpty(itemInSlot)) {
                    var vanillaMove = moveItems(user, null, -1, inventoryView, itemInSlot, 0, inventoryView.getTopInventory().getSize(), false);
                    if (vanillaMove != Move.DENY) {
                        if (vanillaMove.moves.equals(move.moves)) {
                            move = new Move(move.moves, true);
                        }
                    }
                }
            }
        } else {
            move = calculateMoveChest(user, containerView, containerSlot, inventoryView, rawSlot);
        }
        if (move == Move.DENY) return MoveResult.DENY;
        var itemInSlot = inventoryView.getItem(rawSlot);
        if (isEmpty(itemInSlot)) {
            LOGGER.warn("Item in slot {} for quick-move chest was empty", rawSlot);
            return MoveResult.DENY;
        }
        if (containerView != null) {
            var itemInSlotContainer = containerView.container().getAt(containerSlot);
            if (itemInSlotContainer == null) {
                LOGGER.warn("Item in container slot {} was empty", containerSlot);
                return MoveResult.DENY;
            }
            if (!itemInSlotContainer.build().equals(itemInSlot)) {
                LOGGER.warn("Item mismatch with container: {}, {}", rawSlot, containerSlot);
                return MoveResult.DENY;
            }
        }

        var newItemInSlotAmount = itemInSlot.getAmount();
        var sameAsVanilla = move.sameAsVanilla;
        var newItems = new HashMap<Integer, ItemStack>();
        for (var entry : move.moves.entrySet()) {
            var slot = entry.getKey();
            var addCount = entry.getValue();
            newItemInSlotAmount -= addCount;
            var newItem = inventoryView.getItem(slot);
            if (newItem == null) {
                newItem = itemInSlot.clone();
                newItem.setAmount(addCount);
            } else {
                newItem = newItem.clone();
                newItem.setAmount(newItem.getAmount() + addCount);
            }
            newItems.put(slot, newItem);
        }
        if (newItemInSlotAmount < 0) {
            LOGGER.warn("New item in slot amount {} < 0", newItemInSlotAmount);
            return MoveResult.DENY;
        }
        var newItemInSlot = newItemInSlotAmount == 0 ? null : itemInSlot.clone();
        if (newItemInSlot != null) {
            newItemInSlot.setAmount(newItemInSlotAmount);
        }
        return new MoveResult(newItemInSlot, Map.copyOf(newItems), sameAsVanilla);
    }

    private record Move(Map<Integer, Integer> moves, boolean sameAsVanilla) {
        public static final Move DENY = new Move(Map.of(), false);
    }

    private static Move calculateMoveChest(User user, InventoryItemHandler<ItemStack, Player> itemHandler, InventoryView inventoryView, int rawSlot) {
        var itemInSlot = inventoryView.getItem(rawSlot);
        if (isEmpty(itemInSlot)) {
            LOGGER.warn("Item in raw slot {} was empty", rawSlot);
            return Move.DENY;
        }

        var topSize = inventoryView.getTopInventory().getSize();
        if (rawSlot < topSize) throw new IllegalStateException();
        return moveItemsToTop(user, itemHandler, inventoryView, itemInSlot.clone(), 0, topSize);
    }

    private static Move calculateMoveChest(User user, ContainerView containerView, int containerSlot, InventoryView inventoryView, int rawSlot) {
        var itemInSlot = inventoryView.getItem(rawSlot);
        if (isEmpty(itemInSlot)) {
            LOGGER.warn("Item in raw slot {} was empty", rawSlot);
            return Move.DENY;
        }

        var topSize = inventoryView.getTopInventory().getSize();
        var bottomSize = inventoryView.getBottomInventory().getSize();
        if (rawSlot < topSize) {
            // Click in top inventory - move to bottom
            return moveItems(user, containerView, containerSlot, inventoryView, itemInSlot.clone(), topSize, topSize + bottomSize, true);
        } else {
            throw new IllegalStateException();
        }
    }

    private static Move moveItemsToTop(User user, InventoryItemHandler<?, ?> itemHandler, InventoryView view, ItemStack stack, int startIndex, int endIndex) {
        var moves = new HashMap<Integer, Integer>();
        var sameAsVanilla = true;
        var increment = 1;
        var damageable = (Damageable) stack.getItemMeta();
        var stackAmount = stack.getAmount();
        var targets = new HashMap<Integer, ContainerTarget>();
        var targetsByPriority = new TreeMap<Integer, Map<Integer, ContainerTarget>>(Comparator.reverseOrder());
        for (var slot = startIndex; slot < endIndex; slot += increment) {
            var entry = itemHandler.findContainer(slot);
            if (entry == null) continue;
            var target = new ContainerTarget(entry.getKey(), entry.getValue());
            targets.put(slot, target);
            targetsByPriority.computeIfAbsent(entry.getKey().slotPriority(), _ -> new HashMap<>()).put(slot, target);
        }

        if (isStackable(stack, damageable)) {
            for (var entry : targetsByPriority.entrySet()) {
                var priorityTargets = entry.getValue();
                for (var index = startIndex; index < endIndex; index += increment) {
                    var target = priorityTargets.get(index);
                    if (target == null) continue;
                    var slotItem = getItem(view, moves, index, stack);
                    var containerItem = target.containerView.container().getAt(target.containerSlot);
                    if (checkMismatch(slotItem, containerItem, index)) return Move.DENY;
                    // TODO shift bevorzugt IMMER den container mit höherer priorität.
                    // Beispiel: Linker container (prio 1) , Rechter container (prio 0)
                    // Linker container leer
                    // Rechter container halber stack stein
                    // Shift click auf ganzen stack stein im inventar
                    // Stein landet alles im linken container

                    if (isEmpty(slotItem)) continue;
                }
            }
            for (var index = startIndex; index < endIndex; index += increment) {

                var target = targets.get(index);
                if (target == null) continue;
                var slotItem = getItem(view, moves, index, stack);

                if (!isEmpty(slotItem) && slotItem.isSimilar(stack)) {

                }
            }
        }
        return Move.DENY;
    }

    private static boolean checkMismatch(ItemStack item, ItemBuilder containerItem, int slot) {
        if (isEmpty(item) && isEmpty(containerItem)) return false;
        if (item == null || containerItem == null || !item.equals(containerItem.build())) {
            LOGGER.warn("ItemStack mismatch at slot {}", slot);
            return true;
        }
        return false;
    }

    private record ContainerTarget(ContainerView containerView, int containerSlot) {
    }

    private static Move moveItems(User user, @Nullable ContainerView containerView, int containerSlot, InventoryView view, ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        var moves = new HashMap<Integer, Integer>();
        var sameAsVanilla = true;
        var container = containerView == null ? null : containerView.container();
        var increment = fromLast ? -1 : 1;
        var damageable = (Damageable) stack.getItemMeta();
        var stackAmount = stack.getAmount();

        // First try to fill existing slots
        if (isStackable(stack, damageable)) {
            var index = fromLast ? endIndex - 1 : startIndex;
            while (stackAmount > 0) {
                if (fromLast) {
                    if (index < startIndex) break;
                } else if (index >= endIndex) break;

                var slotItem = getItem(view, moves, index, stack);
                if (!isEmpty(slotItem) && slotItem.isSimilar(stack)) {
                    var tryTakeAmount = stackAmount;
                    var takeAmount = container == null ? tryTakeAmount : min(tryTakeAmount, container.getMaxTakeAmount(user, containerSlot, tryTakeAmount));
                    if (takeAmount <= 0) {
                        LOGGER.warn("TakeAmount {} <= 0 - something went wrong when calculating initial amount", takeAmount);
                        return Move.DENY;
                    }
                    var isVanillaAction = tryTakeAmount == takeAmount;
                    sameAsVanilla = sameAsVanilla && isVanillaAction;
                    var sum = takeAmount + slotItem.getAmount();
                    var maxStack = getMaxStackSize(view, index, slotItem);
                    if (sum <= maxStack) {
                        // Item fully fits into slot
                        if (container != null && !container.canTakeItem(user, containerSlot, takeAmount)) {
                            LOGGER.warn("Container wasn't able to accept item when filling partially filled items, even though it said so at slot {} (1)", containerSlot);
                            return Move.DENY;
                        }
                        stackAmount -= takeAmount;
                        moves.put(index, moves.getOrDefault(index, 0) + takeAmount);
                    } else if (slotItem.getAmount() < maxStack) {
                        tryTakeAmount = maxStack - slotItem.getAmount();
                        takeAmount = container == null ? tryTakeAmount : min(tryTakeAmount, container.getMaxTakeAmount(user, containerSlot, tryTakeAmount));
                        if (takeAmount <= 0) {
                            LOGGER.warn("TakeAmount {} <= 0 - something went wrong when filling partially filled items", takeAmount);
                            return Move.DENY;
                        }
                        if (container != null && !container.canTakeItem(user, containerSlot, takeAmount)) {
                            LOGGER.warn("Container wasn't able to accept item when filling partially filled items, even though it said so at slot {} (2)", containerSlot);
                            return Move.DENY;
                        }
                        stackAmount -= takeAmount;
                        moves.put(index, moves.getOrDefault(index, 0) + takeAmount);
                    }
                }

                index += increment;
            }
        }

        if (stackAmount > 0) {
            var index = fromLast ? endIndex - 1 : startIndex;
            while (true) {
                if (fromLast) {
                    if (index < startIndex) break;
                } else if (index >= endIndex) break;

                var slotItem = getItem(view, moves, index, stack);

                if (isEmpty(slotItem) && canPlace(index, slotItem)) {
                    var maxStack = getMaxStackSize(view, index, stack);
                    var tryTakeAmount = min(stackAmount, maxStack);
                    var takeAmount = container == null ? tryTakeAmount : min(tryTakeAmount, container.getMaxTakeAmount(user, containerSlot, tryTakeAmount));
                    var isVanillaAction = takeAmount == tryTakeAmount;
                    sameAsVanilla = sameAsVanilla && isVanillaAction;
                    if (takeAmount <= 0) {
                        LOGGER.warn("TakeAmount {} <= 0 - something went wrong when filling empty slots", takeAmount);
                        return Move.DENY;
                    }
                    if (container != null && !container.canTakeItem(user, containerSlot, takeAmount)) {
                        LOGGER.warn("Container wasn't able to accept item when filling empty slots, even though it said so at slot {}", containerSlot);
                        return Move.DENY;
                    }
                    stackAmount -= takeAmount;
                    moves.put(index, moves.getOrDefault(index, 0) + takeAmount);
                    if (stackAmount <= 0) break;
                }

                index += increment;
            }
        }
        if (stackAmount < 0) {
            LOGGER.warn("StackAmount {} < 0 - something went wrong", stackAmount);
            return Move.DENY; // Something went wrong
        }
        return new Move(moves, sameAsVanilla);
    }

    private static ItemStack getItem(InventoryView view, Map<Integer, Integer> moves, int slot, ItemStack moveItem) {
        var item = view.getItem(slot);
        if (item == null) {
            if (!moves.containsKey(slot)) return null;
            var count = moves.get(slot);
            item = moveItem.clone();
            item.setAmount(count);
            return item;
        }
        item = item.clone();
        item.setAmount(item.getAmount() + moves.getOrDefault(slot, 0));
        return item;
    }

    // private static boolean moveItemTo(InventoryView view, ItemStack itemStack, int startIndex, int endIndex, boolean fromLast) {
    //     var k = fromLast ? endIndex - 1 : startIndex;
    //     var itemMeta = itemStack.getItemMeta();
    //     var damageable = (Damageable) itemMeta;
    //     var flag1 = false;
    //
    //     if (isStackable(itemStack, damageable)) {
    //         while (!itemStack.isEmpty()) {
    //             if (fromLast) {
    //                 if (k < startIndex) {
    //                     break;
    //                 }
    //             } else if (k >= endIndex) {
    //                 break;
    //             }
    //
    //             var slotItem = view.getItem(k);
    //             if (!isEmpty(slotItem) && itemStack.isSimilar(slotItem)) {
    //                 var l = slotItem.getAmount() + itemStack.getAmount();
    //                 var i1 = getMaxStackSize(view, k, slotItem);
    //
    //                 if (l <= i1) {
    //                     itemStack.setAmount(0);
    //                     slotItem.setAmount(l);
    //                     flag1 = true;
    //                 } else if (slotItem.getAmount() < i1) {
    //                     itemStack.setAmount(itemStack.getAmount() - slotItem.getAmount());
    //                     slotItem.setAmount(i1);
    //                     flag1 = true;
    //                 }
    //             }
    //
    //             if (fromLast) {
    //                 k--;
    //             } else {
    //                 k++;
    //             }
    //         }
    //     }
    //
    //     if (!itemStack.isEmpty()) {
    //         if (fromLast) {
    //             k = endIndex - 1;
    //         } else {
    //             k = startIndex;
    //         }
    //
    //         while (true) {
    //             if (fromLast) {
    //                 if (k < startIndex) {
    //                     break;
    //                 }
    //             } else if (k >= endIndex) {
    //                 break;
    //             }
    //
    //             var slotItem = view.getItem(k);
    //
    //             if (isEmpty(slotItem) && canPlace(k, itemStack)) {
    //                 var l = getMaxStackSize(view, k, itemStack);
    //                 var splitResult = split(itemStack, l);
    //                 view.setItem(k, splitResult);
    //                 flag1 = true;
    //                 break;
    //             }
    //
    //             if (fromLast) {
    //                 k--;
    //             } else {
    //                 k++;
    //             }
    //         }
    //     }
    //     return flag1;
    // }

    /**
     * Checks if the item can be put into the slot. Some slots are restricted to specific items. TODO
     */
    private static boolean canPlace(int slot, ItemStack item) {
        return true;
    }

    private static ItemStack split(ItemStack item, int amount) {
        var j = min(amount, item.getAmount());
        var copy = item.clone();
        copy.setAmount(j);
        item.setAmount(item.getAmount() - j);
        return copy;
    }

    private static int getMaxStackSize(InventoryView view, int rawSlot, ItemStack item) {
        var inv = Objects.requireNonNull(view.getInventory(rawSlot));
        return min(inv.getMaxStackSize(), item.getMaxStackSize());
    }

    private static boolean isEmpty(ItemBuilder item) {
        return item == null || isEmpty(item.<ItemStack>build());
    }

    private static boolean isEmpty(ItemStack item) {
        return item == null || item.isEmpty();
    }

    private static boolean isStackable(ItemStack itemStack, Damageable damageable) {
        return itemStack.getMaxStackSize() > 1 && (!isDamageable(damageable) || !isDamaged(damageable));
    }

    private static boolean isDamageable(Damageable damageable) {
        return damageable.hasMaxDamage() && !damageable.isUnbreakable() && damageable.hasDamageValue();
    }

    private static boolean isDamaged(Damageable damageable) {
        return isDamageable(damageable) && getDamageValue(damageable) > 0;
    }

    private static int getDamageValue(Damageable damageable) {
        return Math.clamp(damageable.getDamage(), 0, damageable.getMaxDamage());
    }

    private static int placeAmount(InventoryView view, int rawSlot, ItemStack itemToPlace, ItemStack itemInSlot, InventoryAction action) {
        var placeAmount = switch (action) {
            case PLACE_ALL, PLACE_SOME -> itemToPlace.getAmount();
            case PLACE_ONE -> 1;
            default -> throw new IllegalStateException("Unexpected value: " + action);
        };
        var maxStackSize = getMaxStackSize(view, rawSlot, itemToPlace);
        var currentSize = itemInSlot == null || itemInSlot.isEmpty() ? 0 : itemInSlot.getAmount();
        return min(placeAmount, Math.max(0, maxStackSize - currentSize));
    }

    private static int takeAmount(InventoryView view, int rawSlot, ItemStack itemToTake, InventoryAction action) {
        var takeAmount = switch (action) {
            case PICKUP_ALL -> itemToTake.getAmount();
            case PICKUP_HALF -> (itemToTake.getAmount() + 1) / 2;
            case PICKUP_ONE -> 1;
            default -> throw new IllegalStateException("Unexpected value: " + action);
        };
        var cursor = view.getCursor();
        var maxStackSize = getMaxStackSize(view, rawSlot, itemToTake);
        return min(maxStackSize - cursor.getAmount(), takeAmount);
    }
}
