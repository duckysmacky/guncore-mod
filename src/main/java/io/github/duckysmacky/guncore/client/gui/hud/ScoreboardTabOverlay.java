package io.github.duckysmacky.guncore.client.gui.hud;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.game.Team;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.ChatFormatting.*;

/**
 * Replaces the vanilla TAB player list with a game scoreboard.
 */
@Mod.EventBusSubscriber(modid = GuncoreMod.MOD_ID, value = Dist.CLIENT)
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

    @SubscribeEvent
    public static void onOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_LIST.id()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.getConnection() == null || mc.options.hideGui || !mc.options.keyPlayerList.isDown())
            return;

        draw(mc, event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
    }

    private static void draw(Minecraft mc, GuiGraphics graphics, int screenWidth, int screenHeight) {
        ClientGameInfo game = ClientGameInfo.instance();
        Font font = mc.font;

        boolean[] visible = {
            !game.isTeamMode(), true, true, true,
            game.getGameModeVariant() == GameMode.Variant.LIVES, true
        };

        List<Row> rows = mc.getConnection().getListedOnlinePlayers().stream()
            .map(info -> new Row(info, game))
            .collect(Collectors.toList());

        List<Line> lines = game.isTeamMode() ? teamLines(mc, game, rows) : ffaLines(mc, game, rows);

        int maxLines = Math.max(1, (screenHeight - TOP - PADDING * 2 - ROW_HEIGHT * 3 - 4) / ROW_HEIGHT);
        if (lines.size() > maxLines) {
            int hidden = lines.size() - (maxLines - 1);
            lines = new ArrayList<>(lines.subList(0, maxLines - 1));
            lines.add(Line.text(GRAY + "... and " + hidden + " more"));
        }

        Line columnHeader = new Line(new String[]{
            GRAY + "#", GRAY + "Player", GRAY + "K", GRAY + "D", GRAY + "L", GRAY + "Ping"
        }, 0, null);

        String title = "" + GOLD + BOLD + game.getGameMode().display;
        String info = GRAY + "Status: " + game.stateColor() + game.getGameState().display
            + GRAY + "   Goal: " + WHITE + game.goalText()
            + (game.hasTime() ? GRAY + "   " + game.timeLabel() + ": " + WHITE + game.timeValue() : "");

        int[] widths = new int[COLUMNS];
        measure(font, columnHeader, visible, widths);
        for (Line line : lines)
            measure(font, line, visible, widths);

        int tableWidth = -COLUMN_GAP;
        for (int c = 0; c < COLUMNS; c++)
            if (visible[c]) tableWidth += widths[c] + COLUMN_GAP;

        int innerWidth = Math.max(tableWidth, Math.max(font.width(title), font.width(info)));
        int left = (screenWidth - innerWidth) / 2 - PADDING;
        int innerLeft = left + PADDING;
        int height = PADDING * 2 + ROW_HEIGHT * 2 + 4 + ROW_HEIGHT * (lines.size() + 1);

        graphics.fill(left, TOP, innerLeft + innerWidth + PADDING, TOP + height, PANEL_COLOR);

        int y = TOP + PADDING;
        drawCentered(graphics, font, title, innerLeft + innerWidth / 2, y);
        y += ROW_HEIGHT;
        drawCentered(graphics, font, info, innerLeft + innerWidth / 2, y);
        y += ROW_HEIGHT + 4;

        int tableLeft = innerLeft + (innerWidth - tableWidth) / 2;
        drawLine(graphics, font, columnHeader, tableLeft, y, visible, widths);
        y += ROW_HEIGHT;

        for (Line line : lines) {
            if (line.background != 0)
                graphics.fill(innerLeft, y, innerLeft + innerWidth, y + ROW_HEIGHT - 1, line.background);

            drawLine(graphics, font, line, tableLeft, y, visible, widths);
            y += ROW_HEIGHT;
        }
    }

    private static List<Line> ffaLines(Minecraft mc, ClientGameInfo game, List<Row> rows) {
        rows.sort(Comparator.comparing(r -> r.stats, PlayerStats.RANKING));

        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++)
            lines.add(playerLine(mc, game, rows.get(i), (i + 1) + "."));

        return lines;
    }

    private static List<Line> teamLines(Minecraft mc, ClientGameInfo game, List<Row> rows) {
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
            int rgb = team.color.getColor() != null ? team.color.getColor() : 0xFFFFFF;

            lines.add(new Line(new String[]{
                "",
                "" + team.color + BOLD + name,
                WHITE.toString() + game.getTeamKills(team),
                WHITE.toString() + game.getTeamTotal(team, PlayerStats::getDeaths),
                lives ? WHITE.toString() + aliveCount(members) + GRAY + "/" + members.size() : "",
                ""
            }, (TEAM_HEADER_ALPHA << 24) | (rgb & 0xFFFFFF), null));

            members.forEach(r -> lines.add(playerLine(mc, game, r, "")));
        });

        return lines;
    }

    private static Line playerLine(Minecraft mc, ClientGameInfo game, Row row, String rank) {
        boolean eliminated = game.getGameModeVariant() == GameMode.Variant.LIVES
            && game.getGameState() != GameState.NOT_STARTED
            && row.stats.getLives() <= 0;

        String nameStyle = eliminated
            ? "" + GRAY + STRIKETHROUGH
            : (game.isTeamMode() ? row.team.color.toString() : WHITE.toString());

        boolean own = row.uuid.equals(mc.player.getUUID());

        return new Line(new String[]{
            GRAY + rank,
            nameStyle + row.name,
            WHITE.toString() + row.stats.getKills(),
            WHITE.toString() + row.stats.getDeaths(),
            WHITE.toString() + row.stats.getLives(),
            pingText(row.info.getLatency())
        }, own ? OWN_ROW_COLOR : ROW_COLOR, row.info);
    }

    private static int aliveCount(List<Row> rows) {
        return (int) rows.stream().filter(r -> r.stats.getLives() > 0).count();
    }

    private static String pingText(int ms) {
        if (ms < 0) return GRAY + "?";

        ChatFormatting color = ms < 150 ? GREEN : ms < 300 ? YELLOW : ms < 600 ? GOLD : RED;
        return color + String.valueOf(ms) + "ms";
    }

    private static void measure(Font font, Line line, boolean[] visible, int[] widths) {
        for (int c = 0; c < COLUMNS; c++) {
            if (!visible[c]) continue;

            int width = font.width(line.cells[c]);
            if (c == NAME && line.head != null)
                width += HEAD_SIZE + 2;

            widths[c] = Math.max(widths[c], width);
        }
    }

    private static void drawLine(GuiGraphics graphics, Font font, Line line, int x, int y, boolean[] visible, int[] widths) {
        for (int c = 0; c < COLUMNS; c++) {
            if (!visible[c]) continue;

            String text = line.cells[c];

            if (c == RANK || c == NAME) {
                int textX = x;
                if (c == NAME && line.head != null) {
                    PlayerFaceRenderer.draw(graphics, line.head.getSkinLocation(), x, y, HEAD_SIZE);
                    textX += HEAD_SIZE + 2;
                }
                graphics.drawString(font, text, textX, y, 0xFFFFFF, true);
            } else {
                graphics.drawString(font, text, x + widths[c] - font.width(text), y, 0xFFFFFF, true);
            }

            x += widths[c] + COLUMN_GAP;
        }
    }

    private static void drawCentered(GuiGraphics graphics, Font font, String text, int centerX, int y) {
        graphics.drawString(font, text, centerX - font.width(text) / 2, y, 0xFFFFFF, true);
    }

    private static class Row {
        final PlayerInfo info;
        final UUID uuid;
        final String name;
        final PlayerStats stats;
        final Team team;

        Row(PlayerInfo info, ClientGameInfo game) {
            this.info = info;
            this.uuid = info.getProfile().getId();
            this.name = info.getProfile().getName();
            this.stats = game.getStats(uuid, name);
            this.team = game.getTeam(uuid);
        }
    }

    private static class Line {
        final String[] cells;
        final int background;
        final PlayerInfo head;

        Line(String[] cells, int background, PlayerInfo head) {
            this.cells = cells;
            this.background = background;
            this.head = head;
        }

        static Line text(String text) {
            return new Line(new String[]{"", text, "", "", "", ""}, 0, null);
        }
    }
}
