package io.github.duckysmacky.guncore.common.config;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.GuncoreMod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class ConfigLoader {
    public static final String ID = "ConfigLoader";
    private static final String MOD_CONFIG_DIR = "guncore/";
    private final Path configPath;
    private final Gson gson;

    public ConfigLoader() {
        this.configPath = FMLPaths.CONFIGDIR.get();
        this.gson = new Gson();
    }

    public <T> String readJSON(String filePath, Supplier<T> defaultValue) {
        File file = configPath.resolve(MOD_CONFIG_DIR + filePath).toFile();

        if (!file.exists()) {
            try {
                saveDefaultJSON(file, defaultValue);
            } catch (IOException e) {
                GuncoreMod.LOGGER.error("[{}] Failed to create default '{}' config file: {}", ID, file.getName(), e.getMessage());
                return gson.toJson(defaultValue.get());
            }
        }

        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            GuncoreMod.LOGGER.error("[{}] Failed to load '{}' config file: {}", ID, file.getName(), e.getMessage());
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
}
