package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.util.ItemStackCustomizer;
import io.github.duckysmacky.projectg.gui.StaticMenuPage;
import io.github.duckysmacky.projectg.gui.SubpageEntry;
import net.minecraft.init.Items;

public class MainMenu extends StaticMenuPage {
    public MainMenu() {
        super("Menu", null, 3, 9);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.IRON_CHESTPLATE))
                .setName("&f&lEquipment")
                .addLoreLine("&7Choose your equipment")
                .getItemStack(),
            new EquipmentMenu(this)
        ), 1, 1);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.COMPASS))
                .setName("&f&lLocations")
                .addLoreLine("&7Choose a location to fight in")
                .getItemStack(),
            new LocationsMenu(this)
        ), 1, 4);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.COMPARATOR))
                .setName("&f&lSettings")
                .addLoreLine("&7Adjust map and teams settings")
                .getItemStack(),
            new SettingsMenu(this)
        ), 1, 7);
    }
}
