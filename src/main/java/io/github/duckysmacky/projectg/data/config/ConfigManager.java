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
import io.github.duckysmacky.projectg.network.packets.SyncConfigPacket;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {
    public static final String ID = "ConfigManager";
    private static ConfigManager instance;
    private final Gson gson;
    private GameConfig cachedGameConfig;
    private List<GunEntry> cachedGuns;
    private List<KitEntry> cachedKits;
    private List<GadgetEntry> cachedGadgets;
    private List<LocationEntry> cachedLocations;

    private ConfigManager() {
        this.gson = new Gson();
        this.cachedGameConfig = GameConfig.createDefault();
        this.cachedGuns = Collections.emptyList();
        this.cachedKits = Collections.emptyList();
        this.cachedGadgets = Collections.emptyList();
        this.cachedLocations = Collections.emptyList();
    }

    public static ConfigManager instance() {
        if (instance == null) {
            instance = new ConfigManager();
        }

        return instance;
    }

    public void loadConfig() {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            syncWithClients();
        } else {
            ProjectGMod.LOGGER.info(String.format("[%s] Requesting config from server", ID));
            PacketHandler.instance().sendToServer(new LoadConfigPacket());
        }
    }

    public void syncWithClients() {
        ProjectGMod.LOGGER.info(String.format("[%s] Syncing config with clients", ID));
        ConfigLoader loader = new ConfigLoader(Loader.instance().getConfigDir());

        String gameConfigJson = loader.readJSON("game.json", GameConfig::createDefault);
        String gunsJson = loader.readJSON("catalog/guns.json", () -> Collections.singletonList(GunEntry.createExample()));
        String kitsJson = loader.readJSON("catalog/kits.json", () -> Collections.singletonList(KitEntry.createExample()));
        String gadgetsJson = loader.readJSON("catalog/gadgets.json", () -> Collections.singletonList(GadgetEntry.createExample()));
        String locationsJson = loader.readJSON("catalog/locations.json", () -> Collections.singletonList(LocationEntry.createExample()));

        cacheGameConfig(gameConfigJson);
        cacheGuns(gunsJson);
        cacheKits(kitsJson);
        cacheGadgets(gadgetsJson);
        cacheLocations(locationsJson);

        SyncConfigPacket syncPacket = new SyncConfigPacket(
            gameConfigJson,
            gunsJson,
            kitsJson,
            gadgetsJson,
            locationsJson
        );

        PacketHandler.instance().sendToAll(syncPacket);
    }

    public void cacheGameConfig(String json) {
        try {
           cachedGameConfig = gson.fromJson(json, GameConfig.class);
        } catch (Exception e) {
            ProjectGMod.LOGGER.error(String.format("[%s] Error parsing JSON: %s", ID, e.getMessage()));
            cachedGameConfig = GameConfig.createDefault();
        }
    }

    public void cacheGuns(String json) {
        cachedGuns = parseJsonAs(json, GunEntry.class);
    }

    public void cacheKits(String json) {
        cachedKits = parseJsonAs(json, KitEntry.class);
    }

    public void cacheGadgets(String json) {
        cachedGadgets = parseJsonAs(json, GadgetEntry.class);
    }

    public void cacheLocations(String json) {
        cachedLocations = parseJsonAs(json, LocationEntry.class);
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

    private <T extends CatalogEntry> List<T> parseJsonAs(String json, Class<T> entryClass) {
        @SuppressWarnings("unchecked")
        Type entryArrayType = TypeToken.getArray(entryClass).getType();

        try {
            T[] entries = gson.fromJson(json, entryArrayType);
            return Arrays.stream(entries)
                .filter(CatalogEntry::isEnabled)
                .collect(Collectors.toList());
        } catch (Exception e) {
            ProjectGMod.LOGGER.error(String.format("[%s] Error parsing JSON: %s", ID, e.getMessage()));
            return Collections.emptyList();
        }
    }
}
