package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.DynamicMenuPage;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.entries.EquipmentEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class GadgetsMenuPage extends DynamicMenuPage {
    public GadgetsMenuPage(BaseMenuPage parent) {
        super("Gadgets", parent, 9, 9);

        ConfigManager.instance().getCatalogManager().<EquipmentEntry>getCatalog(CatalogType.GADGETS).stream()
            .sorted(Comparator.comparingInt(gadget -> gadget.getRarity().sortOrder))
            .forEach(gadget -> {
                ItemStack gadgetItem = gadget.getItemStack();

                addEntry(new ActionEntry(gadgetItem, player -> {
                    EquipmentController.equipGadget(player, gadget);

                    player.sendSystemMessage(Component.literal("Selected gadget: " + gadgetItem.getHoverName()));
                    player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f);

                    open(player);
                }));
            });
    }
}
