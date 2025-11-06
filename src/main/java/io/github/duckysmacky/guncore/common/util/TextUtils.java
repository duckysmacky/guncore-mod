package io.github.duckysmacky.guncore.common.util;

public final class TextUtils {
    public static final String COLOR_SYMBOL = "§";
    public static final String AMPERSAND = "&";

    private TextUtils() {}

    public static String translateColorCodes(String text) {
        return text.replace(AMPERSAND, COLOR_SYMBOL);
    }

    public static String formatTime(int secs) {
        if (secs < 0) secs = 0;

        int minutes = secs / 60;
        int seconds = secs % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}
