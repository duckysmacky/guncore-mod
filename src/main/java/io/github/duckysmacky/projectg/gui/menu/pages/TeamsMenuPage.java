package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.data.ItemStackCustomizer;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.StaticMenu;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class TeamsMenuPage extends StaticMenu {
    public TeamsMenuPage(BaseMenu parent) {
        super("Teams", parent, 5, 9);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.DYE))
                .setName("&f&lJoin Placeholder Team")
                .addLoreLine("&7Click to join the placeholder team")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Joined placeholder team!"))
        ), 2, 1);
    }
}