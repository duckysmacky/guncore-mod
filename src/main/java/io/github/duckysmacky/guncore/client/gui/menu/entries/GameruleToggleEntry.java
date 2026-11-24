package io.github.duckysmacky.guncore.client.gui.menu.entries;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.common.game.CommandExecutor;

/**
 * A toggle button entry for a world gamerule. The server saves the value to settings.json and applies it.
 */
public class GameruleToggleEntry extends ToggleButtonEntry {
    public GameruleToggleEntry(BaseMenu menu, String gamerule) {
        super(menu,
            () -> ClientGameInfo.instance().getGamerule(gamerule),
            (player, state) -> CommandExecutor.execute("game world " + gamerule + " " + state)
        );
    }
}
