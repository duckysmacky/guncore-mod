package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.config.catalog.entries.GunCategory;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.util.ItemUtils;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.StaticMenuPage;
import io.github.duckysmacky.guncore.server.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.server.menu.pages.dynamic.WeaponSelectionMenuPage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MainWeaponsMenuPage extends StaticMenuPage {
    public MainWeaponsMenuPage(BaseMenuPage parent) {
        super("Main weapons", parent, 4, 9);
        final var type = EquipmentType.MAIN_WEAPON;

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:ak47"))
                .setName("&f&lAssault Rifles")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.ASSAULT_RIFLE)
        ), 1, 1);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:scar_h"))
                .setName("&f&lBattle Rifles")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.BATTLE_RIFLE)
        ), 1, 2);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("gz:t81sr"))
                .setName("&f&lDMRs")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.DMR)
        ), 1, 3);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:m249"))
                .setName("&f&lLMGs")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.LMG)
        ), 1, 4);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:ump45"))
                .setName("&f&lSMGs")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.SMG)
        ), 1, 5);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:m870"))
                .setName("&f&lShotguns")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.SHOTGUN)
        ), 1, 6);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:m700"))
                .setName("&f&lSniper Rifles")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.SNIPER_RIFLE)
        ), 1, 7);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:glock_17"))
                .setName("&f&lPistols")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.PISTOL)
        ), 2, 2);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.IRON_SWORD))
                .setName("&f&lMelee Weapons")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.MELEE)
        ), 2, 4);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(ItemUtils.getTACZGun("tacz:rpg7"))
                .setName("&f&lSpecial Weapons")
                .getItemStack(),
            new WeaponSelectionMenuPage(this, type, GunCategory.SPECIAL)
        ), 2, 6);
    }
}