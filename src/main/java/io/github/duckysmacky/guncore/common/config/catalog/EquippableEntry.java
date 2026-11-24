package io.github.duckysmacky.guncore.common.config.catalog;

import net.minecraft.item.ItemStack;

/** Catalog entry which can be picked for an equipment slot */
public interface EquippableEntry {
    String getName();

    Rarity getRarity();

    /** Item to show in the selection menu */
    ItemStack getIcon();
}
