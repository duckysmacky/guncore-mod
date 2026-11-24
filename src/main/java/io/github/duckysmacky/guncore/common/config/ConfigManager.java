package io.github.duckysmacky.guncore.common.config;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogManager;
import io.github.duckysmacky.guncore.common.config.registry.Registry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.LoadConfigPacket;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ConfigManager {
    public static final String ID = "ConfigManager";
    private static final String SETTINGS_FILE = "settings.json";
    private static ConfigManager instance;
    private final Gson gson;
    private final CatalogManager catalogManager;
    /** Load order: registries referencing other registries' ids come after them */
    private final List<Registry<?>> registries;
    private Settings settings;

    private ConfigManager() {
        this.gson = new Gson();
        this.catalogManager = new CatalogManager();
        this.registries = Arrays.asList(catalogManager);
        this.settings = Settings.createDefault();
    }

    public static ConfigManager instance() {
        if (instance == null) {
            instance = new ConfigManager();
        }

        return instance;
    }

    public void load() {
        // not getSide(): an integrated server runs in a client JVM, which would make it ask itself for the config forever
        if (FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
            registries.forEach(Registry::syncWithClients);
        } else {
            GuncoreMod.LOGGER.info(String.format("[%s] Requesting config from server", ID));
            PacketHandler.instance().sendToServer(new LoadConfigPacket());
        }

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

    public Registry<?> getRegistry(String id) {
        return registries.stream()
            .filter(registry -> registry.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown config registry: " + id));
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
