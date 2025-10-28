package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.*;
import io.github.duckysmacky.projectg.util.ItemStackCustomizer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class MapSettingsMenu extends StaticMenuPage {
    public MapSettingsMenu(MenuPage parent) {
        super("Map Settings", parent, 6, 9);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.WATER_BUCKET))
                .setName("&f&lWeather Cycle")
                .getItemStack()
        ), 1, 1);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.DYE))
                .setName("&f&lToggle Weather Cycle")
                .addLoreLine("&fClick to toggle the weather cycle")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Weather cycle toggled!"))
        ), 2, 1);
    }
}