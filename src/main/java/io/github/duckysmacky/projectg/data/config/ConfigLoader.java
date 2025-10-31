package io.github.duckysmacky.projectg.data.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.data.catalog.CatalogEntry;
import io.github.duckysmacky.projectg.data.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.projectg.data.catalog.guns.GunEntry;
import io.github.duckysmacky.projectg.data.catalog.kits.KitEntry;
import io.github.duckysmacky.projectg.data.catalog.locations.LocationEntry;
import net.minecraftforge.fml.common.Loader;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigLoader {
    private static final String CONFIG_DIRECTORY_NAME = "projectg/";
    private static ConfigLoader instance;
    private final Gson gson;
    private final File configDir;
    private GameConfig cachedGameConfig;

    private ConfigLoader() {
        this.gson = new Gson();
        this.configDir = Loader.instance().getConfigDir();
    }

    public static ConfigLoader instance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }

        return instance;
    }

    public void loadConfig() {
        this.cachedGameConfig = loadGameConfig();
    }

    public GameConfig getCachedGameConfig() {
        return cachedGameConfig;
    }

    private File getConfigFile(String fileName) {
        return new File(configDir, CONFIG_DIRECTORY_NAME + fileName);
    }

    private GameConfig loadGameConfig() {
        File file = getConfigFile("game.json");

        if (!file.exists()) {
            try {
                saveDefaultGameConfig(file);
            } catch (IOException e) {
                ProjectGMod.LOGGER.error("Failed to create default 'game.json' config file: {}", e.getMessage());
                return GameConfig.createDefault();
            }
        }

        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, GameConfig.class);
        } catch (Exception e) {
            ProjectGMod.LOGGER.error("Failed to load 'game.json' config file: {}", e.getMessage());
            return GameConfig.createDefault();
        }
    }

    private void saveDefaultGameConfig(File file) throws IOException {
        boolean status;
        status = file.getParentFile().mkdirs();
        status = file.createNewFile();

        GameConfig defaultConfig = GameConfig.createDefault();

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(defaultConfig, writer);
        }
    }
}
