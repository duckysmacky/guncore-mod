package io.github.duckysmacky.guncore.server.menu.entries;

import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class SubpageEntry extends MenuEntry {
    private final BaseMenuPage subPage;

    public SubpageEntry(ItemStack icon, BaseMenuPage subPage) {
        super(icon);
        this.subPage = subPage;
    }

    @Override
    public void onClick(ServerPlayer player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 1.0f, 1.0f);
        subPage.open(player);
    }
}