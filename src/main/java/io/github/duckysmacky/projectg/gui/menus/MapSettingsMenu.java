package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.gui.MenuPage;
import io.github.duckysmacky.projectg.gui.StaticMenuPage;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class MapSettingsMenu extends StaticMenuPage {
    public MapSettingsMenu(MenuPage parent) {
        super("Map Settings", parent, 5, 9);

        addEntry(new ActionEntry(new ItemStack(Items.WATER_BUCKET), (player) -> {
            player.sendMessage(new TextComponentString("Weather cycle toggled!"));
        }), 2, 2);
    }
}