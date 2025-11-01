package io.github.duckysmacky.projectg.data.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.data.config.catalog.CatalogEntry;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigLoader {
    public static final String ID = "ConfigLoader";
    private static final String MOD_CONFIG_DIR = "projectg/";
    private final File configDir;
    private final Gson gson;

    public ConfigLoader(File configDir) {
        this.configDir = configDir;
        this.gson = new Gson();
    }

    public <T> String readJSON(String filePath, Supplier<T> defaultValue) {
        File file = getConfigFile(filePath);

        if (!file.exists()) {
            try {
                saveDefaultJSON(file, defaultValue);
            } catch (IOException e) {
                ProjectGMod.LOGGER.error("Failed to create default '{}' config file: {}", file.getName(), e.getMessage());
                return gson.toJson(defaultValue.get());
            }
        }

        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            ProjectGMod.LOGGER.error("Failed to load '{}' config file: {}", file.getName(), e.getMessage());
            return gson.toJson(defaultValue.get());
        }
    }

    private <T> void saveDefaultJSON(File file, Supplier<T> defaultValue) throws IOException {
        boolean status;
        status = file.getParentFile().mkdirs();
        status = file.createNewFile();

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(defaultValue.get(), writer);
        }
    }

    private File getConfigFile(String filePath) {
        return new File(configDir, MOD_CONFIG_DIR + filePath);
    }
}
