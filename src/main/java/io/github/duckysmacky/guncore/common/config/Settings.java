package io.github.duckysmacky.guncore.common.config;

import io.github.duckysmacky.guncore.common.game.EquipmentMode;
import io.github.duckysmacky.guncore.common.game.GameMode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Server-side settings persisted in {@code config/guncore/settings.json}.
 */
public class Settings {
    public GameSettings game;
    public WorldSettings world;
    public EquipmentSettings equipment;

    public static Settings createDefault() {
        Settings settings = new Settings();

        settings.game = new GameSettings();
        settings.game.mode = GameMode.FFA;
        settings.game.variant = GameMode.Variant.LIVES;
        settings.game.killOnlyLifeLoss = true;
        settings.game.ffa = new GameModeSettings(10 * 60, 5, 15); // 10 minutes, 5 lives, 15 kills
        settings.game.tdm = new GameModeSettings(15 * 60, 3, 15);
        settings.game.hostage = new GameModeSettings(20 * 60, 3, 15);

        settings.world = new WorldSettings();
        settings.world.gamerules.put("doWeatherCycle", false);
        settings.world.gamerules.put("doDaylightCycle", false);
        settings.world.gamerules.put("doFireTick", false);
        settings.world.gamerules.put("doTileDrops", false);
        settings.world.gamerules.put("naturalRegeneration", true);
        settings.world.gamerules.put("doMobSpawning", false);

        settings.equipment = new EquipmentSettings();
        settings.equipment.mode = EquipmentMode.BUILDER;

        return settings;
    }

    /**
     * Gson doesn't call constructors, so values missing from an older/partial settings.json end up as null/0.
     * Replaces those with the defaults. Unknown gamerule keys are dropped.
     */
    public static Settings withDefaults(Settings settings) {
        Settings defaults = createDefault();
        if (settings == null) return defaults;

        if (settings.game != null) {
            GameSettings g = settings.game;
            GameSettings d = defaults.game;
            if (g.mode != null) d.mode = g.mode;
            if (g.variant != null) d.variant = g.variant;
            if (g.killOnlyLifeLoss != null) d.killOnlyLifeLoss = g.killOnlyLifeLoss;
            d.ffa = GameModeSettings.withDefaults(g.ffa, d.ffa);
            d.tdm = GameModeSettings.withDefaults(g.tdm, d.tdm);
            d.hostage = GameModeSettings.withDefaults(g.hostage, d.hostage);
        }

        if (settings.world != null && settings.world.gamerules != null) {
            defaults.world.gamerules.replaceAll((rule, value) -> {
                Boolean configured = settings.world.gamerules.get(rule);
                return configured != null ? configured : value;
            });
        }

        if (settings.equipment != null && settings.equipment.mode != null)
            defaults.equipment.mode = settings.equipment.mode;

        return defaults;
    }

    public static class WorldSettings {
        public Map<String, Boolean> gamerules;

        public WorldSettings() {
            this.gamerules = new LinkedHashMap<>();
        }
    }

    public static class GameSettings {
        public GameMode mode;
        public GameMode.Variant variant;
        public Boolean killOnlyLifeLoss;
        public GameModeSettings ffa;
        public GameModeSettings tdm;
        public GameModeSettings hostage;

        public GameModeSettings configFor(GameMode gameMode) {
            switch (gameMode) {
                case TDM: return tdm;
                case HOSTAGE: return hostage;
                default: return ffa;
            }
        }
    }

    public static class GameModeSettings {
        public int roundLengthSec;
        public int startingLives;
        public int killTarget;

        public GameModeSettings(int roundLengthSec, int startingLives, int killTarget) {
            this.roundLengthSec = roundLengthSec;
            this.startingLives = startingLives;
            this.killTarget = killTarget;
        }

        static GameModeSettings withDefaults(GameModeSettings config, GameModeSettings defaults) {
            if (config == null) return defaults;

            return new GameModeSettings(
                config.roundLengthSec > 0 ? config.roundLengthSec : defaults.roundLengthSec,
                config.startingLives > 0 ? config.startingLives : defaults.startingLives,
                config.killTarget > 0 ? config.killTarget : defaults.killTarget
            );
        }
    }

    public static class EquipmentSettings {
        public EquipmentMode mode;
    }
}
