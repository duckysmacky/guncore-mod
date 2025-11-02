package io.github.duckysmacky.guncore.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.network.PacketHandler;
import io.github.duckysmacky.guncore.network.packets.ExecuteCommandPacket;
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
            GuncoreMod.LOGGER.info("[{}] Sending command to server: {}", ID, command);
            PacketHandler.instance().sendToServer(new ExecuteCommandPacket(command));
        }
    }

    public static void executeAsServer(MinecraftServer server, String command) {
        if (server == null) {
            GuncoreMod.LOGGER.warn("[{}] Cannot execute command: server is null!", ID);
            return;
        }

        GuncoreMod.LOGGER.info("[{}] Executing command: {}", ID, command);
        server.commandManager.executeCommand(server, command);
    }
}