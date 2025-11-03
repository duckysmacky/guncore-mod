package io.github.duckysmacky.guncore.gui.menu.entry;

import io.github.duckysmacky.guncore.game.CommandExecutor;
import io.github.duckysmacky.guncore.game.ServerBroadcaster;
import io.github.duckysmacky.guncore.game.ServerSoundPlayer;
import io.github.duckysmacky.guncore.gui.menu.BaseMenu;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;

public class GameruleToggleEntry extends ToggleButtonEntry {
    public GameruleToggleEntry(
        BaseMenu menu,
        String gameruleName,
        String gamerule
    ) {
        super(menu,
            () -> false, // TODO: find a way to actually load gamerule state into this
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
