package io.github.duckysmacky.guncore.common.game;

import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;

/** Equipment slots; the same inventory slots are used by kit-based and build-a-class modes */
public enum EquipmentType {
    MAIN_WEAPON("Main weapon", 0, CatalogType.PRIMARY_WEAPONS),
    SECONDARY_WEAPON("Secondary weapon", 1, CatalogType.SECONDARY_WEAPONS),
    LETHAL("Lethal", 2, CatalogType.LETHALS),
    TACTICAL("Tactical", 3, CatalogType.TACTICALS),
    GADGET("Gadget", 4, CatalogType.GADGETS),
    UTILITY_1("Utility 1", 5, CatalogType.UTILITY),
    UTILITY_2("Utility 2", 6, CatalogType.UTILITY),
    CONSUMABLE("Consumable", 7, CatalogType.CONSUMABLES),
    ARMOR("Armor", -1, CatalogType.ARMOR);

    public final String display;
    /** Hotbar slot of the main item; extra items go to this slot + 27, then up 9 slots at a time */
    public final int slot;
    public final CatalogType catalog;

    EquipmentType(String display, int slot, CatalogType catalog) {
        this.display = display;
        this.slot = slot;
        this.catalog = catalog;
    }
}
