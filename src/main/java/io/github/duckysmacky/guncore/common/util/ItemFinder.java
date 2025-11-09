package io.github.duckysmacky.guncore.common.util;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemFinder {
    public static ItemStack findItemStack(String registryName) {
        Item item = findItem(registryName);

        if (item != null && item != Items.AIR) {
            return new ItemStack(item);
        }

        Block block = findBlock(registryName);

        if (block != null && block != Blocks.AIR) {
            return new ItemStack(block);
        }

        return ItemStack.EMPTY;
    }

    public static Item findItem(String registryName) {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(registryName));
    }

    public static Block findBlock(String registryName) {
        return ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(registryName));
    }
}
