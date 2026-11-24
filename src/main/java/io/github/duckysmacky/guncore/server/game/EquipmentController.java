package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.armor.ArmorEntry;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.config.catalog.items.ItemEntry;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.common.game.EquipmentMode;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.Map;

public final class EquipmentController {
    private static final String BASE_KIT_ID = "base";
    private static final int MAX_EXTRA_ITEMS = 3;
    private static final String EQUIPPED_TAG = "guncoreEquipped";

    private EquipmentController() {}

    /** Equipment is frozen while a round is running (allowed when paused or not started) */
    private static boolean isLocked(EntityPlayerMP player) {
        if (!GameManager.instance().isRunning()) return false;

        player.sendMessage(new TextComponentString(
            TextUtils.translateColorCodes("&cCannot change equipment while the round is active")
        ));
        return true;
    }

    private static boolean isWrongMode(EntityPlayerMP player, EquipmentMode required) {
        if (GameManager.instance().getEquipmentMode() == required) return false;

        player.sendMessage(new TextComponentString(
            TextUtils.translateColorCodes("&cThis is not available in the current equipment mode")
        ));
        return true;
    }

    /** Marks a stack as handed out by the equipment menu (CSG kit contents are not marked) */
    private static ItemStack tag(ItemStack stack) {
        if (stack.isEmpty()) return stack;

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) nbt = new NBTTagCompound();
        nbt.setBoolean(EQUIPPED_TAG, true);
        stack.setTagCompound(nbt);
        return stack;
    }

    public static boolean isEquipped(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean(EQUIPPED_TAG);
    }

    // Weapons (both modes)

    public static void equipGun(EntityPlayerMP player, GunEntry gun, boolean secondary) {
        if (isLocked(player)) return;

        giveGun(player, secondary ? EquipmentType.SECONDARY_WEAPON : EquipmentType.MAIN_WEAPON, gun);
        syncInventory(player);
    }

    private static void giveGun(EntityPlayerMP player, EquipmentType type, GunEntry gun) {
        EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);

        GunEntry previous = equipment.get(type);
        if (previous != null) removeGun(player, previous);

        player.inventory.mainInventory.set(type.slot, tag(gun.getGunItemStack()));
        player.inventory.mainInventory.set(type.slot + 9 * 3, tag(gun.getAmmoItemStack())); // above that slot
        equipment.set(type, gun);
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

    // Items (kit-based gadget and build-a-class item slots)

    public static void equipGadget(EntityPlayerMP player, ItemEntry gadget) {
        if (isLocked(player) || isWrongMode(player, EquipmentMode.KITS)) return;

        giveItem(player, EquipmentType.GADGET, gadget);
        syncInventory(player);
    }

    public static void equipBuildItem(EntityPlayerMP player, EquipmentType type, CatalogEntry entry) {
        if (isLocked(player) || isWrongMode(player, EquipmentMode.BUILDER)) return;

        if (entry instanceof ArmorEntry) giveArmor(player, (ArmorEntry) entry);
        else giveItem(player, type, (ItemEntry) entry);

        syncInventory(player);
    }

    private static void giveItem(EntityPlayerMP player, EquipmentType type, ItemEntry item) {
        EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);

        ItemEntry previous = equipment.get(type);
        if (previous != null) removeItem(player, previous);

        player.inventory.mainInventory.set(type.slot, tag(item.getItemStack()));

        int extraSlot = type.slot + 9 * 3;
        List<ItemStack> extraItems = item.getAdditionalItemStacks();
        for (int i = 0; i < extraItems.size() && i < MAX_EXTRA_ITEMS; i++)
            player.inventory.mainInventory.set(extraSlot - 9 * i, tag(extraItems.get(i)));

        equipment.set(type, item);
    }

    private static void removeItem(EntityPlayerMP player, ItemEntry item) {
        InventoryPlayer inventory = player.inventory;
        List<String> itemIds = item.getItemIds();

        for (int i = 0; i < inventory.mainInventory.size(); i++) {
            ResourceLocation registryName = inventory.getStackInSlot(i).getItem().getRegistryName();
            if (registryName != null && itemIds.contains(registryName.toString()))
                inventory.setInventorySlotContents(i, new ItemStack(Items.AIR));
        }
    }

    // Armor (build-a-class)

    private static void giveArmor(EntityPlayerMP player, ArmorEntry armor) {
        NonNullList<ItemStack> armorInventory = player.inventory.armorInventory;
        List<ItemStack> pieces = armor.getArmorStacks();

        for (int i = 0; i < armorInventory.size(); i++)
            armorInventory.set(i, i < pieces.size() ? tag(pieces.get(i)) : ItemStack.EMPTY);

        EquipmentManager.instance().getEquipment(player).set(EquipmentType.ARMOR, armor);
    }

    // Kits

    public static void equipKit(EntityPlayerMP player, KitEntry kit) {
        if (isLocked(player) || isWrongMode(player, EquipmentMode.KITS)) return;

        String command = String.format("csg_kit give %s %s", kit.getKitId(), player.getName());
        CommandExecutor.execute(command);

        EquipmentManager.instance().getEquipment(player).clear(); // kit replaces the whole inventory
        syncInventory(player);
    }

    /** Resets the inventory to the base kit, then gives back everything that was selected before */
    public static void equipBaseKit(EntityPlayerMP player) {
        if (isLocked(player) || isWrongMode(player, EquipmentMode.BUILDER)) return;

        String command = String.format("csg_kit give %s %s", BASE_KIT_ID, player.getName());
        CommandExecutor.execute(command);

        EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);
        for (Map.Entry<EquipmentType, CatalogEntry> selected : new java.util.ArrayList<>(equipment.getAll().entrySet())) {
            EquipmentType type = selected.getKey();
            CatalogEntry entry = selected.getValue();

            if (entry instanceof GunEntry) {
                equipment.getAll().remove(type); // wiped from the inventory, nothing to remove
                giveGun(player, type, (GunEntry) entry);
            } else if (entry instanceof ArmorEntry) {
                giveArmor(player, (ArmorEntry) entry);
            } else {
                equipment.getAll().remove(type);
                giveItem(player, type, (ItemEntry) entry);
            }
        }

        syncInventory(player);
    }

    private static void syncInventory(EntityPlayerMP player) {
        player.inventory.markDirty();
        player.inventoryContainer.detectAndSendChanges();
        player.sendContainerToPlayer(player.inventoryContainer);

        GuncoreMod.LOGGER.info("SYNCING PLAYER INVENTORY");
    }
}
