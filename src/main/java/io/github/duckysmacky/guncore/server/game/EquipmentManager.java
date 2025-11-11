package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.common.config.catalog.entries.ArmorEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.EquippableEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.ItemEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.GunEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import net.minecraft.server.level.ServerPlayer;

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

    public PlayerEquipment getEquipment(ServerPlayer player) {
        UUID uuid = player.getUUID();

        return playerEquipment.computeIfAbsent(uuid, u -> new PlayerEquipment());
    }

    public static class PlayerEquipment {
        private GunEntry mainWeapon;
        private GunEntry secondaryWeapon;
        private ItemEntry lethalEquipment;
        private ItemEntry tacticalEquipment;
        private ItemEntry gadget;
        private ItemEntry utilityItem;
        private ItemEntry consumableItem;
        private ItemEntry perk;
        private ArmorEntry armor;

        public PlayerEquipment() {}

        public void setEquipment(EquipmentType type, EquippableEntry equipment) {
            switch (type) {
                case MAIN_WEAPON -> mainWeapon = (GunEntry) equipment;
                case SECONDARY_WEAPON -> secondaryWeapon = (GunEntry) equipment;
                case LETHAL -> lethalEquipment = (ItemEntry) equipment;
                case TACTICAL -> tacticalEquipment = (ItemEntry) equipment;
                case GADGET -> gadget = (ItemEntry) equipment;
                case UTILITY -> utilityItem = (ItemEntry) equipment;
                case CONSUMABLE -> consumableItem = (ItemEntry) equipment;
                case PERK -> perk = (ItemEntry) equipment;
                case ARMOR -> armor = (ArmorEntry) equipment;
            };
        }

        public EquippableEntry getEquipment(EquipmentType type) {
            return switch (type) {
                case MAIN_WEAPON -> mainWeapon;
                case SECONDARY_WEAPON -> secondaryWeapon;
                case LETHAL -> lethalEquipment;
                case TACTICAL -> tacticalEquipment;
                case GADGET -> gadget;
                case UTILITY -> utilityItem;
                case CONSUMABLE -> consumableItem;
                case PERK -> perk;
                case ARMOR -> armor;
            };
        }
    }
}
