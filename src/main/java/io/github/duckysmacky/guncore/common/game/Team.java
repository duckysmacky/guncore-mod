package io.github.duckysmacky.guncore.common.game;

import net.minecraft.ChatFormatting;

public enum Team {
    NONE("No", ChatFormatting.WHITE),
    BLUE("Blue", ChatFormatting.BLUE),
    RED("Red", ChatFormatting.RED),
    YELLOW("Yellow", ChatFormatting.YELLOW),
    GREEN("Green", ChatFormatting.GREEN),
    PURPLE("Purple", ChatFormatting.DARK_PURPLE);

    public final String display;
    public final ChatFormatting color;

    Team(String display, ChatFormatting color) {
        this.display = display;
        this.color = color;
    }
}
