package io.github.duckysmacky.guncore.server.menu.entries;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

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
    public abstract void onClick(ServerPlayer player);
}