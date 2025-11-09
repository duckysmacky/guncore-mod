package io.github.duckysmacky.guncore.common.config.catalog.kits;

import com.google.gson.annotations.SerializedName;
import net.minecraft.ChatFormatting;

public enum KitTier {
    @SerializedName("basic")
    BASIC("Basic", ChatFormatting.AQUA, 0),
    @SerializedName("advanced")
    ADVANCED("Advanced", ChatFormatting.BLUE, 1),
    @SerializedName("complex")
    COMPLEX("Complex", ChatFormatting.LIGHT_PURPLE, 2),
    @SerializedName("professional")
    PROFESSIONAL("Professional",ChatFormatting.DARK_PURPLE, 3),
    @SerializedName("forbidden")
    FORBIDDEN("Forbidden", ChatFormatting.DARK_RED, 4),
    @SerializedName("special")
    SPECIAL("Special", ChatFormatting.WHITE, 5);

    public final String display;
    public final ChatFormatting color;
    public final int sortOrder;

    KitTier(String display, ChatFormatting color, int sortOrder) {
        this.display = display;
        this.color = color;
        this.sortOrder = sortOrder;
    }
}
