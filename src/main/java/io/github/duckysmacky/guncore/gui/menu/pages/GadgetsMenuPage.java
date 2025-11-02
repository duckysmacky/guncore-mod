package io.github.duckysmacky.guncore.gui.menu.pages;

import io.github.duckysmacky.guncore.data.config.ConfigManager;
import io.github.duckysmacky.guncore.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.guncore.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.network.PacketHandler;
import io.github.duckysmacky.guncore.network.packets.EquipGadgetPacket;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

public class GadgetsMenuPage extends DynamicMenu {
    public GadgetsMenuPage(BaseMenu parent) {
        super("Gadgets", parent, 9, 9);

        ConfigManager configManager = ConfigManager.instance();

        configManager.getCachedGadgets().stream()
            .sorted(Comparator.comparingInt(gadget -> gadget.getRarity().sortOrder))
            .forEach(gadget -> {
                ItemStack gadgetItem = gadget.getItemStack();

                addEntry(new ActionEntry(gadgetItem, player -> {
                    PacketHandler.instance().sendToServer(new EquipGadgetPacket(gadget));

                    player.sendMessage(new TextComponentString("Selected gadget: " + gadgetItem.getDisplayName()));
                    player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }));
            });
    }
}
