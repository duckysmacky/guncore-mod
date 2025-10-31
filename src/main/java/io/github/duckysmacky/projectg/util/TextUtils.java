package io.github.duckysmacky.projectg.util;

public final class TextUtils {
    public static final String COLOR_SYMBOL = "§";
    public static final String AMPERSAND = "&";

    private TextUtils() {}

    public static String translateColorCodes(String text) {
        return text.replace(AMPERSAND, COLOR_SYMBOL);
    }
}
