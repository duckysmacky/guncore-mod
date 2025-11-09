package io.github.duckysmacky.guncore.common.config.catalog;

import io.github.duckysmacky.guncore.common.config.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.config.catalog.locations.LocationEntry;

import java.util.function.Supplier;

public enum CatalogType {
    KITS("kits.json", KitEntry.class, KitEntry::createExample),
    GUNS("guns.json", GunEntry.class, GunEntry::createExample),
    GADGETS("gadgets.json", GadgetEntry.class, GadgetEntry::createExample),
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