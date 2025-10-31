package io.github.duckysmacky.projectg.commands;

import io.github.duckysmacky.projectg.game.GameManager;
import io.github.duckysmacky.projectg.game.GameMode;
import io.github.duckysmacky.projectg.game.PlayerStats;
import io.github.duckysmacky.projectg.util.TextUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.function.Consumer;

public class GameCommand extends CommandBase {
    private final static String[] subcommands = {
        "start",
        "end",
        "reset",
        "pause",
        "kills",
        "lives",
        "deaths",
        "mode",
        "mode_variant",
        "scoreboard",
        "teams"
    };

    @Override
    public String getName() {
        return "game";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/game <" + String.join("|", subcommands) + ">";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // only ops
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        GameManager gameManager = GameManager.instance();

        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "start":
                gameManager.startRound();
                break;
            case "end":
                gameManager.endRound();
                break;
            case "reset":
                gameManager.resetRound();
                break;
            case "pause":
                gameManager.toggleRoundPause();
                break;
            // player stats
            case "kills": case "lives": case "deaths":
                handleStatCommand(server, sender, subcommand, args);
                break;
            case "mode":
                if (args.length < 2)
                    throw new CommandException("Usage: /game mode <ffa|tdm|hostage>");
                GameMode mode = parseMode(args[1]);
                gameManager.setGameMode(mode);
                break;
            case "mode_variant":
                if (args.length < 2)
                    throw new CommandException("Usage: /game mode_variant <time|lives>");
                GameMode.Variant variant = parseVariant(args[1]);
                gameManager.setGameModeVariant(variant);
                break;
            case "scoreboard":
                gameManager.printGameScoreboard();
                break;
            case "teams":
                gameManager.printTeams();
                break;
            default:
                throw new CommandException(getUsage(sender));
        }
    }

    private void handleStatCommand(MinecraftServer server, ICommandSender sender, String type, String[] args) throws CommandException {
        if (args.length < 4)
            throw new CommandException("Usage: /game " + type + " <add|remove|set> <player> <amount>");

        String action = args[1].toLowerCase();
        String playerName = args[2];

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            throw new CommandException("Amount must be an integer!");
        }

        EntityPlayerMP player = getPlayerByName(server, playerName);
        PlayerStats stats = GameManager.instance().getStats(player);

        if (stats == null)
            throw new CommandException("Player '" + playerName + "' has no active game stats!");

        switch (type) {
            case "kills":
                modifyStatValue(stats::addKills, stats::removeKills, stats::setKills, action, amount);
                break;
            case "lives":
                modifyStatValue(stats::addLives, stats::removeLives, stats::setLives, action, amount);
                break;
            case "deaths":
                modifyStatValue(stats::addDeaths, stats::removeDeaths, stats::setDeaths, action, amount);
                break;
        }

        String message = TextUtils.translateColorCodes(String.format(
            "&a%s %s -> %s = %d",
            playerName, action, type, getStatValue(stats, type)
        ));
        sender.sendMessage(new TextComponentString(message));
    }

    private void modifyStatValue(Consumer<Integer> adder, Consumer<Integer> remover, Consumer<Integer> setter, String statAction, int amount) throws CommandException {
        switch (statAction) {
            case "add":
                adder.accept(amount);
                break;
            case "remove":
                remover.accept(amount);
                break;
            case "set":
                setter.accept(amount);
                break;
            default:
                throw new CommandException("Invalid action! Use the add/remove/set action.");
        }
    }

    private int getStatValue(PlayerStats stats, String statType) {
        switch (statType) {
            case "kills": return stats.getKills();
            case "lives": return stats.getLives();
            case "deaths": return stats.getDeaths();
        }
        return 0;
    }

    private GameMode parseMode(String s) throws CommandException {
        try {
            return GameMode.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandException("Invalid mode! Valid: ffa, tdm, hostage");
        }
    }

    private GameMode.Variant parseVariant(String s) throws CommandException {
        try {
            return GameMode.Variant.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandException("Invalid variant! Valid: time, lives");
        }
    }

    private EntityPlayerMP getPlayerByName(MinecraftServer server, String name) throws CommandException {
        EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(name);
        if (player == null)
            throw new CommandException("Player '" + name + "' not found or not online!");
        return player;
    }
}
