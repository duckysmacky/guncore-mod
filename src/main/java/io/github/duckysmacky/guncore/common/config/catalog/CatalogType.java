package io.github.duckysmacky.guncore.common.config.catalog;

import io.github.duckysmacky.guncore.common.config.catalog.entries.*;

import java.util.function.Supplier;

public enum CatalogType {
    MAIN_WEAPONS("main-weapons.json", GunEntry.class, GunEntry::createExample),
    SECONDARY_WEAPONS("secondary-weapons.json", GunEntry.class, GunEntry::createExample),
    LETHALS("lethals.json", ItemEntry.class, ItemEntry::createExample),
    TACTICALS("tacticals.json", ItemEntry.class, ItemEntry::createExample),
    GADGETS("gadgets.json", ItemEntry.class, ItemEntry::createExample),
    UTILITY("utility.json", ItemEntry.class, ItemEntry::createExample),
    CONSUMABLES("consumables.json", ItemEntry.class, ItemEntry::createExample),
    PERKS("perks.json", ItemEntry.class, ItemEntry::createExample),
    ARMOR("armor.json", ArmorEntry.class, ArmorEntry::createExample),
    LOCATIONS("locations.json", LocationEntry.class, LocationEntry::createExample);

    public final String jsonFile;
    public final Class<? extends CatalogEntry> entryClass;
    public final Supplier<? extends CatalogEntry> exampleSupplier;

    CatalogType(
        String jsonFile,
        Class<? extends CatalogEntry> entryClass,
        Supplier<? extends CatalogEntry> exampleSupplier
    ) {
        this.jsonFile = jsonFile;
        this.entryClass = entryClass;
        this.exampleSupplier = exampleSupplier;
    }
}