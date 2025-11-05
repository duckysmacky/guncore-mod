package io.github.duckysmacky.guncore.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.data.config.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.guncore.data.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.data.config.catalog.kits.KitEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public final class EquipmentController {

    private EquipmentController() {}

    public static void equipGun(EntityPlayerMP player, GunEntry gun) {
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

    private static void removeGun(EntityPlayerMP player, GunEntry gun) {
        InventoryPlayer inventory = player.inventory;

        for (int i = 0; i < inventory.mainInventory.size(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            ResourceLocation registryName = item.getItem().getRegistryName();
            if (registryName == null) continue;

            String itemId = registryName.toString();
            if (itemId.equals(gun.getGunItemId()) || itemId.equals(gun.getAmmoItemId()))
                inventory.setInventorySlotContents(i, new ItemStack(Items.AIR));
        }
    }

    private static void giveGun(EntityPlayerMP player, GunEntry gun, int hotbarSlot) {
        player.inventory.mainInventory.set(hotbarSlot, gun.getGunItemStack());

        int ammoSlot = hotbarSlot + 9 * 3; // above that slot
        player.inventory.mainInventory.set(ammoSlot, gun.getAmmoItemStack());
    }

    public static void equipGadget(EntityPlayerMP player, GadgetEntry gadget) {
        EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);

        equipment.getGadget().ifPresent(g -> removeGadget(player, g));
        giveGadget(player, gadget);
        equipment.setGadget(gadget);

        syncInventory(player);
    }

    private static void removeGadget(EntityPlayerMP player, GadgetEntry gadget) {
        InventoryPlayer inventory = player.inventory;

        for (int i = 0; i < inventory.mainInventory.size(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            ResourceLocation registryName = item.getItem().getRegistryName();
            if (registryName == null) continue;

            String itemId = registryName.toString();
            if (itemId.equals(gadget.getItemId()) || gadget.getAdditionalItemIds().stream().anyMatch(itemId::equals))
                inventory.setInventorySlotContents(i, new ItemStack(Items.AIR));
        }
    }

    private static void giveGadget(EntityPlayerMP player, GadgetEntry gadget) {
        int mainSlot = 4;
        player.inventory.mainInventory.set(mainSlot, gadget.getItemStack());

        int extraSlot = mainSlot + 9 * 3;
        List<ItemStack> extraItems = gadget.getAdditionalItemStacks();
        for (int i = 0; i < extraItems.size() && i < 3; i++)
            player.inventory.mainInventory.set(extraSlot - 9 * i, extraItems.get(i));
    }

    public static void equipKit(EntityPlayerMP player, KitEntry kit) {
        String command = String.format("csg_kit give %s %s", kit.getKitId(), player.getName());
        CommandExecutor.execute(command);

        syncInventory(player);
    }

    private static void syncInventory(EntityPlayerMP player) {
        player.inventory.markDirty();
        player.inventoryContainer.detectAndSendChanges();
        player.sendContainerToPlayer(player.inventoryContainer);

        GuncoreMod.LOGGER.info("SYNCING PLAYER INVENTORY");
    }
}
