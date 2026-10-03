package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.EquippableEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipBuildPacket;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

/** Lists the catalog of a single build-a-class slot (items or armor), rarest last */
public class BuildSelectionMenuPage extends DynamicMenu {
    public BuildSelectionMenuPage(BaseMenu parent, EquipmentType type) {
        super(type.display, parent, 9, 9);

        ConfigManager.instance().getCatalogManager().<CatalogEntry>getCatalog(type.catalog).stream()
            .map(entry -> (EquippableEntry) entry)
            .sorted(Comparator.comparingInt(entry -> entry.getRarity().sortOrder))
            .forEach(entry -> {
                ItemStack icon = entry.getIcon();

                addEntry(new ActionEntry(icon, player -> {
                    PacketHandler.instance().sendToServer(new EquipBuildPacket(type, entry.getName()));

                    player.sendMessage(new TextComponentString(type.display + " selected: " + icon.getDisplayName()));
                    player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

                    open(player);
                }));
            });
    }
}
