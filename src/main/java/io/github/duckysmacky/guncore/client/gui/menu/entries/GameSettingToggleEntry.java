package io.github.duckysmacky.guncore.client.gui.menu.entries;

import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.common.game.ServerBroadcaster;
import io.github.duckysmacky.guncore.common.game.ServerSoundPlayer;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;

/**
 * A toggle button entry for a "/game &lt;subcommand&gt; &lt;true|false&gt;" boolean setting.
 */
public class GameSettingToggleEntry extends ToggleButtonEntry {
    public GameSettingToggleEntry(
        BaseMenu menu,
        boolean baseState,
        String settingName,
        String subcommand
    ) {
        super(menu,
            () -> baseState, // TODO: find a way to actually load the current server-side state into this
            (player, state) -> {
            CommandExecutor.execute("game " + subcommand + " " + state);

            TextFormatting color = state ? TextFormatting.GREEN : TextFormatting.GRAY;
            String stateText = state ? "enabled" : "disabled";
            String message = String.format("&f%s %s%s", settingName, color, stateText);

            ServerBroadcaster.message(message);
            ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
        });
    }
}
