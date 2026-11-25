package io.github.duckysmacky.guncore.common.util;

import io.github.duckysmacky.guncore.GuncoreMod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

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

    public static void applyNbt(ItemStack item, String registryName, String nbtData) {
        if (nbtData == null || nbtData.trim().isEmpty()) return;

        try {
            item.setTagCompound(JsonToNBT.getTagFromJson(nbtData));
        } catch (NBTException e) {
            GuncoreMod.LOGGER.error("Invalid NBT '{}' for item {}: {}", nbtData, registryName, e.getMessage());
        }
    }

    public static Item findItem(String registryName) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
    }

    public static Block findBlock(String registryName) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
    }
}
