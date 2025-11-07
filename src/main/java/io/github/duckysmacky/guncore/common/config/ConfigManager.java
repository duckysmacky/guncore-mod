package io.github.duckysmacky.guncore.common.config;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogManager;
import net.minecraftforge.fml.common.Loader;

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
        catalogManager.load();

        ConfigLoader loader = new ConfigLoader(Loader.instance().getConfigDir());

        String gameConfigJson = loader.readJSON("game.json", GameConfig::createDefault);
        gameConfig = parseConfigJson(gameConfigJson, GameConfig.class, GameConfig::createDefault);
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
            GuncoreMod.LOGGER.error(String.format("[%s] Error parsing '%s' JSON: %s", ID, configClass.getName(), e.getMessage()));
            return defaultValue.get();
        }
    }
}
