package io.github.duckysmacky.guncore.data.config.catalog.kits;

import com.google.gson.annotations.SerializedName;

public enum KitClass {
    @SerializedName("assault")
    ASSAULT("Assault"),
    @SerializedName("skirmisher")
    SKIRMISHER("Skirmisher"),
    @SerializedName("assassin")
    ASSASSIN("Assassin"),
    @SerializedName("sentinel")
    SENTINEL("Sentinel"),
    @SerializedName("special")
    SPECIAL("Special");

    public final String display;

    KitClass(String display) {
        this.display = display;
    }
}
