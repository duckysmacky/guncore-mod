package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.game.GameManager;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.StaticMenu;
import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.entry.DisplayEntry;
import io.github.duckysmacky.projectg.data.items.ItemStackCustomizer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class SettingsMenuPage extends StaticMenu {
    public SettingsMenuPage(BaseMenu parent) {
        super("Settings", parent, 6, 9);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.WATER_BUCKET))
                .setName("&f&lWeather Cycle")
                .getItemStack()
        ), 1, 1);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.DYE))
                .setName("&f&lToggle")
                .addLoreLine("&fClick to toggle the weather cycle")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Work in progress"))
        ), 2, 1);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.REDSTONE_TORCH))
                .setName("&f&lSetup teams")
                .addLoreLine("&7Automatically sets up scoreboard teams and the death counter")
                .getItemStack(),
            p -> GameManager.instance().setupScoreboardTeams()
        ), 4, 1);
    }
}