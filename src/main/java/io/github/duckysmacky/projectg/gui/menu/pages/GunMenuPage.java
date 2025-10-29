package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.config.ConfigLoader;
import io.github.duckysmacky.projectg.config.catalog.GunCategory;
import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.DynamicMenu;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

public class GunMenuPage extends DynamicMenu {
    public GunMenuPage(BaseMenu parent, GunCategory gunCategory) {
        super(gunCategory.display + "s", parent, 8, 9);

        ConfigLoader configLoader = ConfigLoader.instance();

        configLoader.getCachedGuns().stream()
            .filter(gun -> gun.getCategory() == gunCategory)
            .sorted(Comparator.comparingInt(gun -> gun.getRarity().sortOrder))
            .forEach(gun -> {
                ItemStack gunItem = gun.getGunItemStack();

                addEntry(new ActionEntry(
                    gunItem,
                    (player) -> player.sendMessage(new TextComponentString("Selected gun: " + gunItem.getDisplayName()))
                ));
            });
    }
}
