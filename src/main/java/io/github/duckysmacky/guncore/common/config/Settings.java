package io.github.duckysmacky.guncore.common.config;

import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.game.EquipmentMode;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.LoadoutSystem;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Server-side settings persisted in {@code config/guncore/settings.json}.
 */
public class Settings {
    public static final String DEFAULT_RARITY_PRESET = "balanced";

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
        settings.equipment.mythicsEnabled = true;
        settings.equipment.loadoutSystem = LoadoutSystem.FLEXIBLE;
        settings.equipment.rarityPreset = DEFAULT_RARITY_PRESET;
        // scores by rarity (common to mythic), max scores by equipment mode (kits, builder)
        settings.equipment.rarityPresets = new LinkedHashMap<>();
        settings.equipment.rarityPresets.put("tight", RarityPreset.create(new int[]{0, 1, 3, 6, 10, 25}, new int[]{25, 30}));
        settings.equipment.rarityPresets.put(DEFAULT_RARITY_PRESET, RarityPreset.create(new int[]{0, 1, 2, 5, 8, 20}, new int[]{20, 24}));
        settings.equipment.rarityPresets.put("loose", RarityPreset.create(new int[]{0, 1, 2, 3, 5, 20}, new int[]{24, 32}));

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

        if (settings.world != null && settings.world.itemPickup != null)
            defaults.world.itemPickup = settings.world.itemPickup;

        if (settings.world != null && settings.world.gamerules != null) {
            defaults.world.gamerules.replaceAll((rule, value) -> {
                Boolean configured = settings.world.gamerules.get(rule);
                return configured != null ? configured : value;
            });
        }

        if (settings.equipment != null) {
            EquipmentSettings e = settings.equipment;
            EquipmentSettings d = defaults.equipment;
            if (e.mode != null) d.mode = e.mode;
            if (e.mythicsEnabled != null) d.mythicsEnabled = e.mythicsEnabled;
            if (e.loadoutSystem != null) d.loadoutSystem = e.loadoutSystem;

            // presets added in the file are kept; their missing values come from the built-in preset of the same name
            if (e.rarityPresets != null && !e.rarityPresets.isEmpty()) {
                Map<String, RarityPreset> builtIn = d.rarityPresets;
                d.rarityPresets = new LinkedHashMap<>();
                e.rarityPresets.forEach((name, preset) -> d.rarityPresets.put(name, RarityPreset.withDefaults(
                    preset, builtIn.getOrDefault(name, builtIn.get(DEFAULT_RARITY_PRESET))
                )));
            }

            if (e.rarityPreset != null) d.rarityPreset = e.rarityPreset;
            if (!d.rarityPresets.containsKey(d.rarityPreset)) {
                d.rarityPreset = d.rarityPresets.containsKey(DEFAULT_RARITY_PRESET)
                    ? DEFAULT_RARITY_PRESET
                    : d.rarityPresets.keySet().iterator().next();
            }
        }

        return defaults;
    }

    public static class WorldSettings {
        public Boolean itemPickup;
        public Map<String, Boolean> gamerules;

        public WorldSettings() {
            this.itemPickup = true;
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
        public Boolean mythicsEnabled;
        public LoadoutSystem loadoutSystem;
        /** Name of the selected entry of {@link #rarityPresets} */
        public String rarityPreset;
        public Map<String, RarityPreset> rarityPresets;

        public RarityPreset selectedPreset() {
            return rarityPresets.get(rarityPreset);
        }
    }

    /**
     * Rarity scores used by the Rarity Score loadout system. Maps are keyed by strings (lowercase rarity names and
     * equipment mode ids), since Gson can't read back enum map keys which use {@code @SerializedName}.
     */
    public static class RarityPreset {
        public Map<String, Integer> scores;
        public Map<String, Integer> maxScore;

        /**
         * @param scores by {@link Rarity} order
         * @param maxScores by {@link EquipmentMode} order
         */
        public static RarityPreset create(int[] scores, int[] maxScores) {
            RarityPreset preset = new RarityPreset();
            preset.scores = new LinkedHashMap<>();
            for (Rarity rarity : Rarity.values())
                preset.scores.put(key(rarity), scores[rarity.ordinal()]);
            preset.maxScore = new LinkedHashMap<>();
            for (EquipmentMode mode : EquipmentMode.values())
                preset.maxScore.put(mode.id, maxScores[mode.ordinal()]);
            return preset;
        }

        /** Fills missing values from the defaults and drops unknown keys */
        static RarityPreset withDefaults(RarityPreset preset, RarityPreset defaults) {
            RarityPreset result = new RarityPreset();
            result.scores = merge(preset == null ? null : preset.scores, defaults.scores);
            result.maxScore = merge(preset == null ? null : preset.maxScore, defaults.maxScore);
            return result;
        }

        private static Map<String, Integer> merge(Map<String, Integer> values, Map<String, Integer> defaults) {
            Map<String, Integer> merged = new LinkedHashMap<>(defaults);
            if (values != null) merged.replaceAll((key, value) -> {
                Integer configured = values.get(key);
                return configured != null ? configured : value;
            });
            return merged;
        }

        public static String key(Rarity rarity) {
            return rarity.name().toLowerCase();
        }

        public int scoreOf(Rarity rarity) {
            return scores.get(key(rarity));
        }

        public int maxFor(EquipmentMode mode) {
            return maxScore.get(mode.id);
        }
    }
}
