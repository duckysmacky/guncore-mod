package io.github.duckysmacky.guncore.common.game;

public enum GameState {
    NOT_STARTED("Not started"),
    RUNNING("In progress"),
    PAUSED("Paused"),
    ENDED("Ended");

    public final String display;

    GameState(String display) {
        this.display = display;
    }
}
