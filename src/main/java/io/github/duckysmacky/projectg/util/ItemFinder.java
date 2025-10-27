package io.github.duckysmacky.projectg.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemFinder {
    public static Item findItem(String registryName) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
    }

    public static Block findBlock(String registryName) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
    }
}
