package io.github.duckysmacky.guncore.common.game;

import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.entries.ArmorEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.GunEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.ItemEntry;

public enum EquipmentType {
    MAIN_WEAPON("Main weapon", 0, GunEntry.class, CatalogType.MAIN_WEAPONS),
    SECONDARY_WEAPON("Secondary weapon", 1, GunEntry.class, CatalogType.SECONDARY_WEAPONS),
    LETHAL("Lethal equipment", 2, ItemEntry.class, CatalogType.LETHALS),
    TACTICAL("Tactical equipment", 3, ItemEntry.class, CatalogType.TACTICALS),
    GADGET("Gadget", 4, ItemEntry.class, CatalogType.GADGETS),
    UTILITY("Utility item", 5, ItemEntry.class, CatalogType.UTILITY),
    CONSUMABLE("Consumable item", 6, ItemEntry.class, CatalogType.CONSUMABLES),
    PERK("Perk", 16, ItemEntry.class, CatalogType.CONSUMABLES),
    ARMOR("Armor", 36, ArmorEntry.class, CatalogType.ARMOR);

    public final String display;
    public final int slot;
    public final Class<? extends CatalogEntry> entryClass;
    public final CatalogType catalog;

    EquipmentType(String display, int slot, Class<? extends CatalogEntry> entryClass, CatalogType catalog) {
        this.display = display;
        this.slot = slot;
        this.entryClass = entryClass;
        this.catalog = catalog;
    }

    public enum ArmorType {
        HELMET(3),
        CHESTPLATE(2),
        LEGGINGS(1),
        BOOTS(0);

        public final int slot;

        ArmorType(int slot) {
            this.slot = slot;
        }
    }
}
