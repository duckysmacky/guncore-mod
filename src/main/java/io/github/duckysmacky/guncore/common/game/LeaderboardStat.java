package io.github.duckysmacky.guncore.common.game;

import io.github.duckysmacky.guncore.common.util.TextUtils;

/** Stats ranked in the global stats menu; the ordinal is sent over the network */
public enum LeaderboardStat {
    GAMES("Games played", "games", Format.INT, 0, true),
    WINS("Wins", "wins", Format.INT, 0, true),
    LOSSES("Losses", "losses", Format.INT, 0, true),
    WIN_RATE("Win rate", "CAST(wins AS REAL) / games", Format.PERCENT, 5, true),
    KILLS("Kills", "kills", Format.INT, 0, true),
    DEATHS("Deaths", "deaths", Format.INT, 0, true),
    KD("K/D", "CAST(kills AS REAL) / MAX(deaths, 1)", Format.DECIMAL, 5, true),
    BEST_ROUND_KILLS("Most kills in a round", "best_round_kills", Format.INT, 0, false),
    BEST_STREAK("Best kill streak", "best_streak", Format.INT, 0, false),
    MVPS("Times MVP", "mvps", Format.INT, 0, false),
    TIME_PLAYED("Time played", "time_played_sec", Format.TIME, 0, false),
    AVG_KILLS("Avg. kills per round", "CAST(kills AS REAL) / games", Format.DECIMAL, 5, false);

    public enum Format { INT, PERCENT, DECIMAL, TIME }

    public final String display;
    /** Value expression over the players table, server side only */
    public final String sql;
    public final Format format;
    /** Ratio boards skip players with fewer games, so a single lucky round doesn't top them */
    public final int minGames;
    /** Shown in the stats row, otherwise in the records row */
    public final boolean basic;

    LeaderboardStat(String display, String sql, Format format, int minGames, boolean basic) {
        this.display = display;
        this.sql = sql;
        this.format = format;
        this.minGames = minGames;
        this.basic = basic;
    }

    public String format(double value) {
        switch (format) {
            case PERCENT: return Math.round(value * 100) + "%";
            case DECIMAL: return String.format("%.2f", value);
            case TIME: return TextUtils.formatDuration((long) value);
            default: return String.valueOf((long) value);
        }
    }
}
