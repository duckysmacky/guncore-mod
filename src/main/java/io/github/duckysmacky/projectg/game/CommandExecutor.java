package io.github.duckysmacky.projectg.game;

import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.network.PacketHandler;
import io.github.duckysmacky.projectg.network.packets.ExecuteCommandPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class CommandExecutor {
    private static final String ID = "CommandExecutor";

    private CommandExecutor() {}

    public static void execute(String command) {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            executeAsServer(server, command);
        } else {
            // send packet to server
            ProjectGMod.LOGGER.info("[{}] Sending command to server: {}", ID, command);
            PacketHandler.instance().sendToServer(new ExecuteCommandPacket(command));
        }
    }

    public static void executeAsServer(MinecraftServer server, String command) {
        if (server == null) {
            ProjectGMod.LOGGER.warn("[{}] Cannot execute command: server is null!", ID);
            return;
        }

        ProjectGMod.LOGGER.info("[{}] Executing command: {}", ID, command);
        server.commandManager.executeCommand(server, command);
    }
}