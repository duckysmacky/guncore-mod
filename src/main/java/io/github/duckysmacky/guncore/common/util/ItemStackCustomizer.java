package io.github.duckysmacky.guncore.common.util;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import static io.github.duckysmacky.guncore.common.util.TextUtils.translateColorCodes;

public class ItemStackCustomizer {
    private net.minecraft.item.ItemStack item;
    private NBTTagCompound display;
    private NBTTagList loreList;

    public ItemStackCustomizer(net.minecraft.item.ItemStack item) {
        if (item != null && item != net.minecraft.item.ItemStack.EMPTY) {
            this.item = item;
        } else {
            this.item = new net.minecraft.item.ItemStack(Blocks.DIRT);
        }
        this.display = new NBTTagCompound();
        this.loreList = new NBTTagList();
    }

    public static ItemStackCustomizer from(String registryName) {
        return new ItemStackCustomizer(ItemFinder.findItemStack(registryName));
    }

    public ItemStackCustomizer setName(String name) {
        display.setString("Name", translateColorCodes(name));
        return this;
    }

    public ItemStackCustomizer addLoreLine(String line) {
        loreList.appendTag(new NBTTagString(translateColorCodes(line)));
        return this;
    }

    public ItemStackCustomizer resetLore() {
        loreList = new NBTTagList();
        return this;
    }

    public net.minecraft.item.ItemStack getItemStack() {
        display.setTag("Lore", loreList);
        item.setTagInfo("display", display);
        return item;
    }
}
