package io.github.duckysmacky.guncore.client.gui.hud;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.game.Team;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.*;

public class SidebarHud {
    private static final int PADDING = 3;
    private static final int RIGHT_MARGIN = 2;
    private static final int BACKGROUND_COLOR = 0x50000000;
    private static final int TITLE_BACKGROUND_COLOR = 0x70000000;
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.player == null || mc.gameSettings.hideGUI)
            return;
        if (mc.gameSettings.keyBindPlayerList.isKeyDown())
            return;

        ClientGameInfo game = ClientGameInfo.instance();
        String title = "" + GOLD + BOLD + game.getGameMode().display;
        List<String> lines = buildLines(game);

        FontRenderer fr = mc.fontRenderer;
        int lineHeight = fr.FONT_HEIGHT;

        int width = fr.getStringWidth(title);
        for (String line : lines)
            width = Math.max(width, fr.getStringWidth(line));
        width += PADDING * 2;

        int titleHeight = lineHeight + PADDING;
        int height = titleHeight + lines.size() * lineHeight + PADDING;

        int right = event.getResolution().getScaledWidth() - RIGHT_MARGIN;
        int left = right - width;
        int top = (event.getResolution().getScaledHeight() - height) / 2;

        Gui.drawRect(left, top, right, top + titleHeight, TITLE_BACKGROUND_COLOR);
        Gui.drawRect(left, top + titleHeight, right, top + height, BACKGROUND_COLOR);

        fr.drawStringWithShadow(title, left + (width - fr.getStringWidth(title)) / 2f, top + PADDING / 2f + 1, 0xFFFFFF);

        int y = top + titleHeight + 1;
        for (String line : lines) {
            fr.drawStringWithShadow(line, left + PADDING, y, 0xFFFFFF);
            y += lineHeight;
        }
    }

    private List<String> buildLines(ClientGameInfo game) {
        List<String> lines = new ArrayList<>();
        GameMode.Variant variant = game.getGameModeVariant();
        PlayerStats stats = game.getOwnStats();

        lines.add(GRAY + "Status: " + game.stateColor() + game.getGameState().display);
        lines.add(GRAY + "Goal: " + WHITE + game.goalText());

        if (game.hasTime())
            lines.add(GRAY + game.timeLabel() + ": " + WHITE + game.timeValue());

        lines.add("");

        if (game.isTeamMode()) {
            Team team = game.getOwnTeam();
            lines.add(GRAY + "Team: " + team.color + team.display);
        }

        if (variant == GameMode.Variant.KILLS && !game.isTeamMode())
            lines.add(GRAY + "Kills: " + WHITE + stats.getKills() + GRAY + "/" + game.getKillTarget());
        else
            lines.add(GRAY + "Kills: " + WHITE + stats.getKills());

        if (variant == GameMode.Variant.KILLS && game.isTeamMode())
            lines.add(GRAY + "Team kills: " + WHITE + game.getTeamKills(game.getOwnTeam()) + GRAY + "/" + game.getKillTarget());

        lines.add(GRAY + "Deaths: " + WHITE + stats.getDeaths());

        if (variant == GameMode.Variant.LIVES)
            lines.add(GRAY + "Lives: " + WHITE + stats.getLives());

        return lines;
    }
}
