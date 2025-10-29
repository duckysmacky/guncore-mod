package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.util.ItemStackCustomizer;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.StaticMenu;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextComponentString;

public class LocationsMenuPage extends StaticMenu {
    public LocationsMenuPage(BaseMenu parent) {
        super("Locations", parent, 3, 9);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Blocks.IRON_BLOCK))
                .setName("&f&lNewport")
                .addLoreLine("&7The original classic map")
                .addLoreLine("&fComplexity: [3 / 5]")
                .addLoreLine("&fInterior: [4 / 5]")
                .addLoreLine("&fExterior: [2 / 5]")
                .addLoreLine("&fSize: [3 / 5]")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Newport City map clicked!"))
        ), 1, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Blocks.QUARTZ_BLOCK))
                .setName("&f&lRadiant")
                .addLoreLine("&7The best map")
                .addLoreLine("&fComplexity: [4 / 5]")
                .addLoreLine("&fInterior: [5 / 5]")
                .addLoreLine("&fExterior: [4 / 5]")
                .addLoreLine("&fSize: [2 / 5]")
                .addLoreLine("&aSpecial Features: &7Fights in the dark")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Radiant City map clicked!"))
        ), 1, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Blocks.SANDSTONE))
                .setName("&f&lShmar")
                .addLoreLine("&7The most open and the tallest map")
                .addLoreLine("&fComplexity: [2 / 5]")
                .addLoreLine("&fInterior: [1 / 5]")
                .addLoreLine("&fExterior: [4 / 5]")
                .addLoreLine("&fSize: [4 / 5]")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Shmar City map clicked!"))
        ), 1, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Blocks.BRICK_BLOCK))
                .setName("&f&lAudia")
                .addLoreLine("&7The biggest and the most diverse map")
                .addLoreLine("&fComplexity: [4 / 5]")
                .addLoreLine("&fInterior: [3 / 5]")
                .addLoreLine("&fExterior: [4 / 5]")
                .addLoreLine("&fSize: [5 / 5]")
                .addLoreLine("&aSpecial Features: &7Fights in the dark")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Audia City map clicked!"))
        ), 1, 5);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Blocks.STONE))
                .setName("&f&lCity 17")
                .addLoreLine("&7A dystopian city under siege")
                .addLoreLine("&fComplexity: [5 / 5]")
                .addLoreLine("&fInterior: [2 / 5]")
                .addLoreLine("&fExterior: [5 / 5]")
                .addLoreLine("&fSize: [3 / 5]")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("City 17 map clicked!"))
        ), 1, 6);
    }
}
