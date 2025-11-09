package io.github.duckysmacky.guncore.common.game;

import net.minecraft.util.text.TextFormatting;

public enum Team {
    NONE("No", TextFormatting.WHITE),
    BLUE("Blue", TextFormatting.BLUE),
    RED("Red", TextFormatting.RED),
    YELLOW("Yellow", TextFormatting.YELLOW),
    GREEN("Green", TextFormatting.GREEN),
    PURPLE("Purple", TextFormatting.DARK_PURPLE);

    public final String display;
    public final TextFormatting color;

    Team(String display, TextFormatting color) {
        this.display = display;
        this.color = color;
    }
}
