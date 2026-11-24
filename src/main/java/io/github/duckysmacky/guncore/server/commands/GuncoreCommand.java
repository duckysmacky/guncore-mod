package io.github.duckysmacky.guncore.server.commands;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.locations.LocationEntry;
import io.github.duckysmacky.guncore.common.config.catalog.locations.LocationEntry.LocationCoordinates;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionType;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.RefreshMenuPacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import io.github.duckysmacky.guncore.server.game.GameManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GuncoreCommand extends CommandBase {
    private final static String[] subcommands = {
        "config_reload",
        "location",
        "help"
    };

    private final static String[] helpLines = {
        "&6&l--- Guncore Commands ---",
        "&f/menu &7- Opens the equipment/game menu",
        "&f/guncore config_reload &7- (ops) Reloads the mod config from disk and refreshes every player's menu",
        "&f/guncore location add <map> <name> [<x> <y> <z>] &7- (ops) Adds a catalog location, at your position if no coordinates are given. Wrap a name with spaces in \"quotes\"",
        "&f/guncore help &7- Shows this list",
        "&f/game start|end|reset|pause &7- Controls the current round",
        "&f/game mode <ffa|tdm|hostage> &7- Sets the game mode",
        "&f/game mode_variant <time|lives|kills> &7- Sets the win condition",
        "&f/game equipment <kits|builder> &7- Sets the equipment selection system",
        "&f/game kills|lives|deaths <add|remove|set> <player> <amount> &7- Edits player stats",
        "&f/game register_kill <victim> [killer] &7- Registers a kill and a death",
        "&f/game world <gamerule> <true|false> &7- Sets a world gamerule (saved to settings.json)",
        "&f/game world setup &7- Resets world settings to the defaults and applies them",
        "&f/game kill_only_lives <true|false> &7- Toggles life loss on non-player-caused deaths",
        "&f/game item_pickup <true|false> &7- Toggles whether players can pick up items",
        "&f/game spawnpoint [player] &7- Sets a spawnpoint, survival mode, and bedrock at the target's location",
        "&f/game spawnpoint_all &7- Same, but for every online player",
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
                if (!isAdmin(sender)) throw new CommandException("commands.generic.permission");
                ConfigManager.instance().load();
                GameManager.applyWorldSettings(server);
                PacketHandler.instance().sendToAll(new RefreshMenuPacket());
                sender.sendMessage(new TextComponentString(TextUtils.translateColorCodes("&aGuncore config reloaded")));
                break;
            case "location":
                if (!isAdmin(sender)) throw new CommandException("commands.generic.permission");
                addLocation(sender, CommandArgs.tokenize(args));
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
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(Arrays.asList(subcommands));
            if (!isAdmin(sender)) {
                options.remove("config_reload");
                options.remove("location");
            }
            return getListOfStringsMatchingLastWord(args, options);
        }

        if (!args[0].equalsIgnoreCase("location") || !isAdmin(sender)) return Collections.emptyList();
        if (args.length == 2) return getListOfStringsMatchingLastWord(args, "add");
        if (args.length == 3 && args[1].equalsIgnoreCase("add")) {
            return getListOfStringsMatchingLastWord(args, ConfigManager.instance().getMenuSectionManager()
                .getSections(MenuSectionType.MAPS).stream()
                .map(MenuSectionEntry::getId)
                .collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }

    /** {@code tokens}: location add <map> <name> [<x> <y> <z>] */
    private void addLocation(ICommandSender sender, List<String> tokens) throws CommandException {
        String usage = "/guncore location add <map> <name> [<x> <y> <z>]";
        boolean hasCoordinates = tokens.size() == 7;
        if ((tokens.size() != 4 && !hasCoordinates) || !tokens.get(1).equalsIgnoreCase("add")) {
            throw new CommandException(usage);
        }

        String map = tokens.get(2);
        String name = tokens.get(3);

        if (!ConfigManager.instance().getMenuSectionManager().findSection(MenuSectionType.MAPS, map).isPresent()) {
            throw new CommandException("Unknown map '" + map + "'");
        }
        if (ConfigManager.instance().getCatalogManager().findEntry(CatalogType.LOCATIONS, name).isPresent()) {
            throw new CommandException("A location named '" + name + "' already exists");
        }

        BlockPos pos;
        if (hasCoordinates) {
            pos = parseBlockPos(sender, tokens.subList(4, 7).toArray(new String[0]), 0, false);
        } else if (sender instanceof EntityPlayerMP) {
            pos = sender.getPosition();
        } else {
            throw new CommandException("Coordinates are required when not run by a player: " + usage);
        }

        LocationEntry entry = new LocationEntry(
            true, map, name, new LocationCoordinates(pos.getX(), pos.getY(), pos.getZ()), Collections.emptyList()
        );
        ConfigManager.instance().getCatalogManager().appendEntry(CatalogType.LOCATIONS, entry);
        PacketHandler.instance().sendToAll(new RefreshMenuPacket());

        sender.sendMessage(new TextComponentString(TextUtils.translateColorCodes(String.format(
            "&aAdded location '%s' on map '%s' at (%d, %d, %d)", name, map, pos.getX(), pos.getY(), pos.getZ()
        ))));
    }

    private boolean isAdmin(ICommandSender sender) {
        return sender.canUseCommand(2, getName());
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
