package io.github.duckysmacky.guncore.common.game;

public enum EquipmentType {
    MAIN_WEAPON(0),
    SECONDARY_WEAPON(1),
    LETHAL(2),
    TACTICAL(3),
    GADGET(4),
    UTILITY(5),
    CONSUMABLE(6),
    PERK(16),
    ARMOR(36);

    public final int slot;

    EquipmentType(int slot) {
        this.slot = slot;
    }

    enum ArmorType {
        HELMET(39),
        CHESTPLATE(38),
        LEGGINGS(37),
        BOOTS(36);

        public final int slot;

        ArmorType(int slot) {
            this.slot = slot;
        }
    }
}
