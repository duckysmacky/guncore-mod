package io.github.duckysmacky.guncore.data.config.catalog.kits;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.text.TextFormatting;

public enum KitTier {
    @SerializedName("basic")
    BASIC("Basic", TextFormatting.AQUA, 0),
    @SerializedName("advanced")
    ADVANCED("Advanced", TextFormatting.BLUE, 1),
    @SerializedName("complex")
    COMPLEX("Complex", TextFormatting.LIGHT_PURPLE, 2),
    @SerializedName("professional")
    PROFESSIONAL("Professional",TextFormatting.DARK_PURPLE, 3),
    @SerializedName("forbidden")
    FORBIDDEN("Forbidden", TextFormatting.DARK_RED, 4),
    @SerializedName("special")
    SPECIAL("Special", TextFormatting.WHITE, 5);

    public final String display;
    public final TextFormatting color;
    public final int sortOrder;

    KitTier(String display, TextFormatting color, int sortOrder) {
        this.display = display;
        this.color = color;
        this.sortOrder = sortOrder;
    }
}
