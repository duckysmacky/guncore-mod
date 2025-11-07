package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipGadgetPacket;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

public class GadgetsMenuPage extends DynamicMenu {
    public GadgetsMenuPage(BaseMenu parent) {
        super("Gadgets", parent, 9, 9);

        ConfigManager.instance().getCatalogManager().<GadgetEntry>getCatalog(CatalogType.GADGETS).stream()
            .sorted(Comparator.comparingInt(gadget -> gadget.getRarity().sortOrder))
            .forEach(gadget -> {
                ItemStack gadgetItem = gadget.getItemStack();

                addEntry(new ActionEntry(gadgetItem, player -> {
                    PacketHandler.instance().sendToServer(new EquipGadgetPacket(gadget));

                    player.sendMessage(new TextComponentString("Selected gadget: " + gadgetItem.getDisplayName()));
                    player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

                    open(player);
                }));
            });
    }
}
