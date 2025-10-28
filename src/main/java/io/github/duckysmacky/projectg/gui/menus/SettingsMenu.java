package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.util.ItemStackCustomizer;
import io.github.duckysmacky.projectg.gui.StaticMenuPage;
import io.github.duckysmacky.projectg.gui.SubpageEntry;
import io.github.duckysmacky.projectg.gui.MenuPage;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SettingsMenu extends StaticMenuPage {
    public SettingsMenu(MenuPage parent) {
        super("Settings", parent, 3, 9);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.CLOCK))
                .setName("&f&lMap Settings")
                .addLoreLine("&7Adjust map settings")
                .getItemStack(),
            new MapSettingsMenu(this)
        ), 1, 1);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.STANDING_BANNER))
                .setName("&f&lTeams Settings")
                .addLoreLine("&7Adjust teams and scoreboard")
                .getItemStack(),
            new TeamsMenu(this)
        ), 1, 3);
    }
}