package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.common.config.catalog.items.ItemEntry;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
        private GunEntry mainWeapon;
        private GunEntry secondaryWeapon;
        private ItemEntry gadget;

        public PlayerEquipment() {}

        public void setMainWeapon(GunEntry mainWeapon) {
            this.mainWeapon = mainWeapon;
        }

        public void setSecondaryWeapon(GunEntry secondaryWeapon) {
            this.secondaryWeapon = secondaryWeapon;
        }

        public void setGadget(ItemEntry gadget) {
            this.gadget = gadget;
        }

        public Optional<GunEntry> getMainWeapon() {
            return Optional.ofNullable(mainWeapon);
        }

        public Optional<GunEntry> getSecondaryWeapon() {
            return Optional.ofNullable(secondaryWeapon);
        }

        public Optional<ItemEntry> getGadget() {
            return Optional.ofNullable(gadget);
        }
    }
}
