package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.gui.*;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.StaticMenu;
import io.github.duckysmacky.projectg.util.ItemStackCustomizer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class MapSettingsMenuPage extends StaticMenu {
    public MapSettingsMenuPage(BaseMenu parent) {
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