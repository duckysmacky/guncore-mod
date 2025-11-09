package io.github.duckysmacky.guncore.common.config.catalog.guns;

import com.google.gson.annotations.SerializedName;

public enum GunCategory {
    @SerializedName("assault_rifle")
    ASSAULT_RIFLE("Assault Rifle"),
    @SerializedName("battle_rifle")
    BATTLE_RIFLE("Battle Rifle"),
    @SerializedName("dmr")
    DMR("DMR"),
    @SerializedName("lmg")
    LMG("LMG"),
    @SerializedName("smg")
    SMG("SMG"),
    @SerializedName("shotgun")
    SHOTGUN("Shotgun"),
    @SerializedName("sniper_rifle")
    SNIPER_RIFLE("Sniper Rifle"),
    @SerializedName("sidearm")
    SIDEARM("Sidearm"),
    @SerializedName("melee")
    MELEE("Melee"),
    @SerializedName("special")
    SPECIAL("Special Weapon");

    public final String display;

    GunCategory(String display) {
        this.display = display;
    }
}
