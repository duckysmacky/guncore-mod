package io.github.duckysmacky.projectg.commands;

import io.github.duckysmacky.projectg.data.config.ConfigLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class ProjectGConfigCommand extends CommandBase {
    @Override
    public String getName() {
        return "pg_config";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/pg_config <reload>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String subcommand = args[0].toLowerCase();

        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        switch (subcommand) {
            case "reload":
                ConfigLoader.instance().loadConfig();
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
