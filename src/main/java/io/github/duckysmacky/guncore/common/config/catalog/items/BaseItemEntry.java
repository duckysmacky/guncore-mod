package io.github.duckysmacky.guncore.common.config.catalog.items;

import io.github.duckysmacky.guncore.common.config.catalog.Rarity;

import java.util.Collections;

/** Item every build-a-class loadout starts with; always common, additional items are ignored (one item per slot) */
public class BaseItemEntry extends ItemEntry {
    public BaseItemEntry(boolean enabled, String name, String itemId, int itemAmount) {
        super(enabled, name, Rarity.COMMON, itemId, itemAmount, 0, null, Collections.emptyList(), Collections.emptyList());
    }

    public static BaseItemEntry createExample() {
        return new BaseItemEntry(true, "Cooked Beef", "minecraft:cooked_beef", 64);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }
}
