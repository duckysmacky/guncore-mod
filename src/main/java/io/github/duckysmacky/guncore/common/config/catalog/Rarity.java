package io.github.duckysmacky.guncore.common.config.catalog;

import com.google.gson.annotations.SerializedName;
import net.minecraft.ChatFormatting;

public enum Rarity {
    @SerializedName("common")
    COMMON("Common", ChatFormatting.WHITE, 0),
    @SerializedName("uncommon")
    UNCOMMON("Uncommon", ChatFormatting.GREEN, 1),
    @SerializedName("rare")
    RARE("Rare", ChatFormatting.BLUE, 2),
    @SerializedName("epic")
    EPIC("Epic", ChatFormatting.DARK_PURPLE, 3),
    @SerializedName("legendary")
    LEGENDARY("Legendary", ChatFormatting.GOLD, 4),
    @SerializedName("mythic")
    MYTHIC("Mythic", ChatFormatting.DARK_RED, 5),
    @SerializedName("secret")
    SECRET("Secret", ChatFormatting.DARK_GRAY, 6);

    public final String display;
    public final ChatFormatting color;
    public final int sortOrder;

    Rarity(String display, ChatFormatting color, int sortOrder) {
        this.display = display;
        this.color = color;
        this.sortOrder = sortOrder;
    }
}
