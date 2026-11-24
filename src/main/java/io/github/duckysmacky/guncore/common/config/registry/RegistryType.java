package io.github.duckysmacky.guncore.common.config.registry;

import java.util.List;

/** One JSON file of a {@link Registry}; implemented by the registry's enum */
public interface RegistryType {
    /** File name, relative to the registry's directory */
    String getJsonFile();

    Class<? extends RegistryEntry> getEntryClass();

    /** Entries written to the file when it doesn't exist yet */
    List<? extends RegistryEntry> createDefaults();
}
