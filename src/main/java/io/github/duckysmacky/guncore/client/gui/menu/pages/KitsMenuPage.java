package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitClass;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipKitPacket;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

public class KitsMenuPage extends DynamicMenu {
    public KitsMenuPage(BaseMenu parent, KitClass kitClass) {
        super(kitClass.display + " Kits", parent, 5, 9);

        ConfigManager.instance().getCatalogManager().<KitEntry>getCatalog(CatalogType.KITS).stream()
            .filter(kit -> kit.getKitClass() == kitClass)
            .sorted(Comparator.comparingInt(kit -> kit.getTier().sortOrder))
            .forEach(kit -> {
                ItemStack kitIcon = kit.getIconItem();

                addEntry(new ActionEntry(kitIcon, (player) -> {
                    PacketHandler.instance().sendToServer(new EquipKitPacket(kit));

                    player.sendMessage(new TextComponentString("Selected kit: " + kitIcon.getDisplayName()));
                    player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f, 1f);

                    open(player);
                }));
            });
    }
}
