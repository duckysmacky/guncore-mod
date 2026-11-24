package io.github.duckysmacky.guncore.server.commands;

import java.util.ArrayList;
import java.util.List;

/** Helpers for command arguments, which Minecraft splits on every space */
public final class CommandArgs {
    private CommandArgs() {}

    /**
     * Regroups arguments so a double-quoted value with spaces becomes a single token: {@code a "b c" d} gives
     * {@code [a, b c, d]}. A quote only opens a token when it is the token's first character. An unterminated
     * quote takes the rest of the input, so a half-typed name still works for tab completion.
     */
    // shortcut: no escape for an inner quote, add one if a name ever needs it
    public static List<String> tokenize(String[] args) {
        List<String> tokens = new ArrayList<>();
        String input = String.join(" ", args);
        int i = 0;

        while (i < input.length()) {
            if (input.charAt(i) == ' ') {
                i++;
            } else if (input.charAt(i) == '"') {
                int end = input.indexOf('"', i + 1);
                if (end == -1) end = input.length();
                tokens.add(input.substring(i + 1, end));
                i = end + 1;
            } else {
                int end = input.indexOf(' ', i);
                if (end == -1) end = input.length();
                tokens.add(input.substring(i, end));
                i = end;
            }
        }

        return tokens;
    }
}
