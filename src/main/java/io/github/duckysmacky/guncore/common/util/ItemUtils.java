package io.github.duckysmacky.guncore.common.util;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemUtils {
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

    public static ItemStack placeholderItem() {
        return new ItemStack(Blocks.DIRT);
    }

    /**
     * Creates a TACZ gun ItemStack with the specified gun ID
     * @param gunId The gun ID (e.g., "tacz:p320", "tacz:ak47")
     * @return ItemStack of the TACZ gun with proper NBT data
     */
    public static ItemStack getTACZGun(String gunId) {
        return getTACZGun(gunId, "SEMI");
    }

    /**
     * Creates a TACZ gun ItemStack with the specified gun ID and fire mode
     * @param gunId The gun ID (e.g., "tacz:p320", "tacz:ak47")
     * @param fireMode The fire mode (e.g., "SEMI", "AUTO", "BURST")
     * @return ItemStack of the TACZ gun with proper NBT data
     */
    public static ItemStack getTACZGun(String gunId, String fireMode) {
        Item gunItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("tacz", "modern_kinetic_gun"));

        if (gunItem == null || gunItem == Items.AIR) {
            return placeholderItem();
        }

        ItemStack gunStack = new ItemStack(gunItem);

        CompoundTag nbt = new CompoundTag();
        nbt.putString("GunId", gunId);
        nbt.putString("GunFireMode", fireMode);

        gunStack.setTag(nbt);

        return gunStack;
    }

    /**
     * Creates a TACZ ammo ItemStack with the specified ammo ID and count
     * @param ammoId The ammo ID (e.g., "tacz:9mm", "tacz:556x45")
     * @param count The stack size
     * @return ItemStack of the TACZ ammo with proper NBT data
     */
    public static ItemStack getTACZAmmo(String ammoId, int count) {
        // Get the base ammo item
        Item ammoItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("tacz", "ammo"));

        if (ammoItem == null || ammoItem == Items.AIR) {
            // Fallback in case the item doesn't exist
            return placeholderItem();
        }

        ItemStack ammoStack = new ItemStack(ammoItem, count);

        // Create the NBT data
        CompoundTag nbt = new CompoundTag();
        nbt.putString("AmmoId", ammoId);

        // Apply the NBT to the item stack
        ammoStack.setTag(nbt);

        return ammoStack;
    }
}
