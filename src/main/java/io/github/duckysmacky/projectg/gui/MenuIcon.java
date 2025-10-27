package io.github.duckysmacky.projectg.gui;

import io.github.duckysmacky.projectg.util.ItemFinder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class MenuIcon {
    public static final String SIGN_CODE = "§";
    public static final String AMPERSAND = "&";

    private ItemStack item;
    private NBTTagCompound display;
    private NBTTagList loreList;

    public MenuIcon(ItemStack item) {
        this.item = item;
        this.display = new NBTTagCompound();
        this.loreList = new NBTTagList();
    }

    public static MenuIcon fromItem(String registryName) {
        return new MenuIcon(new ItemStack(ItemFinder.findItem(registryName)));
    }

    public static MenuIcon fromBlock(String registryName) {
        return new MenuIcon(new ItemStack(ItemFinder.findBlock(registryName)));
    }

    public MenuIcon setName(String name) {
        String translated = name.replace(AMPERSAND, SIGN_CODE);
        display.setString("Name", translated);
        return this;
    }

    public MenuIcon addLoreLine(String line) {
        String translated = line.replace(AMPERSAND, SIGN_CODE);
        loreList.appendTag(new NBTTagString(translated));
        return this;
    }

    public ItemStack getItemStack() {
        display.setTag("Lore", loreList);
        item.setTagInfo("display", display);
        return item;
    }
}
