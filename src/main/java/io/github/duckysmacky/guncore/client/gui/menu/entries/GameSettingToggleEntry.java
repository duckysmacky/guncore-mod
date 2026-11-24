package io.github.duckysmacky.guncore.client.gui.menu.entries;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.common.game.CommandExecutor;

import java.util.function.Supplier;

/**
 * A toggle button entry for a "/game &lt;subcommand&gt; &lt;true|false&gt;" boolean setting.
 */
public class GameSettingToggleEntry extends ToggleButtonEntry {
    public GameSettingToggleEntry(BaseMenu menu, Supplier<Boolean> stateSupplier, String subcommand) {
        super(menu,
            stateSupplier,
            (player, state) -> CommandExecutor.execute("game " + subcommand + " " + state)
        );
    }
}
