package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunCategory;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipGunPacket;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

public class GunsMenuPage extends DynamicMenu {
    public GunsMenuPage(BaseMenu parent, GunCategory gunCategory) {
        super(gunCategory.display + "s", parent, 6, 9);

        ConfigManager.instance().getCatalogManager().<GunEntry>getCatalog(CatalogType.GUNS).stream()
            .filter(gun -> gun.getCategory() == gunCategory)
            .sorted(Comparator.comparingInt(gun -> gun.getRarity().sortOrder))
            .forEach(gun -> {
                ItemStack gunItem = gun.getGunItemStack();

                addEntry(new ActionEntry(gunItem, player -> {
                    PacketHandler.instance().sendToServer(new EquipGunPacket(gun));

                    player.sendMessage(new TextComponentString("Selected gun: " + gunItem.getDisplayName()));
                    player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

                    open(player);
                }));
            });
    }
}
