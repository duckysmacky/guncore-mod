package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.util.ItemStackCustomizer;
import io.github.duckysmacky.projectg.gui.menu.StaticMenu;
import io.github.duckysmacky.projectg.gui.SubpageEntry;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SettingsMenuPage extends StaticMenu {
    public SettingsMenuPage(BaseMenu parent) {
        super("Settings", parent, 3, 9);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.CLOCK))
                .setName("&f&lMap Settings")
                .addLoreLine("&7Adjust map settings")
                .getItemStack(),
            new MapSettingsMenuPage(this)
        ), 1, 1);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.STANDING_BANNER))
                .setName("&f&lTeams Settings")
                .addLoreLine("&7Adjust teams and scoreboard")
                .getItemStack(),
            new TeamsMenuPage(this)
        ), 1, 3);
    }
}