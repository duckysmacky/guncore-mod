package io.github.duckysmacky.projectg.data.config;

import com.google.gson.Gson;
import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.data.config.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.projectg.data.config.catalog.guns.GunEntry;
import io.github.duckysmacky.projectg.data.config.catalog.kits.KitEntry;
import io.github.duckysmacky.projectg.data.config.catalog.locations.LocationEntry;
import io.github.duckysmacky.projectg.network.PacketHandler;
import io.github.duckysmacky.projectg.network.packets.SyncConfigPacket;

import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.function.Supplier;

public class ConfigLoader {
    public static final String ID = "ConfigLoader";
    private static final String MOD_CONFIG_DIR = "projectg/";
    private final File configDir;
    private final Gson gson;

    public ConfigLoader(File configDir) {
        this.configDir = configDir;
        this.gson = new Gson();
    }

    public void syncWithClients() {
        ProjectGMod.LOGGER.info(String.format("[%s] Syncing config with clients", ID));

        SyncConfigPacket syncPacket = new SyncConfigPacket(
            readJSON("game.json", GameConfig::createDefault),
            readJSON("catalog/guns.json", () -> Collections.singletonList(GunEntry.createExample())),
            readJSON("catalog/kits.json", () -> Collections.singletonList(KitEntry.createExample())),
            readJSON("catalog/gadgets.json", () -> Collections.singletonList(GadgetEntry.createExample())),
            readJSON("catalog/locations.json", () -> Collections.singletonList(LocationEntry.createExample()))
        );

        PacketHandler.instance().sendToAll(syncPacket);
    }

    private <T> String readJSON(String filePath, Supplier<T> defaultValue) {
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
