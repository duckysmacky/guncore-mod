package io.github.duckysmacky.projectg.data.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.data.config.catalog.CatalogEntry;
import io.github.duckysmacky.projectg.data.config.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.projectg.data.config.catalog.guns.GunEntry;
import io.github.duckysmacky.projectg.data.config.catalog.kits.KitEntry;
import io.github.duckysmacky.projectg.data.config.catalog.locations.LocationEntry;
import io.github.duckysmacky.projectg.network.PacketHandler;
import io.github.duckysmacky.projectg.network.packets.LoadConfigPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
    private static final String ID = "ConfigLoader";
    private static final String MOD_CONFIG_DIR = "projectg/";
    private static ConfigLoader instance;
    private final Gson gson;
    private GameConfig cachedGameConfig;
    private List<GunEntry> cachedGuns;
    private List<KitEntry> cachedKits;
    private List<GadgetEntry> cachedGadgets;
    private List<LocationEntry> cachedLocations;

    private ConfigLoader() {
        this.gson = new Gson();
        this.cachedGameConfig = GameConfig.createDefault();
        this.cachedGuns = Collections.emptyList();
        this.cachedKits = Collections.emptyList();
        this.cachedGadgets = Collections.emptyList();
        this.cachedLocations = Collections.emptyList();
    }

    public static ConfigLoader instance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }

        return instance;
    }

    public void loadConfig() {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            loadConfigFromServer(server);
        } else {
            ProjectGMod.LOGGER.info("[{}] Loading config from server: {}", ID);
            PacketHandler.instance().sendToServer(new LoadConfigPacket());
        }
    }

    public void loadConfigFromServer(MinecraftServer server) {
        File configDir = Loader.instance().getConfigDir();

        this.cachedGameConfig = loadGameConfig(getConfigFile(configDir, "game.json"));

        this.cachedGuns = loadCatalogEntries(getConfigFile(configDir, "catalog/guns.json"), GunEntry.class, GunEntry::createExample);
        this.cachedKits = loadCatalogEntries(getConfigFile(configDir, "catalog/kits.json"), KitEntry.class, KitEntry::createExample);
        this.cachedGadgets = loadCatalogEntries(getConfigFile(configDir, "catalog/gadgets.json"), GadgetEntry.class, GadgetEntry::createExample);
        this.cachedLocations = loadCatalogEntries(getConfigFile(configDir, "catalog/locations.json"), LocationEntry.class, LocationEntry::createExample);
    }

    public void setCachedGameConfig(GameConfig gameConfig) {
        this.cachedGameConfig = gameConfig;
    }

    public void setCachedGuns(List<GunEntry> guns) {
        this.cachedGuns = guns;
    }

    public void setCachedKits(List<KitEntry> kits) {
        this.cachedKits = kits;
    }

    public void setCachedGadgets(List<GadgetEntry> gadgets) {
        this.cachedGadgets = gadgets;
    }

    public void setCachedLocations(List<LocationEntry> locations) {
        this.cachedLocations = locations;
    }

    public GameConfig getCachedGameConfig() {
        return cachedGameConfig;
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

    private File getConfigFile(File configDir, String filePath) {
        return new File(configDir, MOD_CONFIG_DIR + filePath);
    }

    private GameConfig loadGameConfig(File file) {
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

    private <T extends CatalogEntry> List<T> loadCatalogEntries(File file, Class<T> entryClass, Supplier<T> defaultEntry) {
        if (!file.exists()) {
            try {
                saveDefaultEntries(file, entryClass, defaultEntry);
            } catch (IOException e) {
                ProjectGMod.LOGGER.error("Failed to create default '{}' config file: {}", file.getName(), e.getMessage());
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
            ProjectGMod.LOGGER.error("Failed to load '{}' config file: {}", file.getName(), e.getMessage());
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
