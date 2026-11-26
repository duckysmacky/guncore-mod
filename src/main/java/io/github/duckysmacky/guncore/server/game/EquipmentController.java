package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.Settings;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.EquippableEntry;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.config.catalog.armor.ArmorEntry;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.config.catalog.items.BaseItemEntry;
import io.github.duckysmacky.guncore.common.config.catalog.items.ItemEntry;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.common.game.EquipmentMode;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.game.LoadoutSystem;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class EquipmentController {
    /** Slots of the base items, bottom to top above the last hotbar slot */
    private static final int[] BASE_ITEM_SLOTS = {8, 17, 26, 35};
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

    /**
     * Rejects picks the rarity rules don't allow
     *
     * @param slot slot the pick replaces, null for a kit (replaces the previous kit)
     */
    private static boolean isDenied(EntityPlayerMP player, EquipmentType slot, Rarity rarity) {
        GameManager game = GameManager.instance();

        if (rarity == Rarity.MYTHIC && !game.isMythicsEnabled()) {
            send(player, "&cMythic items are disabled");
            return true;
        }

        if (game.getLoadoutSystem() != LoadoutSystem.RARITY_SCORE) return false;

        Settings.RarityPreset preset = game.getRarityPreset();
        EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);

        Rarity replaced;
        if (slot != null) {
            EquippableEntry entry = equipment.get(slot);
            replaced = entry != null ? entry.getRarity() : null;
        } else {
            replaced = equipment.getKit() != null ? equipment.getKit().getRarity() : null;
        }
        int rest = equipment.rarityScore(preset::scoreOf) - (replaced != null ? preset.scoreOf(replaced) : 0);

        int total = rest + preset.scoreOf(rarity);
        int max = preset.maxFor(game.getEquipmentMode());
        if (total <= max) return false;

        send(player, String.format("&cOver the rarity score limit: %d/%d", total, max));
        return true;
    }

    private static void reportRarityScore(EntityPlayerMP player) {
        GameManager game = GameManager.instance();
        if (game.getLoadoutSystem() != LoadoutSystem.RARITY_SCORE) return;

        Settings.RarityPreset preset = game.getRarityPreset();
        int score = EquipmentManager.instance().getEquipment(player).rarityScore(preset::scoreOf);
        send(player, String.format("&7Loadout rarity score: &f%d&7/&f%d", score, preset.maxFor(game.getEquipmentMode())));
    }

    private static void send(EntityPlayerMP player, String message) {
        player.sendMessage(new TextComponentString(TextUtils.translateColorCodes(message)));
    }

    // Weapons (both modes)

    public static void equipGun(EntityPlayerMP player, GunEntry gun, boolean secondary) {
        EquipmentType type = secondary ? EquipmentType.SECONDARY_WEAPON : EquipmentType.MAIN_WEAPON;
        if (isLocked(player) || isDenied(player, type, gun.getRarity())) return;

        giveGun(player, type, gun);
        syncInventory(player);
        reportRarityScore(player);
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
        if (isLocked(player) || isWrongMode(player, EquipmentMode.KITS) || isDenied(player, EquipmentType.GADGET, gadget.getRarity())) return;

        giveItem(player, EquipmentType.GADGET, gadget);
        syncInventory(player);
        reportRarityScore(player);
    }

    public static void equipBuildItem(EntityPlayerMP player, EquipmentType type, CatalogEntry entry) {
        if (isLocked(player) || isWrongMode(player, EquipmentMode.BUILDER)) return;
        if (isDenied(player, type, ((EquippableEntry) entry).getRarity())) return;

        if (entry instanceof ArmorEntry) giveArmor(player, (ArmorEntry) entry);
        else giveItem(player, type, (ItemEntry) entry);

        syncInventory(player);
        reportRarityScore(player);
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
        if (isLocked(player) || isWrongMode(player, EquipmentMode.KITS) || isDenied(player, null, kit.getRarity())) return;

        String command = String.format("csg_kit give %s %s", kit.getKitId(), player.getName());
        CommandExecutor.execute(command);

        // the kit replaces the whole inventory, the selected weapons and gadget are given back on top
        EquipmentManager.instance().getEquipment(player).setKit(kit);
        giveSelection(player);
        syncInventory(player);
        reportRarityScore(player);
    }

    /** Gives the base items into their slots, the rest of the inventory is left as is */
    public static void giveBaseItems(EntityPlayerMP player) {
        if (isLocked(player) || isWrongMode(player, EquipmentMode.BUILDER)) return;

        List<BaseItemEntry> baseItems = ConfigManager.instance().getCatalogManager().getCatalog(CatalogType.BASE_ITEMS);
        for (int i = 0; i < baseItems.size() && i < BASE_ITEM_SLOTS.length; i++)
            player.inventory.mainInventory.set(BASE_ITEM_SLOTS[i], tag(baseItems.get(i).getItemStack()));

        syncInventory(player);
    }

    // Deselecting

    /** Removes the entry selected for {@code type} from the inventory and the selection */
    public static void deselect(EntityPlayerMP player, EquipmentType type) {
        if (isLocked(player)) return;

        EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);
        CatalogEntry entry = equipment.get(type);
        if (entry == null) {
            send(player, "&7Nothing selected as " + type.display.toLowerCase());
            return;
        }

        removeEntry(player, entry);
        equipment.getAll().remove(type);
        syncInventory(player);
        send(player, "&7Deselected " + type.display.toLowerCase() + ": &f" + entry.getName());
        reportRarityScore(player);
    }

    /** Clears the inventory (the kit's items are not tracked) and gives back the selected weapons and gadget */
    public static void deselectKit(EntityPlayerMP player) {
        if (isLocked(player) || isWrongMode(player, EquipmentMode.KITS)) return;

        EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);
        if (equipment.getKit() == null) {
            send(player, "&7No kit selected");
            return;
        }

        player.inventory.clear();
        equipment.setKit(null);
        giveSelection(player);
        syncInventory(player);
        send(player, "&7Kit deselected");
        reportRarityScore(player);
    }

    // Whole selection (build-a-class)

    /** Gives back everything that is selected, removing the old copies from the inventory first */
    public static void restoreSelection(EntityPlayerMP player) {
        if (isLocked(player) || isWrongMode(player, EquipmentMode.BUILDER)) return;

        removeSelection(player);
        giveSelection(player);
        syncInventory(player);
    }

    /** Removes everything that is selected from the inventory and forgets the selection */
    public static void resetSelection(EntityPlayerMP player) {
        if (isLocked(player) || isWrongMode(player, EquipmentMode.BUILDER)) return;

        removeSelection(player);
        EquipmentManager.instance().getEquipment(player).clear();
        syncInventory(player);
        send(player, "&7Selection reset");
        reportRarityScore(player);
    }

    /** Gives every selected entry without removing anything from the inventory */
    private static void giveSelection(EntityPlayerMP player) {
        EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);
        for (Map.Entry<EquipmentType, CatalogEntry> selected : new ArrayList<>(equipment.getAll().entrySet())) {
            EquipmentType type = selected.getKey();
            CatalogEntry entry = selected.getValue();
            equipment.getAll().remove(type); // so the give methods don't remove the previous copy

            if (entry instanceof GunEntry) giveGun(player, type, (GunEntry) entry);
            else if (entry instanceof ArmorEntry) giveArmor(player, (ArmorEntry) entry);
            else giveItem(player, type, (ItemEntry) entry);
        }
    }

    private static void removeSelection(EntityPlayerMP player) {
        EquipmentManager.instance().getEquipment(player).getAll().values().forEach(entry -> removeEntry(player, entry));
    }

    private static void removeEntry(EntityPlayerMP player, CatalogEntry entry) {
        if (entry instanceof GunEntry) removeGun(player, (GunEntry) entry);
        else if (entry instanceof ArmorEntry) player.inventory.armorInventory.clear();
        else removeItem(player, (ItemEntry) entry);
    }

    private static void syncInventory(EntityPlayerMP player) {
        player.inventory.markDirty();
        player.inventoryContainer.detectAndSendChanges();
        player.sendContainerToPlayer(player.inventoryContainer);

        GuncoreMod.LOGGER.info("SYNCING PLAYER INVENTORY");
    }
}
