package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.data.config.ConfigLoader;
import io.github.duckysmacky.projectg.data.config.catalog.guns.GunCategory;
import io.github.duckysmacky.projectg.data.config.catalog.guns.GunEntry;
import io.github.duckysmacky.projectg.game.EquipmentManager;
import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.DynamicMenu;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

public class GunsMenuPage extends DynamicMenu {
    public GunsMenuPage(BaseMenu parent, GunCategory gunCategory) {
        super(gunCategory.display + "s", parent, 6, 9);

        ConfigLoader configLoader = ConfigLoader.instance();

        configLoader.getCachedGuns().stream()
            .filter(gun -> gun.getCategory() == gunCategory)
            .sorted(Comparator.comparingInt(gun -> gun.getRarity().sortOrder))
            .forEach(gun -> {
                ItemStack gunItem = gun.getGunItemStack();

                addEntry(new ActionEntry(gunItem, player -> {
                    player.sendMessage(new TextComponentString("Selected gun: " + gunItem.getDisplayName()));

                    EquipmentManager equipmentManager = EquipmentManager.instance();
                    EquipmentManager.PlayerEquipment equipment = equipmentManager.getEquipment(player);

                    if (gun.isSecondary()) {
                        equipment.getSecondaryWeapon().ifPresent(g -> removeGun(player, g));
                        giveGun(player, gun, 1);
                        equipment.setSecondaryWeapon(gun);
                    } else {
                        equipment.getMainWeapon().ifPresent(g -> removeGun(player, g));
                        giveGun(player, gun, 0);
                        equipment.setMainWeapon(gun);
                    }

                    player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }));
            });
    }

    private void removeGun(EntityPlayer player, GunEntry gun) {
        InventoryPlayer inventory = player.inventory;
        int slots = 9 * 4;

        for (int i = 0; i < slots; i++) {
            ItemStack item = inventory.getStackInSlot(i);

            ResourceLocation registryName = item.getItem().getRegistryName();
            if (registryName == null) continue;

            String itemId = registryName.toString();
            if (itemId.equals(gun.getGunItemId()) || itemId.equals(gun.getAmmoItemId()))
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }

    private void giveGun(EntityPlayer player, GunEntry gun, int hotbarSlot) {
        player.inventory.mainInventory.set(hotbarSlot, gun.getGunItemStack());

        int ammoSlot = hotbarSlot + 9 * 3; // above that slot
        player.inventory.mainInventory.set(ammoSlot, gun.getAmmoItemStack());
    }
}
