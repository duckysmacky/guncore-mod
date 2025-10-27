package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class KitsMenu extends StaticMenuPage {
    public KitsMenu(MenuPage parent) {
        super("Kits", parent, 3, 9);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Items.IRON_CHESTPLATE))
                .setName("&f&lAssault Class")
                .addLoreLine("&7Excellent all-rounder kits")
                .addLoreLine("&7Utility for better survivability and easier combat"),
            (player) -> player.sendMessage(new TextComponentString("Assault kits clicked!"))
        ), 1, 2);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Items.ENDER_EYE))
                .setName("&f&lSkirmisher Class")
                .addLoreLine("&7Fast-paced kits for aggressive playstyles")
                .addLoreLine("&7Utility to help initiate a fight or move around"),
            (player) -> player.sendMessage(new TextComponentString("Skirmisher kits clicked!"))
        ), 1, 3);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Items.DIAMOND))
                .setName("&f&lRandom Kit")
                .addLoreLine("&7Select a random kit"),
            (player) -> player.sendMessage(new TextComponentString("Random kit clicked!"))
        ), 1, 4);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Items.IRON_SWORD))
                .setName("&f&lAssassin Class")
                .addLoreLine("&7Kits focused on destruction and high damage")
                .addLoreLine("&7Utility helps eliminate opponents or destroy areas"),
            (player) -> player.sendMessage(new TextComponentString("Assassin kits clicked!"))
        ), 1, 5);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Blocks.PISTON))
                .setName("&f&lSentinel Class")
                .addLoreLine("&7Defensive kits for holding positions")
                .addLoreLine("&7Utility to fortify areas and support teammates"),
            (player) -> player.sendMessage(new TextComponentString("Sentinel kits clicked!"))
        ), 1, 6);
    }
}