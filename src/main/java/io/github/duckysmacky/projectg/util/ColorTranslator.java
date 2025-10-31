package io.github.duckysmacky.projectg.util;

public final class ColorTranslator {
    public static final String COLOR_SYMBOL = "§";
    public static final String AMPERSAND = "&";

    private ColorTranslator() {}

    public static String translateColorCodes(String text) {
        return text.replace(AMPERSAND, COLOR_SYMBOL);
    }
}
