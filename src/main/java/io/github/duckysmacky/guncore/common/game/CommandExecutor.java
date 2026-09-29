package io.github.duckysmacky.guncore.common.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class CommandExecutor {
    private static final String ID = "CommandExecutor";

    private CommandExecutor() {}

    public static void execute(String command) {
        executeAsServer(ServerLifecycleHooks.getCurrentServer(), command);
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