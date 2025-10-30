package io.github.duckysmacky.projectg.game;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandExecutor {
    private final MinecraftServer server;

    public CommandExecutor() {
        this.server = FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public void execute(String command) {
        server.commandManager.executeCommand(server, command);
    }
}
