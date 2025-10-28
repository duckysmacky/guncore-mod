package io.github.duckysmacky.projectg.config.catalog;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.text.TextFormatting;

public enum Rarity {
    @SerializedName("common")
    COMMON("Common", TextFormatting.WHITE, 0),
    @SerializedName("uncommon")
    UNCOMMON("Uncommon", TextFormatting.GREEN, 1),
    @SerializedName("rare")
    RARE("Rare", TextFormatting.BLUE, 2),
    @SerializedName("epic")
    EPIC("Epic", TextFormatting.DARK_PURPLE, 3),
    @SerializedName("legendary")
    LEGENDARY("Legendary", TextFormatting.GOLD, 4),
    @SerializedName("mythic")
    MYTHIC("Mythic", TextFormatting.DARK_RED, 5);

    public final String display;
    public final TextFormatting color;
    public final int sortOrder;

    Rarity(String display, TextFormatting color, int sortOrder) {
        this.display = display;
        this.color = color;
        this.sortOrder = sortOrder;
    }
}
