package io.github.duckysmacky.projectg.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.config.catalog.*;
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
    private static final String CONFIG_DIR_NAME = "projectg/";
    private static ConfigLoader instance;
    private final Gson gson;
    private File configDir;
    private List<GunEntry> cachedGuns;
    private List<KitEntry> cachedKits;
    private List<GadgetEntry> cachedGadgets;
    private List<LocationEntry> cachedLocations;

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
        this.cachedGuns = loadCatalogEntries("guns.json", GunEntry.class, GunEntry::getExample);
        this.cachedKits = loadCatalogEntries("kits.json", KitEntry.class, KitEntry::getExample);
        this.cachedGadgets = loadCatalogEntries("gadgets.json", GadgetEntry.class, GadgetEntry::getExample);
        this.cachedLocations = loadCatalogEntries("locations.json", LocationEntry.class, LocationEntry::getExample);
    }

    public List<GunEntry> getCachedGuns() {
        return cachedGuns;
    }

    public List<KitEntry> getCachedKits() {
        return cachedKits;
    }

    public List<GadgetEntry> getCachedGadgets() {
        return cachedGadgets;
    }

    public List<LocationEntry> getCachedLocations() {
        return cachedLocations;
    }

    private File getConfigFile(String fileName) {
        return new File(configDir, CONFIG_DIR_NAME + fileName);
    }

    private <T extends CatalogEntry> List<T> loadCatalogEntries(String fileName, Class<T> entryClass, Supplier<T> defaultEntry) {
        File file = getConfigFile(fileName);

        if (!file.exists()) {
            try {
                saveDefaultEntries(file, entryClass, defaultEntry);
            } catch (IOException e) {
                ProjectGMod.LOGGER.error("Failed to create default '{}' config file: {}", fileName, e.getMessage());
                return Collections.emptyList();
            }
        }

        try (Reader reader = new FileReader(file)) {
            @SuppressWarnings("unchecked")
            Type entryArrayType = TypeToken.getArray(entryClass).getType();

            T[] entries = gson.fromJson(reader, entryArrayType);

            return Arrays.stream(entries)
                .filter(CatalogEntry::isEnabled)
                .collect(Collectors.toList());
        } catch (Exception e) {
            ProjectGMod.LOGGER.error("Failed to load '{}' config file: {}", fileName, e.getMessage());
            return Collections.emptyList();
        }

    }

    private <T extends CatalogEntry> void saveDefaultEntries(File file, Class<T> entryClass, Supplier<T> defaultEntry) throws IOException {
        boolean status;
        status = file.getParentFile().mkdirs();
        status = file.createNewFile();

        @SuppressWarnings("unchecked")
        T[] defaultEntries = (T[]) Array.newInstance(entryClass, 1);
        defaultEntries[0] = defaultEntry.get();

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(defaultEntries, writer);
        }
    }
}
