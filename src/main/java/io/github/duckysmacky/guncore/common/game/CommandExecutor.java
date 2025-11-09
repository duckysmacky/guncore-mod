package io.github.duckysmacky.guncore.common.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.ExecuteCommandPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class CommandExecutor {
    private static final String ID = "CommandExecutor";

    private CommandExecutor() {}

    public static void execute(String command) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            executeAsServer(server, command);
        } else {
            GuncoreMod.LOGGER.info("[{}] Sending command to server: {}", ID, command);
            PacketHandler.CHANNEL.sendToServer(new ExecuteCommandPacket(command));
        }
    }

    public static void executeAsServer(MinecraftServer server, String command) {
        if (server == null) {
            GuncoreMod.LOGGER.warn("[{}] Cannot execute command: server is null!", ID);
            return;
        }

        GuncoreMod.LOGGER.info("[{}] Executing command: {}", ID, command);
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
    }
}