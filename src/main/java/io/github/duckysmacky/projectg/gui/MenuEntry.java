package io.github.duckysmacky.projectg.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class MenuEntry {
    protected final ItemStack itemIcon;

    public MenuEntry(ItemStack itemIcon) {
        this.itemIcon = itemIcon;
    }

    public ItemStack getItemIcon() {
        return itemIcon;
    }

    /**
     * Called when player clicks this entry
     */
    public abstract void onClick(EntityPlayer player);
}