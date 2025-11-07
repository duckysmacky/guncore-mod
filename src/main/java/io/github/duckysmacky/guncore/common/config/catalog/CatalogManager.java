package io.github.duckysmacky.guncore.common.config.catalog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigLoader;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.LoadConfigPacket;
import io.github.duckysmacky.guncore.common.network.packets.SyncCatalogPacket;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CatalogManager {
    private static final String ID = "CatalogManager";
    private static final String CATALOG_DIRECTORY = "catalog";
    private final Gson gson;
    private final Map<CatalogType, List<? extends CatalogEntry>> catalogs;

    public CatalogManager() {
        this.gson = new Gson();
        this.catalogs = new EnumMap<>(CatalogType.class);
        Arrays.stream(CatalogType.values())
            .forEach(c -> catalogs.put(c, new ArrayList<>()));
    }

    public void load() {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            syncWithClients();
        } else {
            GuncoreMod.LOGGER.info(String.format("[%s] Requesting catalog config from server", ID));
            PacketHandler.instance().sendToServer(new LoadConfigPacket());
        }
    }

    private void syncWithClients() {
        GuncoreMod.LOGGER.info(String.format("[%s] Syncing catalog config with clients", ID));
        ConfigLoader loader = new ConfigLoader(Loader.instance().getConfigDir());

        Arrays.stream(CatalogType.values())
            .forEach(catalog -> {
                String filePath = CATALOG_DIRECTORY + catalog.jsonFile;
                Supplier<List<? extends CatalogEntry>> defaultValueSupplier = () -> Collections.singletonList(catalog.exampleSupplier.get());

                String json = loader.readJSON(filePath, defaultValueSupplier);
                cacheCatalog(catalog, json);

                PacketHandler.instance().sendToAll(new SyncCatalogPacket(catalog, json));
            });
    }

    public void cacheCatalog(CatalogType type, String json) {
        catalogs.put(type, parseCatalogJson(json, type.entryClass));
    }

    public <T extends CatalogEntry> List<T> getCatalog(CatalogType type) {
        @SuppressWarnings("unchecked")
        List<T> list = (List<T>) catalogs.get(type);
        return list;
    }

    private <T extends CatalogEntry> List<T> parseCatalogJson(String json, Class<T> entryClass) {
        @SuppressWarnings("unchecked")
        Type entryArrayType = TypeToken.getArray(entryClass).getType();

        try {
            T[] entries = gson.fromJson(json, entryArrayType);
            return Arrays.stream(entries)
                .filter(CatalogEntry::isEnabled)
                .collect(Collectors.toList());
        } catch (Exception e) {
            GuncoreMod.LOGGER.error(String.format("Error parsing catalog JSON: %s", e.getMessage()));
            return Collections.emptyList();
        }
    }
}
