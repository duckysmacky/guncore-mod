package io.github.duckysmacky.guncore.server.stats;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.game.LeaderboardStat;
import io.github.duckysmacky.guncore.common.game.Leaderboards;
import io.github.duckysmacky.guncore.common.game.PlayerProfile;
import io.github.duckysmacky.guncore.common.game.UsageCategory;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/** Lifetime player stats in config/guncore/stats.db; server side only */
public final class StatsDatabase {
    private static final String ID = "StatsDatabase";
    private static final String FILE = "guncore/stats.db";
    private static StatsDatabase instance;
    private Connection connection;

    private StatsDatabase() {}

    public static StatsDatabase instance() {
        if (instance == null) {
            instance = new StatsDatabase();
        }

        return instance;
    }

    /** One player's result of a finished round */
    public static class RoundResult {
        public final UUID uuid;
        public final String name;
        public final int kills, deaths, bestStreak;
        public final boolean win, mvp;
        public final int durationSec;
        /** Everything the player started the round with */
        public final List<Selection> selections;

        public RoundResult(UUID uuid, String name, int kills, int deaths, int bestStreak, boolean win, boolean mvp, int durationSec, List<Selection> selections) {
            this.uuid = uuid;
            this.name = name;
            this.kills = kills;
            this.deaths = deaths;
            this.bestStreak = bestStreak;
            this.win = win;
            this.mvp = mvp;
            this.durationSec = durationSec;
            this.selections = selections;
        }
    }

    public static class Selection {
        public final UsageCategory category;
        public final String item;

        public Selection(UsageCategory category, String item) {
            this.category = category;
            this.item = item;
        }
    }

