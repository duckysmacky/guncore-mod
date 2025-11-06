package io.github.duckysmacky.guncore.server.commands;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.network.packets.OpenMainMenuPacket;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class MenuCommand extends CommandBase {
    @Override
    public String getName() {
        return "menu";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/menu - Opens the menu";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            ConfigManager.instance().loadConfig();
            PacketHandler.instance().sendTo(new OpenMainMenuPacket(), player);
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}