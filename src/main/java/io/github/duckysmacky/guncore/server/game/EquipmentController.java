package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class EquipmentController {

    private EquipmentController() {}

    public static void equipGun(ServerPlayer player, GunEntry gun) {
        EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);

        if (gun.isSecondary()) {
            equipment.getSecondaryWeapon().ifPresent(g -> removeGun(player, g));
            giveGun(player, gun, 1);
            equipment.setSecondaryWeapon(gun);
        } else {
            equipment.getMainWeapon().ifPresent(g -> removeGun(player, g));
            giveGun(player, gun, 0);
            equipment.setMainWeapon(gun);
        }

        syncInventory(player);
    }

    private static void removeGun(ServerPlayer player, GunEntry gun) {
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack item = inventory.getItem(i);

            if (ItemStack.isSameItem(item, gun.getGunItemStack()) || ItemStack.isSameItem(item, gun.getAmmoItemStack())) {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    private static void giveGun(ServerPlayer player, GunEntry gun, int hotbarSlot) {
        Inventory inventory = player.getInventory();
        inventory.setItem(hotbarSlot, gun.getGunItemStack());

        int ammoSlot = hotbarSlot + 9 * 3; // one row above that slot
        inventory.setItem(ammoSlot, gun.getAmmoItemStack());
    }

    public static void equipGadget(ServerPlayer player, GadgetEntry gadget) {
        EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);

        equipment.getGadget().ifPresent(g -> removeGadget(player, g));
        giveGadget(player, gadget);
        equipment.setGadget(gadget);

        syncInventory(player);
    }

    private static void removeGadget(ServerPlayer player, GadgetEntry gadget) {
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack item = inventory.getItem(i);

            if (ItemStack.isSameItem(item, gadget.getItemStack()) ||
                gadget.getAdditionalItemStacks().stream().anyMatch(it -> ItemStack.isSameItem(item, it))
            ) {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    private static void giveGadget(ServerPlayer player, GadgetEntry gadget) {
        int mainSlot = 4;
        Inventory inventory = player.getInventory();
        inventory.setItem(mainSlot, gadget.getItemStack());

        int extraSlot = mainSlot + 9 * 3;
        var extraItems = gadget.getAdditionalItemStacks();
        for (int i = 0; i < extraItems.size() && i < 3; i++) {
            inventory.setItem(extraSlot - 9 * i, extraItems.get(i));
        }
    }

    public static void equipKit(ServerPlayer player, KitEntry kit) {
        String command = String.format("csg_kits give %s %s", kit.getKitId(), player.getName().getString());
        CommandExecutor.execute(command);
        syncInventory(player);
    }

    private static void syncInventory(ServerPlayer player) {
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();

        GuncoreMod.LOGGER.info("Syncing player {} inventory", player.getScoreboardName());
    }
}