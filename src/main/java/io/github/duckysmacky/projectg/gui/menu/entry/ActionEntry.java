package io.github.duckysmacky.projectg.gui.menu.entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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
        player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
        action.accept(player);
    }
}