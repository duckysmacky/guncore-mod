package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.gui.DynamicMenuPage;
import io.github.duckysmacky.projectg.gui.MenuPage;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class KitsMenu extends DynamicMenuPage {
    public KitsMenu(MenuPage parent) {
        super("Kits", parent, 5, 9);

        addEntry(new ActionEntry(new ItemStack(Items.IRON_SWORD), (player) -> {
            player.sendMessage(new TextComponentString("Assault kits clicked!"));
        }));

        addEntry(new ActionEntry(new ItemStack(Items.BOW), (player) -> {
            player.sendMessage(new TextComponentString("Skirmisher kits clicked!"));
        }));

        addEntry(new ActionEntry(new ItemStack(Items.DIAMOND_SWORD), (player) -> {
            player.sendMessage(new TextComponentString("Assassin kits clicked!"));
        }));

        addEntry(new ActionEntry(new ItemStack(Items.GOLDEN_SWORD), (player) -> {
            player.sendMessage(new TextComponentString("Sentinel kits clicked!"));
        }));
    }
}