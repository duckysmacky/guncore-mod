package io.github.duckysmacky.guncore.client.gui.hud;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GuncoreMod.MOD_ID, value = Dist.CLIENT)
public class StatsHudOverlay {
    private static final int TOP_OFFSET = 27;
    private static final int BOTTOM_OFFSET = 70;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (MC.player == null || MC.options.hideGui)
            return;

        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();

        ClientGameInfo game = ClientGameInfo.instance();

        drawRoundInfo(event.getGuiGraphics(), game, screenWidth);
        drawPlayerStats(event.getGuiGraphics(), game, screenWidth, screenHeight);
    }

    private static void drawRoundInfo(GuiGraphics graphics, ClientGameInfo game, int screenWidth) {
        String modeText = String.format(
            "%s (%s): %s",
            game.getGameMode().display, game.getGameModeVariant().display, game.getGameState().display
        );
        String timeText = getTimeText(game);

        int centerX = screenWidth / 2;
        Font font = MC.font;

        drawCentered(graphics, font, timeText, centerX, TOP_OFFSET - 10, TEXT_COLOR);
    }

    private static String getTimeText(ClientGameInfo game) {
        String timeText = "";
        if (game.getGameState() != GameState.NOT_STARTED && game.getGameState() != GameState.ENDED) {
            if (game.getGameModeVariant() == GameMode.Variant.TIME) {
                int timeLeftSec = game.getRoundLengthSec() - game.getRoundDurationSec();
                timeText = String.format("Time Left: %s", TextUtils.formatTime(timeLeftSec));
            } else {
                timeText = String.format("Round time: %s", TextUtils.formatTime(game.getRoundDurationSec()));
            }
        }
        return timeText;
    }

    private static void drawPlayerStats(GuiGraphics graphics, ClientGameInfo game, int screenWidth, int screenHeight) {
        PlayerStats stats = game.getPlayerStats();
        Font font = MC.font;

        int y = screenHeight - BOTTOM_OFFSET;
        int centerX = screenWidth / 2;

        String killsText = "Kills: " + stats.getKills();
        String deathsText = "Deaths: " + stats.getDeaths();
        String livesText = game.getGameModeVariant() == GameMode.Variant.LIVES ? "Lives: " + stats.getLives() : "";

        int spacing = 8;
        int totalWidth = font.width(killsText)
            + font.width(deathsText)
            + (livesText.isEmpty() ? 0 : font.width(livesText))
            + spacing * 2;

        int startX = centerX - totalWidth / 2;

        graphics.drawString(font, killsText, startX, y, TEXT_COLOR, true);
        String previousText = killsText;

        if (!livesText.isEmpty()) {
            startX += font.width(previousText) + spacing;
            graphics.drawString(font, livesText, startX, y, TEXT_COLOR, true);
            previousText = livesText;
        }

        startX += font.width(previousText) + spacing;
        graphics.drawString(font, deathsText, startX, y, TEXT_COLOR, true);
    }

    private static void drawCentered(GuiGraphics graphics, Font font, String text, int x, int y, int color) {
        if (text == null || text.isEmpty()) return;
        graphics.drawString(font, text, (int)(x - font.width(text) / 2f), y, color, true);
    }
}