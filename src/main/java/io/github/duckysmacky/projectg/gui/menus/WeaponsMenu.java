package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.gui.MenuIcon;
import io.github.duckysmacky.projectg.gui.MenuPage;
import io.github.duckysmacky.projectg.gui.StaticMenuPage;
import net.minecraft.util.text.TextComponentString;

public class WeaponsMenu extends StaticMenuPage {
    public WeaponsMenu(MenuPage parent) {
        super("Weapons", parent, 4, 9);

        // Assault Rifles, Battle Rifles, DMRs, LMGs, SMGs, Shotguns, Sniper Rifles, Sidearms, Special Weapons
        addEntry(new ActionEntry(
            MenuIcon.fromItem("mw:m4a1")
                .setName("&f&lAssault Rifles"),
            (player) -> player.sendMessage(new TextComponentString("Assault rifles clicked!"))
        ), 1, 1);

        addEntry(new ActionEntry(
            MenuIcon.fromItem("mw:m4a1")
                .setName("&f&lBattle Rifles"),
            (player) -> player.sendMessage(new TextComponentString("Battle rifles clicked!"))
        ), 1, 2);

        addEntry(new ActionEntry(
            MenuIcon.fromItem("mw:m4a1")
                .setName("&f&lDMRs"),
            (player) -> player.sendMessage(new TextComponentString("DMRs clicked!"))
        ), 1, 3);

        addEntry(new ActionEntry(
            MenuIcon.fromItem("mw:m4a1")
                .setName("&f&lLMGs"),
            (player) -> player.sendMessage(new TextComponentString("LMGs clicked!"))
        ), 1, 4);

        addEntry(new ActionEntry(
            MenuIcon.fromItem("mw:m4a1")
                .setName("&f&lSMGs"),
            (player) -> player.sendMessage(new TextComponentString("SMGs clicked!"))
        ), 1, 5);

        addEntry(new ActionEntry(
            MenuIcon.fromItem("mw:m4a1")
                .setName("&f&lShotguns"),
            (player) -> player.sendMessage(new TextComponentString("Shotguns clicked!"))
        ), 1, 6);

        addEntry(new ActionEntry(
            MenuIcon.fromItem("mw:m4a1")
                .setName("&f&lSniper Rifles"),
            (player) -> player.sendMessage(new TextComponentString("Sniper rifles clicked!"))
        ), 1, 7);

        addEntry(new ActionEntry(
            MenuIcon.fromItem("mw:m4a1")
                .setName("&f&lSidearms"),
            (player) -> player.sendMessage(new TextComponentString("Sidearms clicked!"))
        ), 2, 2);

        addEntry(new ActionEntry(
            MenuIcon.fromItem("mw:m4a1")
                .setName("&f&lSpecial Weapons"),
            (player) -> player.sendMessage(new TextComponentString("Special weapons clicked!"))
        ), 2, 6);
    }
}