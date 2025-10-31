package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.data.catalog.CatalogLoader;
import io.github.duckysmacky.projectg.data.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.projectg.game.EquipmentManager;
import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.DynamicMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;
import java.util.List;

public class GadgetsMenuPage extends DynamicMenu {
    public GadgetsMenuPage(BaseMenu parent) {
        super("Gadgets", parent, 9, 9);

        CatalogLoader catalogLoader = CatalogLoader.instance();

        catalogLoader.getCachedGadgets().stream()
            .sorted(Comparator.comparingInt(gadget -> gadget.getRarity().sortOrder))
            .forEach(gadget -> {
                ItemStack gadgetItem = gadget.getItemStack();

                addEntry(new ActionEntry(gadgetItem, player -> {
                    player.sendMessage(new TextComponentString("Selected gadget: " + gadgetItem.getDisplayName()));

                    EquipmentManager equipmentManager = EquipmentManager.instance();
                    EquipmentManager.PlayerEquipment equipment = equipmentManager.getEquipment(player);

                    equipment.getGadget().ifPresent(g -> removeGadget(player, g));
                    giveGadget(player, gadget);
                    equipment.setGadget(gadget);

                    player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }));
            });
    }

    private void removeGadget(EntityPlayer player, GadgetEntry gadget) {
        InventoryPlayer inventory = player.inventory;
        int slots = 9 * 4;

        for (int i = 0; i < slots; i++) {
            ItemStack item = inventory.getStackInSlot(i);

            ResourceLocation registryName = item.getItem().getRegistryName();
            if (registryName == null) continue;

            String itemId = registryName.toString();
            if (itemId.equals(gadget.getItemId()) || gadget.getAdditionalItemIds().stream().anyMatch(itemId::equals))
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }

    private void giveGadget(EntityPlayer player, GadgetEntry gadget) {
        int mainSlot = 4;
        player.inventory.mainInventory.set(mainSlot, gadget.getItemStack());

        int extraSlot = mainSlot + 9 * 3;
        List<ItemStack> extraItems = gadget.getAdditionalItemStacks();
        for (int i = 0; i < extraItems.size() && i < 3; i++)
            player.inventory.mainInventory.set(extraSlot - 9 * i, extraItems.get(i));
    }
}
