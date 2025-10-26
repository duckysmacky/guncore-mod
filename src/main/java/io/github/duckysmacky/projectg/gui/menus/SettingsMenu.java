package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.StaticMenuPage;
import io.github.duckysmacky.projectg.gui.SubpageEntry;
import io.github.duckysmacky.projectg.gui.MenuPage;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SettingsMenu extends StaticMenuPage {
    public SettingsMenu(MenuPage parent) {
        super("Settings", parent, 5, 9);

        addEntry(new SubpageEntry(new ItemStack(Items.CLOCK), new MapSettingsMenu(this)), 1, 1);
        addEntry(new SubpageEntry(new ItemStack(Items.LEATHER), new TeamsMenu(this)), 1, 3);
    }
}