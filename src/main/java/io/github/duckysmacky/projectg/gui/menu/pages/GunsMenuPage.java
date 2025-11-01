package io.github.duckysmacky.projectg.gui.menu.pages;

import com.google.gson.Gson;
import io.github.duckysmacky.projectg.data.config.ConfigManager;
import io.github.duckysmacky.projectg.data.config.catalog.guns.GunCategory;
import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.DynamicMenu;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.network.PacketHandler;
import io.github.duckysmacky.projectg.network.packets.EquipGunPacket;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

public class GunsMenuPage extends DynamicMenu {
    public GunsMenuPage(BaseMenu parent, GunCategory gunCategory) {
        super(gunCategory.display + "s", parent, 6, 9);

        Gson gson = new Gson();
        ConfigManager configManager = ConfigManager.instance();

        configManager.getCachedGuns().stream()
            .filter(gun -> gun.getCategory() == gunCategory)
            .sorted(Comparator.comparingInt(gun -> gun.getRarity().sortOrder))
            .forEach(gun -> {
                ItemStack gunItem = gun.getGunItemStack();

                addEntry(new ActionEntry(gunItem, player -> {
                    PacketHandler.instance().sendToServer(new EquipGunPacket(gun));

                    player.sendMessage(new TextComponentString("Selected gun: " + gunItem.getDisplayName()));
                    player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }));
            });
    }
}
