package io.github.duckysmacky.guncore.common.config;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogManager;
import net.minecraftforge.fml.common.Loader;

import java.util.function.Supplier;

public class ConfigManager {
    public static final String ID = "ConfigManager";
    private static final String SETTINGS_FILE = "settings.json";
    private static ConfigManager instance;
    private final Gson gson;
    private final CatalogManager catalogManager;
    private Settings settings;

    private ConfigManager() {
        this.gson = new Gson();
        this.catalogManager = new CatalogManager();
        this.settings = Settings.createDefault();
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

        String settingsJson = loader.readJSON(SETTINGS_FILE, Settings::createDefault);
        Settings parsedSettings = parseConfigJson(settingsJson, Settings.class, Settings::createDefault);
        settings = Settings.withDefaults(parsedSettings);

        // write back so values added in newer versions show up in the file
        if (!gson.toJson(settings).equals(gson.toJson(parsedSettings))) {
            GuncoreMod.LOGGER.warn("[{}] '{}' had missing or invalid values, filled them in with defaults", ID, SETTINGS_FILE);
            saveSettings();
        }
    }

    public void saveSettings() {
        new ConfigLoader(Loader.instance().getConfigDir()).writeJSON(SETTINGS_FILE, settings);
    }

    public CatalogManager getCatalogManager() {
        return catalogManager;
    }

    public Settings getSettings() {
        return settings;
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
