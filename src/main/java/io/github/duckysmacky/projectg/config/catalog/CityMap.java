package io.github.duckysmacky.projectg.config.catalog;

import com.google.gson.annotations.SerializedName;

public enum CityMap {
    @SerializedName("newport")
    NEWPORT("Newport City"),
    @SerializedName("radiant")
    RADIANT("Radiant City"),
    @SerializedName("shmar")
    SHMAR("Shmar City"),
    @SerializedName("audia")
    AUDIA("Audia City"),
    @SerializedName("city17")
    CITY17("City 17");

    public final String display;

    CityMap(String display) {
        this.display = display;
    }
}
