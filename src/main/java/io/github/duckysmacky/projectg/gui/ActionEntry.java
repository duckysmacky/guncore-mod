package io.github.duckysmacky.projectg.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class ActionEntry extends MenuEntry {
    private final Consumer<EntityPlayer> action;

    public ActionEntry(ItemStack icon, Consumer<EntityPlayer> action) {
        super(icon);
        this.action = action;
    }

    @Override
    public void onClick(EntityPlayer player) {
        action.accept(player);
    }
}