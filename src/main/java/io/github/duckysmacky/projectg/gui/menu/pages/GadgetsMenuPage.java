package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.data.ConfigLoader;
import io.github.duckysmacky.projectg.data.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.projectg.data.catalog.guns.GunEntry;
import io.github.duckysmacky.projectg.game.EquipmentManager;
import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.DynamicMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class GadgetsMenuPage extends DynamicMenu {
    public GadgetsMenuPage(BaseMenu parent) {
        super("Gadgets", parent, 9, 9);

        ConfigLoader configLoader = ConfigLoader.instance();

        configLoader.getCachedGadgets().stream()
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
        player.inventory.mainInventory.removeIf(item -> {
            ResourceLocation registryName = item.getItem().getRegistryName();
            if (registryName == null) return false;

            String itemId = registryName.toString();
            if (itemId.equals(gadget.getItemId())) return true;

            return gadget.getAdditionalItemIds().stream()
                .anyMatch(itemId::equals);
        });
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
