package io.github.duckysmacky.guncore.common.config;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogManager;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.LoadConfigPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

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
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            GuncoreMod.LOGGER.info("[{}] Loading config", ID);
            ConfigLoader loader = new ConfigLoader();

            catalogManager.load(loader);

            String gameConfigJson = loader.readJSON("game.json", GameConfig::createDefault);
            gameConfig = parseConfigJson(gameConfigJson, GameConfig.class, GameConfig::createDefault);
        } else {
            GuncoreMod.LOGGER.info("[{}] Requesting config from server", ID);
            PacketHandler.CHANNEL.sendToServer(new LoadConfigPacket());
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
