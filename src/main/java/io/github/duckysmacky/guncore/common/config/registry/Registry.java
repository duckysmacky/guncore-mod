package io.github.duckysmacky.guncore.common.config.registry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigLoader;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.SyncRegistryPacket;
import net.minecraftforge.fml.common.Loader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Set of JSON list files (one per {@link RegistryType}) living in one config directory. The server reads the files and
 * pushes them to every client, which only ever holds the synced copy.
 */
public class Registry<T extends Enum<T> & RegistryType> {
    private static final String ID = "Registry";
    private final String id;
    private final String directory;
    private final Class<T> typeClass;
    private final Gson gson;
    private final Map<T, List<? extends RegistryEntry>> entries;

    public Registry(String id, String directory, Class<T> typeClass) {
        this.id = id;
        this.directory = directory;
        this.typeClass = typeClass;
        this.gson = new Gson();
        this.entries = new EnumMap<>(typeClass);
        Arrays.stream(typeClass.getEnumConstants())
            .forEach(type -> entries.put(type, new ArrayList<>()));
    }

    public String getId() {
        return id;
    }

    /** Reads every file of this registry (creating missing ones with defaults) and sends them to all clients */
    public void syncWithClients() {
        GuncoreMod.LOGGER.info(String.format("[%s] Syncing '%s' registry with clients", ID, id));
        ConfigLoader loader = new ConfigLoader(Loader.instance().getConfigDir());

        for (T type : typeClass.getEnumConstants()) {
            String json = loader.readJSON(directory + type.getJsonFile(), () -> type.createDefaults()); // not type::createDefaults: javac erases T to Enum there, which breaks at runtime
            cache(type, json);

            PacketHandler.instance().sendToAll(new SyncRegistryPacket(id, type.ordinal(), json));
        }
    }

    public void cache(int typeOrdinal, String json) {
        cache(typeClass.getEnumConstants()[typeOrdinal], json);
    }

    public void cache(T type, String json) {
        List<? extends RegistryEntry> list = parse(json, type.getEntryClass());
        entries.put(type, list);
        GuncoreMod.LOGGER.info(String.format("[%s] Loaded %s %s: %d entries", ID, id, type.name(), list.size()));
    }

    public <E extends RegistryEntry> List<E> getEntries(T type) {
        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) entries.get(type);
        return list;
    }

    private List<? extends RegistryEntry> parse(String json, Class<? extends RegistryEntry> entryClass) {
        try {
            RegistryEntry[] parsed = gson.fromJson(json, TypeToken.getArray(entryClass).getType());
            return Arrays.stream(parsed)
                .filter(RegistryEntry::isEnabled)
                .collect(Collectors.toList());
        } catch (Exception e) {
            GuncoreMod.LOGGER.error(String.format("[%s] Error parsing '%s' registry JSON: %s", ID, id, e.getMessage()));
            return Collections.emptyList();
        }
    }
}
