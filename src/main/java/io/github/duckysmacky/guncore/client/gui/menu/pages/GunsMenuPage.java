package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipGunPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

public class GunsMenuPage extends DynamicMenu {
    public GunsMenuPage(BaseMenu parent, boolean secondary, MenuSectionEntry gunCategory) {
        super(gunCategory.getName(), parent, 6, 9);

        ConfigManager.instance().getCatalogManager().<GunEntry>getCatalog(secondary ? CatalogType.SECONDARY_WEAPONS : CatalogType.PRIMARY_WEAPONS).stream()
            .filter(gun -> gun.getCategory().equals(gunCategory.getId()))
            .sorted(Comparator.comparingInt(gun -> gun.getRarity().sortOrder))
            .forEach(gun -> {
                ItemStack gunItem = gun.getGunItemStack();

                addEntry(new ActionEntry(gunItem, player -> {
                    equip(player, secondary, gun);
                    open(player);
                }));
            });
    }

    public static void equip(EntityPlayer player, boolean secondary, GunEntry gun) {
        PacketHandler.instance().sendToServer(new EquipGunPacket(secondary, gun));

        player.sendMessage(new TextComponentString("Selected gun: " + gun.getName()));
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
}
