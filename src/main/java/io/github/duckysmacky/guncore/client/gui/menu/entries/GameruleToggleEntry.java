package io.github.duckysmacky.guncore.client.gui.menu.entries;

import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.common.game.ServerBroadcaster;
import io.github.duckysmacky.guncore.common.game.ServerSoundPlayer;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;

public class GameruleToggleEntry extends ToggleButtonEntry {
    public GameruleToggleEntry(
        BaseMenu menu,
        boolean baseState,
        String gameruleName,
        String gamerule
    ) {
        super(menu,
            () -> baseState, // TODO: find a way to actually load gamerule state into this
            (player, state) -> {
            CommandExecutor.execute("gamerule " + gamerule + " " + state);

            TextFormatting color = state ? TextFormatting.GREEN : TextFormatting.GRAY;
            String stateText = state ? "enabled" : "disabled";
            String message = String.format("&f%s %s%s", gameruleName, color, stateText);

            ServerBroadcaster.message(message);
            ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
        });
    }
}
