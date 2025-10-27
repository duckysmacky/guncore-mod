package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class MapSettingsMenu extends StaticMenuPage {
    public MapSettingsMenu(MenuPage parent) {
        super("Map Settings", parent, 6, 9);

        addEntry(new DisplayEntry(
            new MenuIcon(new ItemStack(Items.WATER_BUCKET))
                .setName("&f&lWeather Cycle")
        ), 1, 1);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Items.DYE))
                .setName("&f&lToggle Weather Cycle")
                .addLoreLine("&fClick to toggle the weather cycle"),
            (player) -> player.sendMessage(new TextComponentString("Weather cycle toggled!"))
        ), 2, 1);
    }
}