package io.github.duckysmacky.guncore.server.menu.entries;

import io.github.duckysmacky.guncore.common.game.ServerSoundPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ActionEntry extends MenuEntry {
    private final Consumer<ServerPlayer> action;

    public ActionEntry(ItemStack icon, Consumer<ServerPlayer> action) {
        super(icon);
        this.action = action;
    }

    @Override
    public void onClick(ServerPlayer player) {
        ServerSoundPlayer.playFor(player, SoundEvents.UI_BUTTON_CLICK.get(), 1f, 1f);
        action.accept(player);
    }
}