package io.github.duckysmacky.guncore.server.commands;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.RefreshMenuPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class GuncoreConfigCommand extends CommandBase {
    @Override
    public String getName() {
        return "guncore_config";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/guncore_config <reload>";
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
            case "reload":
                ConfigManager.instance().load();
                if (sender instanceof EntityPlayerMP) {
                    PacketHandler.instance().sendTo(new RefreshMenuPacket(), (EntityPlayerMP) sender);
                }
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
