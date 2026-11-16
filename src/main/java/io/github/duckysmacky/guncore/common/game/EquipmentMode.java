package io.github.duckysmacky.guncore.common.game;

import com.google.gson.annotations.SerializedName;

public enum EquipmentMode {
    @SerializedName("kits") KITS("kits", "Kit-based"),
    @SerializedName("builder") BUILDER("builder", "Build-a-class");

    public final String id;
    public final String display;

    EquipmentMode(String id, String display) {
        this.id = id;
        this.display = display;
    }
}
