package io.github.duckysmacky.guncore.client.gui.hud;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class HudOverlay {
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        ClientGameInfo game = ClientGameInfo.instance();

        String modeText = String.format("%s (%s)", game.getGameMode().display, game.getGameModeVariant().display);
        event.getLeft().add(modeText);

        String statusText = String.format("Status: %s", game.getGameState().display);
        event.getLeft().add(statusText);

        if (game.getGameState() != GameState.NOT_STARTED && game.getGameState() != GameState.ENDED) {
            long timeElapsed = System.currentTimeMillis() - game.getRoundStartTime();

            if (game.getGameModeVariant() == GameMode.Variant.TIME) {
                int timeLeftSecs = (int) (game.getRoundLengthSec() - (timeElapsed / 1000L));

                String timeText = String.format("Time Left: %s", TextUtils.formatTime(timeLeftSecs));
                event.getLeft().add(timeText);
            } else {
                String timeText = String.format("Round time: %s", TextUtils.formatTime((int) (timeElapsed / 1000L)));
                event.getLeft().add(timeText);
            }
        }

        PlayerStats stats = game.getPlayerStats();
        event.getRight().add("Kills: " + stats.getKills());
        event.getRight().add("Deaths: " + stats.getDeaths());

        if (game.getGameModeVariant() == GameMode.Variant.LIVES)
            event.getRight().add("Lives: " + stats.getLives());
    }
}