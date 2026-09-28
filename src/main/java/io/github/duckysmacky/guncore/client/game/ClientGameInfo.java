package io.github.duckysmacky.guncore.client.game;

import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.game.Team;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.ToIntFunction;

public final class ClientGameInfo {
    private static ClientGameInfo instance;
    private GameMode gameMode;
    private GameMode.Variant gameModeVariant;
    private GameState gameState;
    private int roundDurationSec;
    private int roundLengthSec;
    private int killTarget;
    private Map<UUID, PlayerStats> playerStats;
    private Map<UUID, Team> playerTeams;

    private ClientGameInfo() {
        this.gameMode = GameMode.FFA;
        this.gameModeVariant = GameMode.Variant.LIVES;
        this.gameState = GameState.NOT_STARTED;
        this.playerStats = Collections.emptyMap();
        this.playerTeams = Collections.emptyMap();
    }

    public static ClientGameInfo instance() {
        if (instance == null) {
            instance = new ClientGameInfo();
        }

        return instance;
    }

    public void update(
        GameMode gameMode,
        GameMode.Variant gameModeVariant,
        GameState gameState,
        int roundDurationSec,
        int roundLengthSec,
        int killTarget,
        Map<UUID, PlayerStats> playerStats,
        Map<UUID, Team> playerTeams
    ) {
        this.gameMode = gameMode;
        this.gameModeVariant = gameModeVariant;
        this.gameState = gameState;
        this.roundDurationSec = roundDurationSec;
        this.roundLengthSec = roundLengthSec;
        this.killTarget = killTarget;
        this.playerStats = playerStats;
        this.playerTeams = playerTeams;
    }

    public PlayerStats getStats(UUID uuid, String fallbackName) {
        PlayerStats stats = playerStats.get(uuid);
        return stats != null ? stats : new PlayerStats(fallbackName);
    }

    public PlayerStats getOwnStats() {
        Minecraft mc = Minecraft.getMinecraft();
        return getStats(mc.player.getUniqueID(), mc.player.getName());
    }

    public Team getTeam(UUID uuid) {
        return playerTeams.getOrDefault(uuid, Team.NONE);
    }

    public Team getOwnTeam() {
        return getTeam(Minecraft.getMinecraft().player.getUniqueID());
    }

    /** Sums a stat over all team members, including offline ones (same as the server's win logic) */
    public int getTeamTotal(Team team, ToIntFunction<PlayerStats> stat) {
        return playerStats.entrySet().stream()
            .filter(e -> getTeam(e.getKey()) == team)
            .mapToInt(e -> stat.applyAsInt(e.getValue()))
            .sum();
    }

    public int getTeamKills(Team team) {
        return getTeamTotal(team, PlayerStats::getKills);
    }

    public boolean isTeamMode() {
        return gameMode != GameMode.FFA;
    }

    public boolean isRoundActive() {
        return gameState == GameState.RUNNING || gameState == GameState.PAUSED;
    }

    public String goalText() {
        switch (gameModeVariant) {
            case TIME:
                return "Most kills in " + TextUtils.formatTime(roundLengthSec);
            case LIVES:
                return isTeamMode() ? "Last team standing" : "Last one standing";
            case KILLS:
                return "First to " + killTarget + " kills";
            default:
                return "";
        }
    }

    public boolean hasTime() {
        return gameState != GameState.NOT_STARTED;
    }

    private boolean showsTimeLeft() {
        return gameModeVariant == GameMode.Variant.TIME && gameState != GameState.ENDED;
    }

    public String timeLabel() {
        return showsTimeLeft() ? "Time left" : "Time";
    }

    public String timeValue() {
        return TextUtils.formatTime(showsTimeLeft() ? roundLengthSec - roundDurationSec : roundDurationSec);
    }

    public TextFormatting stateColor() {
        switch (gameState) {
            case RUNNING: return TextFormatting.GREEN;
            case PAUSED: return TextFormatting.YELLOW;
            case ENDED: return TextFormatting.RED;
            default: return TextFormatting.GRAY;
        }
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameMode.Variant getGameModeVariant() {
        return gameModeVariant;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getRoundDurationSec() {
        return roundDurationSec;
    }

    public int getRoundLengthSec() {
        return roundLengthSec;
    }

    public int getKillTarget() {
        return killTarget;
    }
}
