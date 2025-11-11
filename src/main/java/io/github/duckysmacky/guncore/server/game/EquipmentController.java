package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.catalog.entries.ArmorEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.EquippableEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class EquipmentController {

    private EquipmentController() {}

    public static void equip(ServerPlayer player, EquipmentType type, EquippableEntry equipment) {
        var playerEquipment = EquipmentManager.instance().getEquipment(player);

        EquippableEntry previous = playerEquipment.getEquipment(type);
        if (previous != null)
            removeEquipment(player, type, previous);

        giveEquipment(player, type, equipment);
        playerEquipment.setEquipment(type, equipment);

        syncInventory(player);
    }

    private static void removeEquipment(ServerPlayer player, EquipmentType type, EquippableEntry equipment) {
        Inventory inventory = player.getInventory();

        if (type != EquipmentType.ARMOR) {
            for (int i = 0; i < inventory.items.size(); i++) {
                ItemStack item = inventory.getItem(i);

                if (ItemStack.matches(item, equipment.getItemStack()) ||
                    equipment.getAdditionalItemStacks().stream().anyMatch(it -> ItemStack.matches(item, it))
                ) {
                    inventory.setItem(i, ItemStack.EMPTY);
                }
            }
        } else if (equipment instanceof ArmorEntry) {
            inventory.armor.set(EquipmentType.ArmorType.BOOTS.slot, ItemStack.EMPTY);
            inventory.armor.set(EquipmentType.ArmorType.LEGGINGS.slot, ItemStack.EMPTY);
            inventory.armor.set(EquipmentType.ArmorType.CHESTPLATE.slot, ItemStack.EMPTY);
            inventory.armor.set(EquipmentType.ArmorType.HELMET.slot, ItemStack.EMPTY);
        }

        syncInventory(player);
    }

    private static void giveEquipment(ServerPlayer player, EquipmentType type, EquippableEntry equipment) {
        Inventory inventory = player.getInventory();

        if (type != EquipmentType.ARMOR) {
            inventory.setItem(type.slot, equipment.getItemStack());

            int extraSlot = type.slot + 9 * 3;
            var extraItems = equipment.getAdditionalItemStacks();
            for (int i = 0; i < extraItems.size() && i < 3; i++) {
                inventory.setItem(extraSlot, extraItems.get(i));
                extraSlot -= 9;
            }
        } else if (equipment instanceof ArmorEntry armor) {
            inventory.armor.set(EquipmentType.ArmorType.BOOTS.slot, armor.getBootsItemStack());
            inventory.armor.set(EquipmentType.ArmorType.LEGGINGS.slot, armor.getLeggingsItemStack());
            inventory.armor.set(EquipmentType.ArmorType.CHESTPLATE.slot, armor.getChestplateItemStack());
            inventory.armor.set(EquipmentType.ArmorType.HELMET.slot, armor.getHelmetItemStack());
        }

        syncInventory(player);
    }

    private static void syncInventory(ServerPlayer player) {
        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();

        GuncoreMod.LOGGER.info("Synced player '{}' inventory", player.getScoreboardName());
    }
}