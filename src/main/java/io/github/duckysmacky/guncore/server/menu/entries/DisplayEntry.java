package io.github.duckysmacky.guncore.server.menu.entries;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class DisplayEntry extends MenuEntry {
    public DisplayEntry(ItemStack icon) {
        super(icon);
    }

    @Override
    public void onClick(ServerPlayer player) {
        // do nothing
    }
}