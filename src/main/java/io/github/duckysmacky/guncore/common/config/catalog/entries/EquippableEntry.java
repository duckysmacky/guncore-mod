package io.github.duckysmacky.guncore.common.config.catalog.entries;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface EquippableEntry {
    ItemStack getItemStack();
    List<ItemStack> getAdditionalItemStacks();
}
