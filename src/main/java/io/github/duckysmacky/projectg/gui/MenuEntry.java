package io.github.duckysmacky.projectg.gui;

import net.minecraft.entity.player.EntityPlayer;

public abstract class MenuEntry {
    protected MenuIcon icon;

    public MenuEntry(MenuIcon icon) {
        this.icon = icon;
    }

    public void setIcon(MenuIcon icon) {
        this.icon = icon;
    }

    public MenuIcon getIcon() {
        return icon;
    }

    /**
     * Called when player clicks this entry
     */
    public abstract void onClick(EntityPlayer player);
}