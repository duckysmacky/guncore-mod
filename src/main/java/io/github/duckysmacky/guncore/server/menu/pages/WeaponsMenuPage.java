package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.config.catalog.guns.GunCategory;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.StaticMenuPage;
import io.github.duckysmacky.guncore.server.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;

public class WeaponsMenuPage extends StaticMenuPage {
    public WeaponsMenuPage(BaseMenuPage parent) {
        super("Weapons", parent, 4, 9);

        // Assault Rifles, Battle Rifles, DMRs, LMGs, SMGs, Shotguns, Sniper Rifles, Sidearms, Special Weapons
        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lAssault Rifles")
                .getItemStack(),
            new GunsMenuPage(this, GunCategory.ASSAULT_RIFLE)
        ), 1, 1);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:scar_h_cqc")
                .setName("&f&lBattle Rifles")
                .getItemStack(),
            new GunsMenuPage(this, GunCategory.BATTLE_RIFLE)
        ), 1, 2);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m110_sass")
                .setName("&f&lDMRs")
                .getItemStack(),
            new GunsMenuPage(this, GunCategory.DMR)
        ), 1, 3);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:mg42")
                .setName("&f&lLMGs")
                .getItemStack(),
            new GunsMenuPage(this, GunCategory.LMG)
        ), 1, 4);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:mp7")
                .setName("&f&lSMGs")
                .getItemStack(),
            new GunsMenuPage(this, GunCategory.SMG)
        ), 1, 5);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:remington870")
                .setName("&f&lShotguns")
                .getItemStack(),
            new GunsMenuPage(this, GunCategory.SHOTGUN)
        ), 1, 6);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m40a6")
                .setName("&f&lSniper Rifles")
                .getItemStack(),
            new GunsMenuPage(this, GunCategory.SNIPER_RIFLE)
        ), 1, 7);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:chainsaw")
                .setName("&f&lMelee Weapons")
                .getItemStack(),
            new GunsMenuPage(this, GunCategory.MELEE)
        ), 2, 4);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:m17")
                .setName("&f&lSidearms")
                .getItemStack(),
            new GunsMenuPage(this, GunCategory.SIDEARM)
        ), 2, 2);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("mw:rpg7")
                .setName("&f&lSpecial Weapons")
                .getItemStack(),
            new GunsMenuPage(this, GunCategory.SPECIAL)
        ), 2, 6);
    }
}