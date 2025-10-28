package io.github.duckysmacky.projectg.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

public class SubpageEntry extends MenuEntry {
    private final MenuPage subPage;

    public SubpageEntry(ItemStack icon, MenuPage subPage) {
        super(icon);
        this.subPage = subPage;
    }

    @Override
    public void onClick(EntityPlayer player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
        subPage.open(player);
    }
}