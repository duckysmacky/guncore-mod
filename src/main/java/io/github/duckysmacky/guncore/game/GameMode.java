package io.github.duckysmacky.guncore.game;

public enum GameMode {
    FFA("Free for all"),
    TDM("Team deathmatch"),
    HOSTAGE("Hostage rescue");

    public final String display;

    GameMode(String display) {
        this.display = display;
    }

    public enum Variant {
        TIME("Time-based"),
        LIVES("Life-based");

        public final String display;

        Variant(String display) {
            this.display = display;
        }
    }
}
