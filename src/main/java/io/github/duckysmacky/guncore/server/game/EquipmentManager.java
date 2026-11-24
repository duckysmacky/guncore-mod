package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.EquippableEntry;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.ToIntFunction;

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

    /** Forgets every player's selected equipment, items in inventories stay */
    public void clearAll() {
        playerEquipment.clear();
    }

    /** Returns null when the player never selected anything */
    public PlayerEquipment getEquipment(UUID uuid) {
        return playerEquipment.get(uuid);
    }

    public static class PlayerEquipment {
        private final Map<EquipmentType, CatalogEntry> equipped = new EnumMap<>(EquipmentType.class);
        /** Kit of the kit-based mode; the rest of the kit's items are not tracked */
        private KitEntry kit;

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

        public KitEntry getKit() {
            return kit;
        }

        public void setKit(KitEntry kit) {
            this.kit = kit;
        }

        public void clear() {
            equipped.clear();
            kit = null;
        }

        /** Total rarity score of the kit and everything selected */
        public int rarityScore(ToIntFunction<Rarity> cost) {
            int score = kit != null ? cost.applyAsInt(kit.getRarity()) : 0;
            for (CatalogEntry entry : equipped.values())
                score += cost.applyAsInt(((EquippableEntry) entry).getRarity());
            return score;
        }
    }
}
