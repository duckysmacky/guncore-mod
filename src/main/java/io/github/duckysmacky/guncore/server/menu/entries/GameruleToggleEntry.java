package io.github.duckysmacky.guncore.server.menu.entries;

import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.common.game.ServerBroadcaster;
import io.github.duckysmacky.guncore.common.game.ServerSoundPlayer;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;

public class GameruleToggleEntry extends ToggleButtonEntry {
    public GameruleToggleEntry(
        BaseMenuPage menu,
        boolean baseState,
        String gameruleName,
        String gamerule
    ) {
        super(menu,
            () -> baseState, // TODO: find a way to actually load gamerule state into this
            (player, state) -> {
            CommandExecutor.execute("gamerule " + gamerule + " " + state);

            ChatFormatting color = state ? ChatFormatting.GREEN : ChatFormatting.GRAY;
            String stateChat = state ? "enabled" : "disabled";
            String message = String.format("&f%s %s%s", gameruleName, color, stateChat);

            ServerBroadcaster.message(message);
            ServerSoundPlayer.playForAll(SoundEvents.NOTE_BLOCK_HARP.get(), 1f, 1f);
        });
    }
}
