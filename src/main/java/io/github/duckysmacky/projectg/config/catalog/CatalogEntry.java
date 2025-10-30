package io.github.duckysmacky.projectg.config.catalog;

import java.util.List;

public abstract class CatalogEntry {
    protected final boolean enabled;
    protected final String name;
    protected final List<String> descriptionLines;

    public CatalogEntry(
        boolean enabled,
        String name,
        List<String> descriptionLines
    ) {
        this.enabled = enabled;
        this.name = name;
        this.descriptionLines = descriptionLines;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescriptionLines() {
        return descriptionLines;
    }
}
