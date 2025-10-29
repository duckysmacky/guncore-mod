package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.config.catalog.GunCategory;
import io.github.duckysmacky.projectg.gui.*;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.StaticMenu;
import io.github.duckysmacky.projectg.util.ItemStackCustomizer;

public class WeaponsMenuPage extends StaticMenu {
    public WeaponsMenuPage(BaseMenu parent) {
        super("Weapons", parent, 4, 9);

        // Assault Rifles, Battle Rifles, DMRs, LMGs, SMGs, Shotguns, Sniper Rifles, Sidearms, Special Weapons
        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lAssault Rifles")
                .getItemStack(),
            new GunMenuPage(this, GunCategory.ASSAULT_RIFLE)
        ), 1, 1);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lBattle Rifles")
                .getItemStack(),
            new GunMenuPage(this, GunCategory.BATTLE_RIFLE)
        ), 1, 2);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lDMRs")
                .getItemStack(),
            new GunMenuPage(this, GunCategory.DMR)
        ), 1, 3);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lLMGs")
                .getItemStack(),
            new GunMenuPage(this, GunCategory.LMG)
        ), 1, 4);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lSMGs")
                .getItemStack(),
            new GunMenuPage(this, GunCategory.SMG)
        ), 1, 5);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lShotguns")
                .getItemStack(),
            new GunMenuPage(this, GunCategory.SHOTGUN)
        ), 1, 6);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lSniper Rifles")
                .getItemStack(),
            new GunMenuPage(this, GunCategory.SNIPER_RIFLE)
        ), 1, 7);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lSidearms")
                .getItemStack(),
            new GunMenuPage(this, GunCategory.SIDEARM)
        ), 2, 2);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:chainsaw")
                .setName("&f&lMelee Weapons")
                .getItemStack(),
            new GunMenuPage(this, GunCategory.MELEE)
        ), 2, 4);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lSpecial Weapons")
                .getItemStack(),
            new GunMenuPage(this, GunCategory.SPECIAL)
        ), 2, 6);
    }
}