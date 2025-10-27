package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.gui.MenuIcon;
import io.github.duckysmacky.projectg.gui.MenuPage;
import io.github.duckysmacky.projectg.gui.StaticMenuPage;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class LocationsMenu extends StaticMenuPage {
    public LocationsMenu(MenuPage parent) {
        super("Locations", parent, 3, 9);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Blocks.IRON_BLOCK))
                .setName("&f&lNewport")
                .addLoreLine("&7The original classic map")
                .addLoreLine("&fComplexity: [3 / 5]")
                .addLoreLine("&fInterior: [4 / 5]")
                .addLoreLine("&fExterior: [2 / 5]")
                .addLoreLine("&fSize: [3 / 5]"),
            (player) -> player.sendMessage(new TextComponentString("Newport City map clicked!"))
        ), 1, 2);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Blocks.QUARTZ_BLOCK))
                .setName("&f&lRadiant")
                .addLoreLine("&7The best map")
                .addLoreLine("&fComplexity: [4 / 5]")
                .addLoreLine("&fInterior: [5 / 5]")
                .addLoreLine("&fExterior: [4 / 5]")
                .addLoreLine("&fSize: [2 / 5]")
                .addLoreLine("&aSpecial Features: &7Fights in the dark"),
            (player) -> player.sendMessage(new TextComponentString("Radiant City map clicked!"))
        ), 1, 3);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Blocks.SANDSTONE))
                .setName("&f&lShmar")
                .addLoreLine("&7The most open and the tallest map")
                .addLoreLine("&fComplexity: [2 / 5]")
                .addLoreLine("&fInterior: [1 / 5]")
                .addLoreLine("&fExterior: [4 / 5]")
                .addLoreLine("&fSize: [4 / 5]"),
            (player) -> player.sendMessage(new TextComponentString("Shmar City map clicked!"))
        ), 1, 4);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Blocks.BRICK_BLOCK))
                .setName("&f&lAudia")
                .addLoreLine("&7The biggest and the most diverse map")
                .addLoreLine("&fComplexity: [4 / 5]")
                .addLoreLine("&fInterior: [3 / 5]")
                .addLoreLine("&fExterior: [4 / 5]")
                .addLoreLine("&fSize: [5 / 5]")
                .addLoreLine("&aSpecial Features: &7Fights in the dark"),
            (player) -> player.sendMessage(new TextComponentString("Audia City map clicked!"))
        ), 1, 5);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Blocks.STONE))
                .setName("&f&lCity 17")
                .addLoreLine("&7A dystopian city under siege")
                .addLoreLine("&fComplexity: [5 / 5]")
                .addLoreLine("&fInterior: [2 / 5]")
                .addLoreLine("&fExterior: [5 / 5]")
                .addLoreLine("&fSize: [3 / 5]"),
            (player) -> player.sendMessage(new TextComponentString("City 17 map clicked!"))
        ), 1, 6);
    }
}
