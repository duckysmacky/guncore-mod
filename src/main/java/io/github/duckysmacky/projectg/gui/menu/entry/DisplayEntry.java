package io.github.duckysmacky.projectg.gui.menu.entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class DisplayEntry extends MenuEntry {
    public DisplayEntry(ItemStack icon) {
        super(icon);
    }

    @Override
    public void onClick(EntityPlayer player) {
        // do nothing
    }
}