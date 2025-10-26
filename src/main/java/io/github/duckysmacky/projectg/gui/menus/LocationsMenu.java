package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.gui.DynamicMenuPage;
import io.github.duckysmacky.projectg.gui.MenuPage;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class LocationsMenu extends DynamicMenuPage {
    public LocationsMenu(MenuPage parent) {
        super("Locations", parent, 5, 9);

        addEntry(new ActionEntry(new ItemStack(Items.MAP),
            (player) -> player.sendMessage(new TextComponentString("Map 1 selected!"))
        ));
    }
}
