package io.github.duckysmacky.guncore.common.config.registry;

/** Single entry of a {@link Registry} JSON file */
public interface RegistryEntry {
    /** Disabled entries are dropped on load */
    boolean isEnabled();
}
