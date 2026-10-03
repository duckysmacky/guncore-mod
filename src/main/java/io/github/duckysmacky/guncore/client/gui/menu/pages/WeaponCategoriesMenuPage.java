package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunCategory;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.item.ItemStack;

public class WeaponCategoriesMenuPage extends StaticMenu {
    private final boolean secondary;

    public WeaponCategoriesMenuPage(BaseMenu parent, boolean secondary) {
        super(secondary ? "Secondary Weapons" : "Primary Weapons", parent, 4, 9);
        this.secondary = secondary;

        // Assault Rifles, Battle Rifles, DMRs, LMGs, SMGs, Shotguns, Sniper Rifles, Sidearms, Special Weapons
        addCategory(
            ItemStackCustomizer.from("mw:m4a1")
                .setName("&f&lAssault Rifles")
                .getItemStack(),
            GunCategory.ASSAULT_RIFLE,
            1, 1
        );

        addCategory(
            ItemStackCustomizer.from("mw:scar_h_cqc")
                .setName("&f&lBattle Rifles")
                .getItemStack(),
            GunCategory.BATTLE_RIFLE,
            1, 2
        );

        addCategory(
            ItemStackCustomizer.from("mw:m110_sass")
                .setName("&f&lDMRs")
                .getItemStack(),
            GunCategory.DMR,
            1, 3
        );

        addCategory(
            ItemStackCustomizer.from("mw:mg42")
                .setName("&f&lLMGs")
                .getItemStack(),
            GunCategory.LMG,
            1, 4
        );

        addCategory(
            ItemStackCustomizer.from("mw:mp7")
                .setName("&f&lSMGs")
                .getItemStack(),
            GunCategory.SMG,
            1, 5
        );

        addCategory(
            ItemStackCustomizer.from("mw:remington870")
                .setName("&f&lShotguns")
                .getItemStack(),
            GunCategory.SHOTGUN,
            1, 6
        );

        addCategory(
            ItemStackCustomizer.from("mw:m40a6")
                .setName("&f&lSniper Rifles")
                .getItemStack(),
            GunCategory.SNIPER_RIFLE,
            1, 7
        );

        addCategory(
            ItemStackCustomizer.from("mw:chainsaw")
                .setName("&f&lMelee Weapons")
                .getItemStack(),
            GunCategory.MELEE,
            2, 4
        );

        addCategory(
            ItemStackCustomizer.from("mw:m17")
                .setName("&f&lPistols")
                .getItemStack(),
            GunCategory.PISTOL,
            2, 2
        );

        addCategory(
            ItemStackCustomizer.from("mw:rpg7")
                .setName("&f&lSpecial Weapons")
                .getItemStack(),
            GunCategory.SPECIAL,
            2, 6
        );
    }

    private void addCategory(ItemStack icon, GunCategory category, int row, int col) {
        CatalogType type = secondary ? CatalogType.SECONDARY_WEAPONS : CatalogType.PRIMARY_WEAPONS;
        boolean hasGuns = ConfigManager.instance().getCatalogManager().<GunEntry>getCatalog(type).stream()
            .anyMatch(gun -> gun.getCategory() == category);
        if (!hasGuns) return;

        addEntry(new SubpageEntry(icon, new GunsMenuPage(this, secondary, category)), row, col);
    }
}
