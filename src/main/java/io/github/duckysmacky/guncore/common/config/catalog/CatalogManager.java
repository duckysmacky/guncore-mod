package io.github.duckysmacky.guncore.common.config.catalog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigLoader;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.CacheCatalogPacket;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class CatalogManager {
    private static final String ID = "CatalogManager";
    private static final String CATALOG_DIRECTORY = "catalog/";
    private final Gson gson;
    private final Map<CatalogType, List<? extends CatalogEntry>> catalogs;

    public CatalogManager() {
        this.gson = new Gson();
        this.catalogs = new EnumMap<>(CatalogType.class);
        Arrays.stream(CatalogType.values())
            .forEach(c -> catalogs.put(c, new ArrayList<>()));
    }

    public void load(ConfigLoader loader) {
        if (ServerLifecycleHooks.getCurrentServer() == null) return;
        GuncoreMod.LOGGER.info("[{}] Syncing catalog config with clients", ID);

        for (CatalogType catalog : CatalogType.values()) {
            String filePath = CATALOG_DIRECTORY + catalog.jsonFile;
            String json = loader.readJSON(filePath, () -> List.of(catalog.exampleSupplier.get()));

            cacheCatalog(catalog, json);
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new CacheCatalogPacket(catalog, json));
        }
    }

    public void cacheCatalog(CatalogType type, String json) {
        List<? extends CatalogEntry> catalog = parseCatalogJson(json, type.entryClass);
        catalogs.put(type, catalog);
        GuncoreMod.LOGGER.info("[{}] Loaded {} catalog: {} entries", ID, type.name(), catalog.size());
    }

    public <T extends CatalogEntry> List<T> getCatalog(CatalogType type) {
        @SuppressWarnings("unchecked")
        List<T> list = (List<T>) catalogs.get(type);
        return list;
    }

    private <T extends CatalogEntry> List<T> parseCatalogJson(String json, Class<T> entryClass) {
        Type entryArrayType = TypeToken.getArray(entryClass).getType();

        try {
            T[] entries = gson.fromJson(json, entryArrayType);
            return Arrays.stream(entries)
                .filter(CatalogEntry::isEnabled)
                .collect(Collectors.toList());
        } catch (Exception e) {
            GuncoreMod.LOGGER.error("[{}] Error parsing catalog JSON: {}", ID, e.getMessage());
            return Collections.emptyList();
        }
    }
}
