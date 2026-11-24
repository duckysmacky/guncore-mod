package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.ReopenMenuPacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntConsumer;

/**
 * A question asked to a player in chat. While a player has a prompt open, their next chat messages are treated as
 * answers and never shown to anyone else; {@code cancel} aborts it.
 * <p>
 * To add a prompt: register a factory under an id on the server ({@link #register}), then on the client use
 * {@code PromptEntry} with the same id. Prompts can also be started straight from server code with {@link #start}.
 */
public final class ChatPrompt {
    private static final Map<String, Function<EntityPlayerMP, ChatPrompt>> registered = new HashMap<>();
    private static final Map<UUID, ChatPrompt> open = new HashMap<>();

    private final String question;
    private final Function<String, String> onAnswer;
    private boolean reopenMenu = true;

    /**
     * @param question what the player is asked
     * @param onAnswer gets the trimmed answer; returns an error message to ask again, or null if the answer was accepted
     */
    public ChatPrompt(String question, Function<String, String> onAnswer) {
        this.question = question;
        this.onAnswer = onAnswer;
    }

    /**
     * A prompt for a whole number from min to max
     */
    public static ChatPrompt integer(String question, int min, int max, IntConsumer onValid) {
        return new ChatPrompt(question, answer -> {
            try {
                int amount = Integer.parseInt(answer);
                if (amount >= min && amount <= max) {
                    onValid.accept(amount);
                    return null;
                }
            } catch (NumberFormatException ignored) {}

            return String.format("Enter a whole number from %d to %d", min, max);
        });
    }

    /**
     * Don't bring the player's menu back once the prompt is done
     */
    public ChatPrompt withoutMenuReopen() {
        this.reopenMenu = false;
        return this;
    }

    /**
     * Makes the prompt startable from clients by id. The factory runs when the prompt starts, so it can read
     * the state at that moment.
     */
    public static void register(String id, Function<EntityPlayerMP, ChatPrompt> factory) {
        registered.put(id, factory);
    }

    public static void start(EntityPlayerMP player, String id) {
        Function<EntityPlayerMP, ChatPrompt> factory = registered.get(id);
        if (factory == null) {
            GuncoreMod.LOGGER.warn("[ChatPrompt] Unknown prompt '{}' requested by {}", id, player.getName());
            return;
        }

        start(player, factory.apply(player));
    }

    public static void start(EntityPlayerMP player, ChatPrompt prompt) {
        open.put(player.getUniqueID(), prompt);
        send(player, "&e&l" + prompt.question + " &e- or send &f&lcancel &e&lto abort");
    }

    public static void clear(EntityPlayerMP player) {
        open.remove(player.getUniqueID());
    }

    /**
     * Handles a chat message of a player; returns true if it was a prompt answer and must not be broadcast
     */
    public static boolean handleChat(EntityPlayerMP player, String message) {
        ChatPrompt prompt = open.get(player.getUniqueID());
        if (prompt == null) return false;

        String answer = message.trim();

        if (answer.equalsIgnoreCase("cancel")) {
            send(player, "&7Cancelled");
        } else {
            String error = prompt.onAnswer.apply(answer);
            if (error != null) {
                send(player, "&c" + error + ", or send &f&lcancel");
                return true;
            }
        }

        open.remove(player.getUniqueID());
        if (prompt.reopenMenu) PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
        return true;
    }

    private static void send(EntityPlayerMP player, String message) {
        player.sendMessage(new TextComponentString(TextUtils.translateColorCodes(message)));
    }
}
