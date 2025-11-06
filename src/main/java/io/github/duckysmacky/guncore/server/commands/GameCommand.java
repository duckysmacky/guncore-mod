package io.github.duckysmacky.guncore.server.commands;

import io.github.duckysmacky.guncore.server.game.GameManager;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
            case "register_death":
                handleRegisterDeath(server, sender, args);
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
                modifyStatValue(stats::getKills, stats::setKills, action, amount);
                break;
            case "lives":
                modifyStatValue(stats::getLives, stats::setLives, action, amount);
                break;
            case "deaths":
                modifyStatValue(stats::getDeaths, stats::setDeaths, action, amount);
                break;
        }

        String message = TextUtils.translateColorCodes(String.format(
            "&fUpdated %s's stats: &7%s -> %s = &a%d",
            playerName, action, type, getStatValue(stats, type)
        ));
        sender.sendMessage(new TextComponentString(message));
    }

    private void handleRegisterDeath(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2)
            throw new CommandException("Usage: /game register_death <player>");

        String playerName = args[1];
        EntityPlayerMP target = getPlayerByName(server, playerName);

        if (sender instanceof EntityPlayerMP) {
            GameManager.instance().onPlayerKill((EntityPlayerMP) sender, target);
        }
    }

    private void modifyStatValue(Supplier<Integer> getter, Consumer<Integer> setter, String statAction, int amount) throws CommandException {
        switch (statAction) {
            case "add":
                setter.accept(getter.get() + amount);
                break;
            case "remove":
                setter.accept(getter.get() - amount);
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
