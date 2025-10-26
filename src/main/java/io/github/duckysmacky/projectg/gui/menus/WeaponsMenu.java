package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.gui.DynamicMenuPage;
import io.github.duckysmacky.projectg.gui.MenuPage;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class WeaponsMenu extends DynamicMenuPage {
    public WeaponsMenu(MenuPage parent) {
        super("Weapons", parent, 5, 9);

        for (int i = 0; i < 9; i++) {
            addEntry(new ActionEntry(new ItemStack(Items.DIAMOND_SWORD), (player) -> {
                player.sendMessage(new TextComponentString("Weapon Category clicked!"));
            }));
        }
    }
}