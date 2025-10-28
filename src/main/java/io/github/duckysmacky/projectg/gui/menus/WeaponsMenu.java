package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.config.catalog.GunCategory;
import io.github.duckysmacky.projectg.gui.*;
import io.github.duckysmacky.projectg.util.ItemStackCustomizer;

public class WeaponsMenu extends StaticMenuPage {
    public WeaponsMenu(MenuPage parent) {
        super("Weapons", parent, 4, 9);

        // Assault Rifles, Battle Rifles, DMRs, LMGs, SMGs, Shotguns, Sniper Rifles, Sidearms, Special Weapons
        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lAssault Rifles")
                .getItemStack(),
            new GunMenu(this, GunCategory.ASSAULT_RIFLE)
        ), 1, 1);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lBattle Rifles")
                .getItemStack(),
            new GunMenu(this, GunCategory.BATTLE_RIFLE)
        ), 1, 2);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lDMRs")
                .getItemStack(),
            new GunMenu(this, GunCategory.DMR)
        ), 1, 3);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lLMGs")
                .getItemStack(),
            new GunMenu(this, GunCategory.LMG)
        ), 1, 4);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lSMGs")
                .getItemStack(),
            new GunMenu(this, GunCategory.SMG)
        ), 1, 5);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lShotguns")
                .getItemStack(),
            new GunMenu(this, GunCategory.SHOTGUN)
        ), 1, 6);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lSniper Rifles")
                .getItemStack(),
            new GunMenu(this, GunCategory.SNIPER_RIFLE)
        ), 1, 7);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lSidearms")
                .getItemStack(),
            new GunMenu(this, GunCategory.SIDEARM)
        ), 2, 2);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lSpecial Weapons")
                .getItemStack(),
            new GunMenu(this, GunCategory.SPECIAL)
        ), 2, 6);
    }
}