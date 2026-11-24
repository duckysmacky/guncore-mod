package io.github.duckysmacky.guncore.common.game;

/**
 * A numeric per-gamemode setting which can be edited in-game
 */
public enum GameValue {
    ROUND_TIME("Round time", "minutes", 60), // stored in seconds
    LIVES("Lives", "lives", 1),
    KILL_TARGET("Kill target", "kills", 1);

    public final String display;
    public final String unit;
    /** How many stored units one entered unit is worth */
    public final int factor;

    /** Id of the chat prompt which edits this value, see GameManager.registerPrompts */
    public String promptId() {
        return "game_value." + name().toLowerCase();
    }

    GameValue(String display, String unit, int factor) {
        this.display = display;
        this.unit = unit;
        this.factor = factor;
    }
}
