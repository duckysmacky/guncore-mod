package io.github.duckysmacky.projectg.config;

import com.google.gson.Gson;
import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.config.catalog.GunEntry;
import io.github.duckysmacky.projectg.config.catalog.GunCategory;
import io.github.duckysmacky.projectg.config.catalog.Rarity;
import net.minecraftforge.fml.common.Loader;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigLoader {
    private static final String CONFIG_DIR_NAME = "projectg/";
    private static ConfigLoader instance;
    private final Gson gson;
    private File configDir;
    private List<GunEntry> cachedGuns;

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
        this.cachedGuns = loadGuns();
    }

    public List<GunEntry> getCachedGuns() {
        return cachedGuns;
    }

    private File getConfigFile(String fileName) {
        return new File(configDir, CONFIG_DIR_NAME + fileName);
    }

    private List<GunEntry> loadGuns() {
        File file = getConfigFile("guns.json");

        if (!file.exists()) {
            try {
                saveDefaultGuns(file);
            } catch (IOException e) {
                ProjectGMod.LOGGER.error("Failed to create default guns config file: {}", e.getMessage());
                return Collections.emptyList();
            }
        }

        try (Reader reader = new FileReader(file)) {
            GunEntry[] entries = gson.fromJson(reader, GunEntry[].class);

            return Arrays.stream(entries)
                .filter(GunEntry::isEnabled)
                .collect(Collectors.toList());
        } catch (Exception e) {
            ProjectGMod.LOGGER.error("Failed to load guns config file: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private void saveDefaultGuns(File file) throws IOException {
        boolean status;
        status = file.getParentFile().mkdirs();
        status = file.createNewFile();

        GunEntry[] defaultGuns = new GunEntry[]{
            new GunEntry(
                true,
                "SOCOM M4A1",
                GunCategory.ASSAULT_RIFLE,
                Rarity.COMMON,
                "mw:socom_m4a1",
                "mw:socom_mag",
                12
            )
        };

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(defaultGuns, writer);
        }
    }
}
