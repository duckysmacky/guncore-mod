package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.MenuIcon;
import io.github.duckysmacky.projectg.gui.StaticMenuPage;
import io.github.duckysmacky.projectg.gui.SubpageEntry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MainMenu extends StaticMenuPage {
    public MainMenu() {
        super("Menu", null, 3, 9);

        addEntry(new SubpageEntry(
            new MenuIcon(new ItemStack(Items.IRON_CHESTPLATE))
                .setName("&f&lEquipment")
                .addLoreLine("&7Choose your equipment"),
            new EquipmentMenu(this)
        ), 1, 1);

        addEntry(new SubpageEntry(
            new MenuIcon(new ItemStack(Items.COMPASS))
                .setName("&f&lLocations")
                .addLoreLine("&7Choose a location to fight in"),
            new LocationsMenu(this)
        ), 1, 4);

        addEntry(new SubpageEntry(
            new MenuIcon(new ItemStack(Items.COMPARATOR))
                .setName("&f&lSettings")
                .addLoreLine("&7Adjust map and teams settings"),
            new SettingsMenu(this)
        ), 1, 7);
    }
}
