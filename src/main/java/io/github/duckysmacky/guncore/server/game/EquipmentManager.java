package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EquipmentManager {
    private static EquipmentManager instance;
    private final Map<UUID, PlayerEquipment> playerEquipment;

    private EquipmentManager() {
        this.playerEquipment = new HashMap<>();
    }

    public static EquipmentManager instance() {
        if (instance == null) {
            instance = new EquipmentManager();
        }

        return instance;
    }

    public PlayerEquipment getEquipment(EntityPlayer player) {
        UUID uuid = player.getUniqueID();

        return playerEquipment.computeIfAbsent(uuid, u -> new PlayerEquipment());
    }

    public static class PlayerEquipment {
        private final Map<EquipmentType, CatalogEntry> equipped = new EnumMap<>(EquipmentType.class);

        public void set(EquipmentType type, CatalogEntry entry) {
            equipped.put(type, entry);
        }

        @SuppressWarnings("unchecked")
        public <T extends CatalogEntry> T get(EquipmentType type) {
            return (T) equipped.get(type);
        }

        public Map<EquipmentType, CatalogEntry> getAll() {
            return equipped;
        }

        public void clear() {
            equipped.clear();
        }
    }
}
