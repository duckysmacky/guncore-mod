package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.gui.MenuPage;
import io.github.duckysmacky.projectg.gui.StaticMenuPage;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class TeamsMenu extends StaticMenuPage {
    public TeamsMenu(MenuPage parent) {
        super("Teams", parent, 5, 9);

        addEntry(new ActionEntry(new ItemStack(Items.COAL), (player) -> {
            player.sendMessage(new TextComponentString("Joined placeholder team!"));
        }), 2, 2);
    }
}