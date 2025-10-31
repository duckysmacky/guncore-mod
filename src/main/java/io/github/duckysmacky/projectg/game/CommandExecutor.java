package io.github.duckysmacky.projectg.game;

import io.github.duckysmacky.projectg.ProjectGMod;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandExecutor {
    private static final String ID = "CommandExecutor";
    private final MinecraftServer server;

    public CommandExecutor() {
        this.server = FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public void execute(String command) {
        ProjectGMod.LOGGER.info(String.format("[%s] %s", ID, command));
        server.commandManager.executeCommand(server, command);
    }
}
