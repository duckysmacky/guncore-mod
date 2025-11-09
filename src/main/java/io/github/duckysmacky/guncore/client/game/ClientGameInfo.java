package io.github.duckysmacky.guncore.client.game;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.GameConfig;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import net.minecraft.client.Minecraft;

import java.util.Map;
import java.util.UUID;

public final class ClientGameInfo {
    private static ClientGameInfo instance;
    private final PlayerStats stats;
    private GameMode gameMode;
    private GameMode.Variant gameModeVariant;
    private GameState gameState;
    private int roundDurationSec;

    private ClientGameInfo() {
        this.stats = new PlayerStats(Minecraft.getMinecraft().player.getName());
        this.gameMode = GameMode.FFA;
        this.gameModeVariant = GameMode.Variant.LIVES;
        this.gameState = GameState.NOT_STARTED;
        this.roundDurationSec = 0;
    }

    public static ClientGameInfo instance() {
        if (instance == null) {
            instance = new ClientGameInfo();
        }

        return instance;
    }

    public void updatePlayerStats(Map<UUID, PlayerStats> playerStats) {
        UUID uuid = Minecraft.getMinecraft().player.getUniqueID();
        PlayerStats stats = playerStats.get(uuid);

        if (stats != null) {
            this.stats.setKills(stats.getKills());
            this.stats.setDeaths(stats.getDeaths());
            this.stats.setLives(stats.getLives());
        }
    }

    public int getRoundLengthSec() {
        GameConfig gameConfig = ConfigManager.instance().getGameConfig();

        switch (gameMode) {
            case FFA:
                return gameConfig.ffaConfig.roundLengthSec;
            case TDM:
                return gameConfig.tdmConfig.roundLengthSec;
            case HOSTAGE:
                return gameConfig.hostageConfig.roundLengthSec;
            default:
                return 600;
        }
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setGameModeVariant(GameMode.Variant gameModeVariant) {
        this.gameModeVariant = gameModeVariant;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setRoundDurationSec(int roundDurationSec) {
        this.roundDurationSec = roundDurationSec;
    }

    public PlayerStats getPlayerStats() {
        return stats;
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
}