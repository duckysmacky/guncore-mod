package io.github.duckysmacky.guncore.common.config.catalog;

import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.config.catalog.items.ItemEntry;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.config.catalog.locations.LocationEntry;

import java.util.function.Supplier;

public enum CatalogType {
    KITS("kits.json", KitEntry.class, KitEntry::createExample),
    KIT_GADGETS("kit-gadgets.json", ItemEntry.class, ItemEntry::createExample),
    PRIMARY_WEAPONS("primary-weapons.json", GunEntry.class, GunEntry::createExample),
    SECONDARY_WEAPONS("secondary-weapons.json", GunEntry.class, GunEntry::createExample),
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