package io.github.duckysmacky.projectg.config.catalog;

public abstract class CatalogEntry {
    private final boolean enabled;
    protected final String displayName;
    protected final String displayItemId;

    public CatalogEntry(
        boolean enabled,
        String displayItemId,
        String displayName
    ) {
        this.enabled = enabled;
        this.displayItemId = displayItemId;
        this.displayName = displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDisplayName() {
        return displayName;
    }
}
