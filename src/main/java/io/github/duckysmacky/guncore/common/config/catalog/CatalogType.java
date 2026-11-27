package io.github.duckysmacky.guncore.common.config.catalog;

import io.github.duckysmacky.guncore.common.config.catalog.armor.ArmorEntry;
import io.github.duckysmacky.guncore.common.config.catalog.attachments.AttachmentEntry;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.config.catalog.items.BaseItemEntry;
import io.github.duckysmacky.guncore.common.config.catalog.items.ItemEntry;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.config.catalog.locations.LocationEntry;

import io.github.duckysmacky.guncore.common.config.registry.RegistryEntry;
import io.github.duckysmacky.guncore.common.config.registry.RegistryType;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public enum CatalogType implements RegistryType {
    KITS("kits.json", KitEntry.class, KitEntry::createExample),
    KIT_GADGETS("kit-gadgets.json", ItemEntry.class, ItemEntry::createExample),
    PRIMARY_WEAPONS("primary-weapons.json", GunEntry.class, GunEntry::createExample),
    SECONDARY_WEAPONS("secondary-weapons.json", GunEntry.class, GunEntry::createExample),
    LETHALS("lethals.json", ItemEntry.class, ItemEntry::createExample),
    TACTICALS("tacticals.json", ItemEntry.class, ItemEntry::createExample),
    GADGETS("gadgets.json", ItemEntry.class, ItemEntry::createExample),
    UTILITY("utility.json", ItemEntry.class, ItemEntry::createExample),
    CONSUMABLES("consumables.json", ItemEntry.class, ItemEntry::createExample),
    BASE_ITEMS("base-items.json", BaseItemEntry.class, BaseItemEntry::createExample),
    ARMOR("armor.json", ArmorEntry.class, ArmorEntry::createExample),
    LOCATIONS("locations.json", LocationEntry.class, LocationEntry::createExample),
    ATTACHMENTS("attachments.json", AttachmentEntry.class, AttachmentEntry::createExample);

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

    @Override
    public String getJsonFile() {
        return jsonFile;
    }

    @Override
    public Class<? extends RegistryEntry> getEntryClass() {
        return entryClass;
    }

    @Override
    public List<? extends RegistryEntry> createDefaults() {
        return Collections.singletonList(exampleSupplier.get());
    }
}
