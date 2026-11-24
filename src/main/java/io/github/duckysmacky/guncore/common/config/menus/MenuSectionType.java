package io.github.duckysmacky.guncore.common.config.menus;

import io.github.duckysmacky.guncore.common.config.registry.RegistryEntry;
import io.github.duckysmacky.guncore.common.config.registry.RegistryType;

import java.util.List;
import java.util.function.Supplier;

public enum MenuSectionType implements RegistryType {
    GUN_CATEGORIES("gun_categories.json", MenuSectionEntry::defaultGunCategories),
    KIT_CLASSES("kit_classes.json", MenuSectionEntry::defaultKitClasses),
    MAPS("maps.json", MenuSectionEntry::defaultMaps);

    public final String jsonFile;
    private final Supplier<List<MenuSectionEntry>> defaultsSupplier;

    MenuSectionType(String jsonFile, Supplier<List<MenuSectionEntry>> defaultsSupplier) {
        this.jsonFile = jsonFile;
        this.defaultsSupplier = defaultsSupplier;
    }

    @Override
    public String getJsonFile() {
        return jsonFile;
    }

    @Override
    public Class<? extends RegistryEntry> getEntryClass() {
        return MenuSectionEntry.class;
    }

    @Override
    public List<? extends RegistryEntry> createDefaults() {
        return defaultsSupplier.get();
    }
}
