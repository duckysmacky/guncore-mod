package io.github.duckysmacky.guncore.common.config.catalog.entries;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ItemEntry {
    ItemStack getItemStack();
    List<ItemStack> getAdditionalItemStacks();
}
