package io.github.duckysmacky.guncore.client.gui.hud;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.game.Team;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Replaces the vanilla TAB player list with a game scoreboard.
 * Drawn on Post(ALL) instead of Pre(PLAYER_LIST), since the latter doesn't fire in singleplayer.
 */
public class ScoreboardTabOverlay {
    private static final int TOP = 10;
    private static final int PADDING = 4;
    private static final int ROW_HEIGHT = 9;
    private static final int COLUMN_GAP = 8;
    private static final int HEAD_SIZE = 8;
    private static final int PANEL_COLOR = 0x80000000;
    private static final int ROW_COLOR = 0x20FFFFFF;
    private static final int OWN_ROW_COLOR = 0x40FFFFFF;
    private static final int TEAM_HEADER_ALPHA = 0x50;

    private static final int RANK = 0, NAME = 1, KILLS = 2, DEATHS = 3, LIVES = 4, PING = 5;
    private static final int COLUMNS = 6;

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.PLAYER_LIST)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.player == null || mc.getConnection() == null)
            return;
        if (mc.gameSettings.hideGUI || !mc.gameSettings.keyBindPlayerList.isKeyDown())
            return;

        draw(event.getResolution());
    }

    private void draw(ScaledResolution res) {
        ClientGameInfo game = ClientGameInfo.instance();
        FontRenderer fr = mc.fontRenderer;

        boolean[] visible = {
            !game.isTeamMode(), true, true, true,
            game.getGameModeVariant() == GameMode.Variant.LIVES, true
        };

        List<Row> rows = mc.getConnection().getPlayerInfoMap().stream()
            .map(info -> new Row(info, game))
            .collect(Collectors.toList());

        List<Line> lines = game.isTeamMode() ? teamLines(game, rows) : ffaLines(game, rows);

        int maxLines = Math.max(1, (res.getScaledHeight() - TOP - PADDING * 2 - ROW_HEIGHT * 3 - 4) / ROW_HEIGHT);
        if (lines.size() > maxLines) {
            int hidden = lines.size() - (maxLines - 1);
            lines = new ArrayList<>(lines.subList(0, maxLines - 1));
            lines.add(Line.text(GRAY + "... and " + hidden + " more", 0));
        }

        Line columnHeader = new Line(new String[]{
            GRAY + "#", GRAY + "Player", GRAY + "K", GRAY + "D", GRAY + "L", GRAY + "Ping"
        }, 0, null);

        String title = "" + GOLD + BOLD + game.getGameMode().display;
        String info = GRAY + "Status: " + game.stateColor() + game.getGameState().display
            + GRAY + "   Goal: " + WHITE + game.goalText()
            + (game.hasTime() ? GRAY + "   " + game.timeLabel() + ": " + WHITE + game.timeValue() : "");

        int[] widths = new int[COLUMNS];
        measure(fr, columnHeader, visible, widths);
        for (Line line : lines)
            measure(fr, line, visible, widths);

        int tableWidth = -COLUMN_GAP;
        for (int c = 0; c < COLUMNS; c++)
            if (visible[c]) tableWidth += widths[c] + COLUMN_GAP;

        int innerWidth = Math.max(tableWidth, Math.max(fr.getStringWidth(title), fr.getStringWidth(info)));
        int left = (res.getScaledWidth() - innerWidth) / 2 - PADDING;
        int innerLeft = left + PADDING;
        int height = PADDING * 2 + ROW_HEIGHT * 2 + 4 + ROW_HEIGHT * (lines.size() + 1);

        Gui.drawRect(left, TOP, innerLeft + innerWidth + PADDING, TOP + height, PANEL_COLOR);

        int y = TOP + PADDING;
        drawCentered(fr, title, innerLeft + innerWidth / 2, y);
        y += ROW_HEIGHT;
        drawCentered(fr, info, innerLeft + innerWidth / 2, y);
        y += ROW_HEIGHT + 4;

        int tableLeft = innerLeft + (innerWidth - tableWidth) / 2;
        drawLine(fr, columnHeader, tableLeft, y, visible, widths);
        y += ROW_HEIGHT;

        for (Line line : lines) {
            if (line.background != 0)
                Gui.drawRect(innerLeft, y, innerLeft + innerWidth, y + ROW_HEIGHT - 1, line.background);

            drawLine(fr, line, tableLeft, y, visible, widths);
            y += ROW_HEIGHT;
        }
    }

    private List<Line> ffaLines(ClientGameInfo game, List<Row> rows) {
        rows.sort(Comparator.comparing(r -> r.stats, PlayerStats.RANKING));

        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++)
            lines.add(playerLine(game, rows.get(i), (i + 1) + "."));

        return lines;
    }

    private List<Line> teamLines(ClientGameInfo game, List<Row> rows) {
        boolean lives = game.getGameModeVariant() == GameMode.Variant.LIVES;

        Map<Team, List<Row>> byTeam = new EnumMap<>(Team.class);
        rows.forEach(r -> byTeam.computeIfAbsent(r.team, t -> new ArrayList<>()).add(r));

        Comparator<Team> order = Comparator.comparing((Team t) -> t == Team.NONE);
        if (lives)
            order = order.thenComparing(t -> aliveCount(byTeam.get(t)), Comparator.<Integer>reverseOrder());
        order = order.thenComparing(game::getTeamKills, Comparator.<Integer>reverseOrder());

        List<Line> lines = new ArrayList<>();
        byTeam.keySet().stream().sorted(order).forEach(team -> {
            List<Row> members = byTeam.get(team);
            members.sort(Comparator.comparing(r -> r.stats, PlayerStats.RANKING));

            String name = team == Team.NONE ? "No team" : team.display + " Team";
            int rgb = mc.fontRenderer.getColorCode(team.color.toString().charAt(1));

            lines.add(new Line(new String[]{
                "",
                "" + team.color + BOLD + name,
                WHITE.toString() + game.getTeamKills(team),
                WHITE.toString() + game.getTeamTotal(team, PlayerStats::getDeaths),
                lives ? WHITE.toString() + aliveCount(members) + GRAY + "/" + members.size() : "",
                ""
            }, (TEAM_HEADER_ALPHA << 24) | (rgb & 0xFFFFFF), null));

            members.forEach(r -> lines.add(playerLine(game, r, "")));
        });

        return lines;
    }

    private Line playerLine(ClientGameInfo game, Row row, String rank) {
        boolean eliminated = game.getGameModeVariant() == GameMode.Variant.LIVES
            && game.getGameState() != GameState.NOT_STARTED
            && row.stats.getLives() <= 0;

        String nameStyle = eliminated
            ? "" + GRAY + STRIKETHROUGH
            : (game.isTeamMode() ? row.team.color.toString() : WHITE.toString());

        boolean own = row.uuid.equals(mc.player.getUniqueID());

        return new Line(new String[]{
            GRAY + rank,
            nameStyle + row.name,
            WHITE.toString() + row.stats.getKills(),
            WHITE.toString() + row.stats.getDeaths(),
            WHITE.toString() + row.stats.getLives(),
            pingText(row.info.getResponseTime())
        }, own ? OWN_ROW_COLOR : ROW_COLOR, row.info);
    }

    private static int aliveCount(List<Row> rows) {
        return (int) rows.stream().filter(r -> r.stats.getLives() > 0).count();
    }

    private static String pingText(int ms) {
        if (ms < 0) return GRAY + "?";

        TextFormatting color = ms < 150 ? GREEN : ms < 300 ? YELLOW : ms < 600 ? GOLD : RED;
        return color + String.valueOf(ms) + "ms";
    }

    private void measure(FontRenderer fr, Line line, boolean[] visible, int[] widths) {
        for (int c = 0; c < COLUMNS; c++) {
            if (!visible[c]) continue;

            int width = fr.getStringWidth(line.cells[c]);
            if (c == NAME && line.head != null)
                width += HEAD_SIZE + 2;

            widths[c] = Math.max(widths[c], width);
        }
    }

    private void drawLine(FontRenderer fr, Line line, int x, int y, boolean[] visible, int[] widths) {
        for (int c = 0; c < COLUMNS; c++) {
            if (!visible[c]) continue;

            String text = line.cells[c];

            if (c == RANK || c == NAME) {
                int textX = x;
                if (c == NAME && line.head != null) {
                    drawHead(line.head, x, y);
                    textX += HEAD_SIZE + 2;
                }
                fr.drawStringWithShadow(text, textX, y, 0xFFFFFF);
            } else {
                fr.drawStringWithShadow(text, x + widths[c] - fr.getStringWidth(text), y, 0xFFFFFF);
            }

            x += widths[c] + COLUMN_GAP;
        }
    }

    private void drawHead(NetworkPlayerInfo info, int x, int y) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        mc.getTextureManager().bindTexture(info.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, HEAD_SIZE, HEAD_SIZE, 64, 64);  // face
        Gui.drawScaledCustomSizeModalRect(x, y, 40, 8, 8, 8, HEAD_SIZE, HEAD_SIZE, 64, 64); // hat layer
    }

    private static void drawCentered(FontRenderer fr, String text, int centerX, int y) {
        fr.drawStringWithShadow(text, centerX - fr.getStringWidth(text) / 2f, y, 0xFFFFFF);
    }

    private static class Row {
        final NetworkPlayerInfo info;
        final UUID uuid;
        final String name;
        final PlayerStats stats;
        final Team team;

        Row(NetworkPlayerInfo info, ClientGameInfo game) {
            this.info = info;
            this.uuid = info.getGameProfile().getId();
            this.name = info.getGameProfile().getName();
            this.stats = game.getStats(uuid, name);
            this.team = game.getTeam(uuid);
        }
    }

    private static class Line {
        final String[] cells;
        final int background;
        final NetworkPlayerInfo head;

        Line(String[] cells, int background, NetworkPlayerInfo head) {
            this.cells = cells;
            this.background = background;
            this.head = head;
        }

        static Line text(String text, int background) {
            return new Line(new String[]{"", text, "", "", "", ""}, background, null);
        }
    }
}
