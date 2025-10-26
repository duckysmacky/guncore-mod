package io.github.duckysmacky.projectg.commands;

import io.github.duckysmacky.projectg.network.packets.OpenMenuPacket;
import io.github.duckysmacky.projectg.network.PacketHandler;
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
        return "/menu - Opens the Project G menu";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            PacketHandler.instance.sendTo(new OpenMenuPacket(), player);
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}