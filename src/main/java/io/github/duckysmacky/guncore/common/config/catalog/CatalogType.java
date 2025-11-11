package io.github.duckysmacky.guncore.common.config.catalog;

import io.github.duckysmacky.guncore.common.config.catalog.entries.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.EquipmentEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.GunEntry;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.LocationEntry;

import java.util.function.Supplier;

public enum CatalogType {
    KITS("kits.json", KitEntry.class, KitEntry::createExample),
    GUNS("guns.json", GunEntry.class, GunEntry::createExample),
    GADGETS("gadgets.json", EquipmentEntry.class, EquipmentEntry::createExample),
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