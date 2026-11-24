package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameValue;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.ReopenMenuPacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Chat prompts for editing numeric game settings. While a player has a prompt open, their next chat messages are
 * treated as input and never shown to anyone else.
 */
public final class ValuePrompt {
    private static final int MAX_AMOUNT = 9999;
    private static final Map<UUID, Prompt> prompts = new HashMap<>();

    private ValuePrompt() {}

    private static class Prompt {
        final GameValue value;
        final GameMode mode;

        Prompt(GameValue value, GameMode mode) {
            this.value = value;
            this.mode = mode;
        }
    }

    public static void start(EntityPlayerMP player, GameValue value) {
        GameMode mode = GameManager.instance().getGameMode();
        prompts.put(player.getUniqueID(), new Prompt(value, mode));

        send(player, String.format(
            "&e&lEnter the new %s for %s &7(%s) &e- or send &f&lcancel &e&lto abort",
            value.display.toLowerCase(), mode.display, value.unit
        ));
    }

    public static void clear(EntityPlayerMP player) {
        prompts.remove(player.getUniqueID());
    }

    /**
     * Handles a chat message of a player; returns true if it was prompt input and must not be broadcast
     */
    public static boolean handleChat(EntityPlayerMP player, String message) {
        Prompt prompt = prompts.get(player.getUniqueID());
        if (prompt == null) return false;

        String input = message.trim();

        if (input.equalsIgnoreCase("cancel")) {
            prompts.remove(player.getUniqueID());
            send(player, "&7Cancelled");
            reopenMenu(player);
            return true;
        }

        int amount = parse(input);
        if (amount < 1) {
            send(player, String.format(
                "&cInvalid value! Enter a whole number from 1 to %d (%s), or send &f&lcancel", MAX_AMOUNT, prompt.value.unit
            ));
            return true;
        }

        prompts.remove(player.getUniqueID());
        GameManager.instance().setGameValue(prompt.mode, prompt.value, amount);
        reopenMenu(player);
        return true;
    }

    private static int parse(String input) {
        try {
            int amount = Integer.parseInt(input);
            return amount <= MAX_AMOUNT ? amount : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void send(EntityPlayerMP player, String message) {
        player.sendMessage(new TextComponentString(TextUtils.translateColorCodes(message)));
    }

    private static void reopenMenu(EntityPlayerMP player) {
        PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
    }
}
