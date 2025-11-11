package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.config.catalog.entries.GunCategory;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.common.util.ItemUtils;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.StaticMenuPage;
import io.github.duckysmacky.guncore.server.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.server.menu.pages.dynamic.WeaponSelectionMenuPage;

public class SecondaryWeaponsMenuPage extends StaticMenuPage {
    public SecondaryWeaponsMenuPage(BaseMenuPage parent) {
        super("Secondary weapons", parent, 3, 9);
        final var type = EquipmentType.SECONDARY_WEAPON;

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:glock_17"))
                .setName("&f&lPistols")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.PISTOL)
        ), 1, 2);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:glock_17"))
                .setName("&f&lSMGs")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.SMG)
        ), 1, 3);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:glock_17"))
                .setName("&f&lShotguns")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.SHOTGUN)
        ), 1, 4);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:glock_17"))
                .setName("&f&lMelee Weapons")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.MELEE)
        ), 1, 5);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:glock_17"))
                .setName("&f&lSpecial Weapons")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.SPECIAL)
        ), 1, 6);
    }
}