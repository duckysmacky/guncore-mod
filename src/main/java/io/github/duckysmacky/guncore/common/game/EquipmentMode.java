package io.github.duckysmacky.guncore.common.game;

public enum EquipmentMode {
    KIT_BASED("kit-based", "Kit-based"),
    BUILD_A_CLASS("build-a-class", "Build-a-class");

    public final String id;
    public final String display;

    EquipmentMode(String id, String display) {
        this.id = id;
        this.display = display;
    }
}
