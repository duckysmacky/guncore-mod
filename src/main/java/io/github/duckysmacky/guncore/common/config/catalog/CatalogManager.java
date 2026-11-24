package io.github.duckysmacky.guncore.common.config.catalog;

import io.github.duckysmacky.guncore.common.config.registry.Registry;

import java.util.List;
import java.util.Optional;

/** Registry of everything players can pick (kits, guns, items, locations), stored in `config/guncore/catalog/` */
public class CatalogManager extends Registry<CatalogType> {
    public CatalogManager() {
        super("catalog", "catalog/", CatalogType.class);
    }

    public <T extends CatalogEntry> List<T> getCatalog(CatalogType type) {
        return getEntries(type);
    }

    public <T extends CatalogEntry> Optional<T> findEntry(CatalogType type, String name) {
        List<T> list = getCatalog(type);
        return list.stream().filter(e -> e.getName().equals(name)).findFirst();
    }
}
