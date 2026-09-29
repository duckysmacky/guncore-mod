package io.github.duckysmacky.guncore.common.config;

public record GameConfig(
    GameModeConfig ffaConfig,
    GameModeConfig tdmConfig,
    GameModeConfig hostageConfig
) {
    public static GameConfig createDefault() {
        return new GameConfig(
            new GameModeConfig(10 * 60, 5, 15), // FFA: 10 minutes, 5 lives, 15 kills
            new GameModeConfig(15 * 60, 3, 15),  // TDM: 15 minutes, 3 lives, 15 kills
            new GameModeConfig(20 * 60, 3, 15) // Hostage: 20 minutes, 3 lives, 10 kills
        );
    }

    /**
     * Gson leaves values missing from an older/partial game.json as null/0.
     * Replaces those with the defaults.
     */
    public static GameConfig withDefaults(GameConfig config) {
        GameConfig defaults = createDefault();
        if (config == null) return defaults;

        return new GameConfig(
            GameModeConfig.withDefaults(config.ffaConfig(), defaults.ffaConfig()),
            GameModeConfig.withDefaults(config.tdmConfig(), defaults.tdmConfig()),
            GameModeConfig.withDefaults(config.hostageConfig(), defaults.hostageConfig())
        );
    }

    public record GameModeConfig(int roundLengthSec, int startingLives, int killTarget) {
        static GameModeConfig withDefaults(GameModeConfig config, GameModeConfig defaults) {
            if (config == null) return defaults;

            return new GameModeConfig(
                config.roundLengthSec() > 0 ? config.roundLengthSec() : defaults.roundLengthSec(),
                config.startingLives() > 0 ? config.startingLives() : defaults.startingLives(),
                config.killTarget() > 0 ? config.killTarget() : defaults.killTarget()
            );
        }
    }
}
