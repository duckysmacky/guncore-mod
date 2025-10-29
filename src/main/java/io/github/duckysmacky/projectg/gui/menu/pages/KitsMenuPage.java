package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.gui.*;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.StaticMenu;
import io.github.duckysmacky.projectg.util.ItemStackCustomizer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.text.TextComponentString;

public class KitsMenuPage extends StaticMenu {
    public KitsMenuPage(BaseMenu parent) {
        super("Kits", parent, 3, 9);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.IRON_CHESTPLATE))
                .setName("&f&lAssault Class")
                .addLoreLine("&7Excellent all-rounder kits")
                .addLoreLine("&7Utility for better survivability and easier combat")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Assault kits clicked!"))
        ), 1, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.ENDER_EYE))
                .setName("&f&lSkirmisher Class")
                .addLoreLine("&7Fast-paced kits for aggressive playstyles")
                .addLoreLine("&7Utility to help initiate a fight or move around")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Skirmisher kits clicked!"))
        ), 1, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.DIAMOND))
                .setName("&f&lRandom Kit")
                .addLoreLine("&7Select a random kit")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Random kit clicked!"))
        ), 1, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.IRON_SWORD))
                .setName("&f&lAssassin Class")
                .addLoreLine("&7Kits focused on destruction and high damage")
                .addLoreLine("&7Utility helps eliminate opponents or destroy areas")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Assassin kits clicked!"))
        ), 1, 5);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Blocks.PISTON))
                .setName("&f&lSentinel Class")
                .addLoreLine("&7Defensive kits for holding positions")
                .addLoreLine("&7Utility to fortify areas and support teammates")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Sentinel kits clicked!"))
        ), 1, 6);
    }
}