    public void open() {
        if (connection != null) return;

        File file = new File(Loader.instance().getConfigDir(), FILE);
        file.getParentFile().mkdirs();

        try {
            // not DriverManager: it can't see drivers loaded by Forge's LaunchClassLoader
            connection = new org.sqlite.JDBC().connect("jdbc:sqlite:" + file.getAbsolutePath(), new Properties());

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS players ("
                    + "uuid TEXT PRIMARY KEY, name TEXT NOT NULL, "
                    + "games INTEGER NOT NULL DEFAULT 0, wins INTEGER NOT NULL DEFAULT 0, losses INTEGER NOT NULL DEFAULT 0, "
                    + "kills INTEGER NOT NULL DEFAULT 0, deaths INTEGER NOT NULL DEFAULT 0, "
                    + "best_round_kills INTEGER NOT NULL DEFAULT 0, best_streak INTEGER NOT NULL DEFAULT 0, "
                    + "mvps INTEGER NOT NULL DEFAULT 0, time_played_sec INTEGER NOT NULL DEFAULT 0, "
                    + "first_played INTEGER NOT NULL, last_played INTEGER NOT NULL)");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS usage ("
                    + "uuid TEXT NOT NULL, category TEXT NOT NULL, item TEXT NOT NULL, uses INTEGER NOT NULL DEFAULT 0, "
                    + "PRIMARY KEY (uuid, category, item))");
            }

            GuncoreMod.LOGGER.info("[{}] Opened {}", ID, file);
        } catch (SQLException e) {
            GuncoreMod.LOGGER.error("[{}] Failed to open {}, stats won't be saved", ID, file, e);
            connection = null;
        }
    }

    public void close() {
        if (connection == null) return;

        try {
            connection.close();
        } catch (SQLException e) {
            GuncoreMod.LOGGER.error("[{}] Failed to close the database", ID, e);
        }
        connection = null;
    }

    /** Adds a finished round to every participant's lifetime stats, all or nothing */
    public void recordRound(List<RoundResult> results) {
        if (connection == null || results.isEmpty()) return;

        long now = System.currentTimeMillis();

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement player = connection.prepareStatement(
                    "INSERT INTO players (uuid, name, games, wins, losses, kills, deaths, best_round_kills, best_streak, mvps, time_played_sec, first_played, last_played) "
                        + "VALUES (?, ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON CONFLICT(uuid) DO UPDATE SET name = excluded.name, games = games + 1, "
                        + "wins = wins + excluded.wins, losses = losses + excluded.losses, "
                        + "kills = kills + excluded.kills, deaths = deaths + excluded.deaths, "
                        + "best_round_kills = MAX(best_round_kills, excluded.best_round_kills), "
                        + "best_streak = MAX(best_streak, excluded.best_streak), mvps = mvps + excluded.mvps, "
                        + "time_played_sec = time_played_sec + excluded.time_played_sec, last_played = excluded.last_played");
                 PreparedStatement usage = connection.prepareStatement(
                    "INSERT INTO usage (uuid, category, item, uses) VALUES (?, ?, ?, 1) "
                        + "ON CONFLICT(uuid, category, item) DO UPDATE SET uses = uses + 1")) {

                for (RoundResult result : results) {
                    String uuid = result.uuid.toString();

                    player.setString(1, uuid);
                    player.setString(2, result.name);
                    player.setInt(3, result.win ? 1 : 0);
                    player.setInt(4, result.win ? 0 : 1);
                    player.setInt(5, result.kills);
                    player.setInt(6, result.deaths);
                    player.setInt(7, result.kills);
                    player.setInt(8, result.bestStreak);
                    player.setInt(9, result.mvp ? 1 : 0);
                    player.setInt(10, result.durationSec);
                    player.setLong(11, now);
                    player.setLong(12, now);
                    player.addBatch();

                    for (Selection selection : result.selections) {
                        usage.setString(1, uuid);
                        usage.setString(2, selection.category.id);
                        usage.setString(3, selection.item);
                        usage.addBatch();
                    }
                }

                player.executeBatch();
                usage.executeBatch();
            }

            connection.commit();
        } catch (SQLException e) {
            GuncoreMod.LOGGER.error("[{}] Failed to record the round", ID, e);
            try {
                connection.rollback();
            } catch (SQLException rollbackError) {
                GuncoreMod.LOGGER.error("[{}] Failed to roll back", ID, rollbackError);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }

    /** Server-wide top lists; empty when the database is unavailable */
    public Leaderboards loadLeaderboards() {
        Leaderboards boards = new Leaderboards();
        if (connection == null) return boards;

        try {
            for (LeaderboardStat stat : LeaderboardStat.values()) {
                // stat.sql is a constant from the enum, never user input
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT name, " + stat.sql + " AS v FROM players WHERE games >= ? ORDER BY v DESC, name LIMIT " + Leaderboards.TOP_ENTRIES)) {
                    statement.setInt(1, Math.max(1, stat.minGames));
                    readEntries(statement, boards.get(stat));
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT item, SUM(uses) AS u FROM usage WHERE category = ? GROUP BY item ORDER BY u DESC, item LIMIT " + Leaderboards.TOP_ENTRIES)) {
                for (UsageCategory category : UsageCategory.values()) {
                    statement.setString(1, category.id);
                    readEntries(statement, boards.get(category));
                }
            }
        } catch (SQLException e) {
            GuncoreMod.LOGGER.error("[{}] Failed to load leaderboards", ID, e);
        }

        return boards;
    }

    private static void readEntries(PreparedStatement statement, List<Leaderboards.Entry> entries) throws SQLException {
        try (ResultSet result = statement.executeQuery()) {
            while (result.next())
                entries.add(new Leaderboards.Entry(result.getString(1), result.getDouble(2)));
        }
    }

    /** Looks up a player by their last known name, ignoring case; works for offline players */
    public UUID findUuidByName(String name) {
        if (connection == null) return null;

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT uuid FROM players WHERE name = ? COLLATE NOCASE ORDER BY last_played DESC LIMIT 1")) {
            statement.setString(1, name);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? UUID.fromString(result.getString(1)) : null;
            }
        } catch (SQLException e) {
            GuncoreMod.LOGGER.error("[{}] Failed to look up player {}", ID, name, e);
            return null;
        }
    }

    /** Returns null when the player never finished a round (or the database is unavailable) */
    public PlayerProfile loadProfile(UUID uuid) {
        if (connection == null) return null;

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());

            PlayerProfile profile = new PlayerProfile();
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return null;

                profile.name = result.getString("name");
                profile.games = result.getInt("games");
                profile.wins = result.getInt("wins");
                profile.losses = result.getInt("losses");
                profile.kills = result.getInt("kills");
                profile.deaths = result.getInt("deaths");
                profile.bestRoundKills = result.getInt("best_round_kills");
                profile.bestStreak = result.getInt("best_streak");
                profile.mvps = result.getInt("mvps");
                profile.timePlayedSec = result.getLong("time_played_sec");
                profile.firstPlayed = result.getLong("first_played");
                profile.lastPlayed = result.getLong("last_played");
            }

            try (PreparedStatement usage = connection.prepareStatement(
                    "SELECT item, uses FROM usage WHERE uuid = ? AND category = ? ORDER BY uses DESC, item LIMIT " + PlayerProfile.TOP_ENTRIES)) {
                for (UsageCategory category : UsageCategory.values()) {
                    usage.setString(1, uuid.toString());
                    usage.setString(2, category.id);

                    List<PlayerProfile.Usage> favorites = new ArrayList<>();
                    try (ResultSet result = usage.executeQuery()) {
                        while (result.next())
                            favorites.add(new PlayerProfile.Usage(result.getString(1), result.getInt(2)));
                    }
                    profile.favorites.put(category, favorites);
                }
            }

            return profile;
        } catch (SQLException e) {
            GuncoreMod.LOGGER.error("[{}] Failed to load profile of {}", ID, uuid, e);
            return null;
        }
    }
}
