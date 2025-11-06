package io.github.duckysmacky.guncore.common.config;

import java.util.Objects;

public class GameConfig {
    public final GameModeConfig ffaConfig;
    public final GameModeConfig tdmConfig;
    public final GameModeConfig hostageConfig;

    public GameConfig(
        GameModeConfig ffaConfig,
        GameModeConfig tdmConfig,
        GameModeConfig hostageConfig
    ) {
        this.ffaConfig = Objects.requireNonNull(ffaConfig);
        this.tdmConfig = Objects.requireNonNull(tdmConfig);
        this.hostageConfig = Objects.requireNonNull(hostageConfig);
    }

    public static GameConfig createDefault() {
        return new GameConfig(
            new GameModeConfig(10 * 60, 5), // FFA: 10 minutes, 5 lives
            new GameModeConfig(15 * 60, 3),  // TDM: 15 minutes, 3 lives
            new GameModeConfig(20 * 60, 3) // Hostage: 20 minutes, 3 lives
        );
    }

    public static class GameModeConfig {
        public final int roundLengthSec;
        public final int startingLives;

        public GameModeConfig(
            int roundLengthSec,
            int startingLives
        ) {
            this.roundLengthSec = roundLengthSec;
            this.startingLives = startingLives;
        }
    }
}
