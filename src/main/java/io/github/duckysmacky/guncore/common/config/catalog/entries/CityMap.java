package io.github.duckysmacky.guncore.common.config.catalog.entries;

import com.google.gson.annotations.SerializedName;

public enum CityMap {
    @SerializedName("other")
    OTHER("Other");

    public final String display;

    CityMap(String display) {
        this.display = display;
    }
}
