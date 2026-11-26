package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.entries.RarityScoreEntry;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;

public class WeaponsMenuPage extends StaticMenu {
    public WeaponsMenuPage(BaseMenu parent) {
        super("Weapons", parent, 3, 9);

        addEntry(new RarityScoreEntry(), 0, 4);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("minecraft:iron_sword")
                .setName("&f&lPrimary weapons")
                .addLoreLine("&7Select your main weapon")
                .getItemStack(),
            new WeaponCategoriesMenuPage(this, false)
        ), 1, 2);

        addEntry(new SubpageEntry(
            ItemStackCustomizer.from("minecraft:wooden_sword")
                .setName("&f&lSecondary weapons")
                .addLoreLine("&7Select your secondary weapon")
                .getItemStack(),
            new WeaponCategoriesMenuPage(this, true)
        ), 1, 6);
    }
}
