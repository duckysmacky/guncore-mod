package io.github.duckysmacky.projectg.commands;

import io.github.duckysmacky.projectg.data.ConfigLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class ConfigReloadCommand extends CommandBase {
    @Override
    public String getName() {
        return "config_reload";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/config_reload - Reloads the mod configuration files";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        ConfigLoader configLoader = ConfigLoader.instance();
        configLoader.loadConfig();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
