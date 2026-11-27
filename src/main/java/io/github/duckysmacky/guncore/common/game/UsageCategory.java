package io.github.duckysmacky.guncore.common.game;

/** What a player started a round with; each category gets its own top list in the profile */
public enum UsageCategory {
    KIT("kit", "Kits"),
    WEAPON("weapon", "Weapons"),
    ATTACHMENT("attachment", "Attachments"),
    LETHAL("lethal", "Lethals"),
    TACTICAL("tactical", "Tacticals"),
    GADGET("gadget", "Gadgets"),
    UTILITY("utility", "Utility"),
    CONSUMABLE("consumable", "Consumables"),
    ARMOR("armor", "Armor"),
    GAME_MODE("mode", "Game modes");

    /** Stored in the database, never change */
    public final String id;
    public final String display;

    UsageCategory(String id, String display) {
        this.id = id;
        this.display = display;
    }

    public static UsageCategory of(EquipmentType type) {
        switch (type) {
            case MAIN_WEAPON: case SECONDARY_WEAPON: return WEAPON;
            case LETHAL: return LETHAL;
            case TACTICAL: return TACTICAL;
            case GADGET: return GADGET;
            case UTILITY_1: case UTILITY_2: return UTILITY;
            case CONSUMABLE: return CONSUMABLE;
            case ARMOR: return ARMOR;
            default: throw new IllegalArgumentException("No usage category for " + type);
        }
    }

    public static UsageCategory byId(String id) {
        for (UsageCategory category : values())
            if (category.id.equals(id)) return category;
        return null;
    }
}
