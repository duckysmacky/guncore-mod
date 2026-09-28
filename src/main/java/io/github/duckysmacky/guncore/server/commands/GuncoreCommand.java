package io.github.duckysmacky.guncore.server.commands;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.RefreshMenuPacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;

public class GuncoreCommand extends CommandBase {
    private final static String[] subcommands = {
        "config_reload",
        "help"
    };

    private final static String[] helpLines = {
        "&6&l--- Guncore Commands ---",
        "&f/menu &7- Opens the equipment/game menu",
        "&f/guncore config_reload &7- Reloads the mod config from disk",
        "&f/guncore help &7- Shows this list",
        "&f/game start|end|reset|pause &7- Controls the current round",
        "&f/game mode <ffa|tdm|hostage> &7- Sets the game mode",
        "&f/game mode_variant <time|lives|kills> &7- Sets the win condition",
        "&f/game kills|lives|deaths <add|remove|set> <player> <amount> &7- Edits player stats",
        "&f/game register_kill <victim> [killer] &7- Registers a kill and a death",
        "&f/game kill_only_lives <true|false> &7- Toggles life loss on non-player-caused deaths",
        "&f/game scoreboard &7- Prints the scoreboard",
        "&f/game teams &7- Prints team rosters"
    };

    @Override
    public String getName() {
        return "guncore";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/guncore <" + String.join("|", subcommands) + ">";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "config_reload":
                ConfigManager.instance().load();
                if (sender instanceof EntityPlayerMP) {
                    PacketHandler.instance().sendTo(new RefreshMenuPacket(), (EntityPlayerMP) sender);
                }
                sender.sendMessage(new TextComponentString(TextUtils.translateColorCodes("&aGuncore config reloaded")));
                break;
            case "help":
                Arrays.stream(helpLines).forEach(line ->
                    sender.sendMessage(new TextComponentString(TextUtils.translateColorCodes(line)))
                );
                break;
            default:
                throw new CommandException(getUsage(sender));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
