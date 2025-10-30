package io.github.duckysmacky.projectg.game;

import net.minecraft.util.text.TextFormatting;

public enum Team {
    BLUE("Blue", TextFormatting.BLUE),
    RED("Red", TextFormatting.RED),
    YELLOW("Yellow", TextFormatting.YELLOW),
    GREEN("Green", TextFormatting.GREEN);

    public final String display;
    public final TextFormatting color;

    Team(String display, TextFormatting color) {
        this.display = display;
        this.color = color;
    }
}
