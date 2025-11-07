package io.github.duckysmacky.guncore.client.gui.hud;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class HudOverlay {
    private static final int TOP_OFFSET = 27;
    private static final int BOTTOM_OFFSET = 70;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.player == null)
            return;

        ScaledResolution res = new ScaledResolution(mc);
        ClientGameInfo game = ClientGameInfo.instance();

        drawRoundInfo(game, res);
        drawPlayerStats(game, res);
    }

    private void drawRoundInfo(ClientGameInfo game, ScaledResolution res) {
        String modeText = String.format("%s (%s): %s",
            game.getGameMode().display, game.getGameModeVariant().display, game.getGameState().display);

        String timeText = "";
        if (game.getGameState() != GameState.NOT_STARTED && game.getGameState() != GameState.ENDED) {
            if (game.getGameModeVariant() == GameMode.Variant.TIME) {
                int timeLeftSec = game.getRoundLengthSec() - game.getRoundDurationSec();
                timeText = String.format("Time Left: %s", TextUtils.formatTime(timeLeftSec));
            } else {
                timeText = String.format("Round time: %s", TextUtils.formatTime(game.getRoundDurationSec()));
            }
        }

        int centerX = res.getScaledWidth() / 2;
        int y = TOP_OFFSET;

        //drawCentered(mc.fontRenderer, modeText, centerX, y, TEXT_COLOR);
        drawCentered(mc.fontRenderer, timeText, centerX, y, TEXT_COLOR);
    }

    private void drawPlayerStats(ClientGameInfo game, ScaledResolution res) {
        PlayerStats stats = game.getPlayerStats();

        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        int y = screenHeight - BOTTOM_OFFSET;
        int centerX = screenWidth / 2;

        String killsText = "Kills: " + stats.getKills();
        String deathsText = "Deaths: " + stats.getDeaths();
        String livesText = game.getGameModeVariant() == GameMode.Variant.LIVES ? "Lives: " + stats.getLives() : "";

        FontRenderer fr = mc.fontRenderer;
        int spacing = 8;
        int totalWidth = fr.getStringWidth(killsText)
            + fr.getStringWidth(deathsText)
            + (livesText.isEmpty() ? 0 : fr.getStringWidth(livesText))
            + spacing * 2;

        int startX = centerX - totalWidth / 2;

        fr.drawStringWithShadow(killsText, startX, y, TEXT_COLOR);
        String previousText = killsText;

        if (!livesText.isEmpty()) {
            startX += fr.getStringWidth(previousText) + spacing;
            fr.drawStringWithShadow(livesText, startX, y, TEXT_COLOR);
            previousText = livesText;
        }

        startX += fr.getStringWidth(previousText) + spacing;
        fr.drawStringWithShadow(deathsText, startX, y, TEXT_COLOR);
    }

    private void drawCentered(FontRenderer fr, String text, int x, int y, int color) {
        fr.drawStringWithShadow(text, x - fr.getStringWidth(text) / 2f, y, color);
    }
}