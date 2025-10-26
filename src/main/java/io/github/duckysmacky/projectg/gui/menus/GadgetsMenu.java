package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.gui.DynamicMenuPage;
import io.github.duckysmacky.projectg.gui.MenuPage;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class GadgetsMenu extends DynamicMenuPage {
    public GadgetsMenu(MenuPage parent) {
        super("Gadgets", parent, 5, 9);

        addEntry(new ActionEntry(new ItemStack(Items.ELYTRA), (player) -> {
            player.sendMessage(new TextComponentString("Elytra gadget selected!"));
        }));

        addEntry(new ActionEntry(new ItemStack(Items.GOLDEN_APPLE), (player) -> {
            player.sendMessage(new TextComponentString("Golden apple gadget selected!"));
        }));
    }
}
