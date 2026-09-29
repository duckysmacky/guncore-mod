package io.github.duckysmacky.guncore.common.config;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogManager;

import java.util.function.Supplier;

public class ConfigManager {
    public static final String ID = "ConfigManager";
    private static ConfigManager instance;
    private final Gson gson;
    private final CatalogManager catalogManager;
    private GameConfig gameConfig;

    private ConfigManager() {
        this.gson = new Gson();
        this.catalogManager = new CatalogManager();
        this.gameConfig = GameConfig.createDefault();
    }

    public static ConfigManager instance() {
        if (instance == null) {
            instance = new ConfigManager();
        }

        return instance;
    }

    public void load() {
        GuncoreMod.LOGGER.info("[{}] Loading config", ID);
        ConfigLoader loader = new ConfigLoader();

        catalogManager.load(loader);

        String gameConfigJson = loader.readJSON("game.json", GameConfig::createDefault);
        GameConfig parsedConfig = parseConfigJson(gameConfigJson, GameConfig.class, GameConfig::createDefault);
        gameConfig = GameConfig.withDefaults(parsedConfig);

        // write back so values added in newer versions (e.g. killTarget) show up in the file
        if (!gson.toJson(gameConfig).equals(gson.toJson(parsedConfig))) {
            GuncoreMod.LOGGER.warn("[{}] 'game.json' had missing or invalid values, filled them in with defaults", ID);
            loader.writeJSON("game.json", gameConfig);
        }
    }

    public CatalogManager getCatalogManager() {
        return catalogManager;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    private <T> T parseConfigJson(String json, Class<T> configClass, Supplier<T> defaultValue) {
        try {
            return gson.fromJson(json, configClass);
        } catch (Exception e) {
            GuncoreMod.LOGGER.error("[{}] Error parsing '{}' JSON: {}", ID, configClass.getName(), e.getMessage());
            return defaultValue.get();
        }
    }
}
