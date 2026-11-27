package io.github.duckysmacky.guncore.common.game;

import com.google.gson.annotations.SerializedName;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;

/** Rules for combining equipment into a loadout */
public enum LoadoutSystem {
    @SerializedName("flexible") FLEXIBLE("flexible", "Flexible"),
    @SerializedName("rarity_score") RARITY_SCORE("rarity_score", "Rarity Score");

    /** Ids of the chat prompts editing the selected rarity preset, see GameManager.registerPrompts */
    public static final String PRESET_PROMPT_ID = "rarity_preset";
    public static final String MAX_ATTACHMENTS_PROMPT_ID = "max_attachments";

    public final String id;
    public final String display;

    LoadoutSystem(String id, String display) {
        this.id = id;
        this.display = display;
    }

    public static String scorePromptId(Rarity rarity) {
        return "rarity_score." + rarity.name().toLowerCase();
    }

    public static String maxScorePromptId(EquipmentMode mode) {
        return "max_rarity_score." + mode.id;
    }
}
