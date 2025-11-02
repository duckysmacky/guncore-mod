package io.github.duckysmacky.guncore.data.config.catalog;

import java.util.List;
import java.util.Objects;

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
        this.name = Objects.requireNonNull(name);
        this.descriptionLines = Objects.requireNonNull(descriptionLines);
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
