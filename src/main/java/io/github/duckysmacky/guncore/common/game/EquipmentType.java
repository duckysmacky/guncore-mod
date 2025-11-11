package io.github.duckysmacky.guncore.common.game;

import io.github.duckysmacky.guncore.common.config.catalog.entries.ArmorEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.GunEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.ItemEntry;

public enum EquipmentType {
    MAIN_WEAPON(0, GunEntry.class),
    SECONDARY_WEAPON(1, GunEntry.class),
    LETHAL(2, ItemEntry.class),
    TACTICAL(3, ItemEntry.class),
    GADGET(4, ItemEntry.class),
    UTILITY(5, ItemEntry.class),
    CONSUMABLE(6, ItemEntry.class),
    PERK(16, ItemEntry.class),
    ARMOR(36, ArmorEntry.class);

    public final int slot;
    public final Class<? extends CatalogEntry> entryClass;

    EquipmentType(int slot, Class<? extends CatalogEntry> entryClass) {
        this.slot = slot;
        this.entryClass = entryClass;
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
