package io.github.duckysmacky.guncore.common.config.catalog.kits;

import com.google.gson.annotations.SerializedName;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import net.minecraft.util.text.TextFormatting;

public enum KitTier {
    @SerializedName("basic")
    BASIC("Basic", TextFormatting.AQUA, 0, Rarity.COMMON),
    @SerializedName("advanced")
    ADVANCED("Advanced", TextFormatting.BLUE, 1, Rarity.UNCOMMON),
    @SerializedName("complex")
    COMPLEX("Complex", TextFormatting.LIGHT_PURPLE, 2, Rarity.RARE),
    @SerializedName("professional")
    PROFESSIONAL("Professional", TextFormatting.DARK_PURPLE, 3, Rarity.EPIC),
    @SerializedName("forbidden")
    FORBIDDEN("Forbidden", TextFormatting.DARK_RED, 4, Rarity.LEGENDARY),
    @SerializedName("special")
    SPECIAL("Special", TextFormatting.WHITE, 5, Rarity.MYTHIC);

    public final String display;
    public final TextFormatting color;
    public final int sortOrder;
    /** Rarity the kit counts as for rarity rules */
    public final Rarity rarity;

    KitTier(String display, TextFormatting color, int sortOrder, Rarity rarity) {
        this.display = display;
        this.color = color;
        this.sortOrder = sortOrder;
        this.rarity = rarity;
    }
}
