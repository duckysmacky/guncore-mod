package io.github.duckysmacky.guncore.gui.menu.entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class MenuEntry {
    protected ItemStack icon;

    public MenuEntry(ItemStack icon) {
        this.icon = icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public ItemStack getIcon() {
        return icon;
    }

    /**
     * Called when player clicks this entry
     */
    public abstract void onClick(EntityPlayer player);
}