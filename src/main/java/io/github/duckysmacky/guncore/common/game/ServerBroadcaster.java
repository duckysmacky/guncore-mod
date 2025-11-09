package io.github.duckysmacky.guncore.common.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.BroadcastMessagePacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class ServerBroadcaster {
    private static final String ID = "ServerBroadcaster";

    private ServerBroadcaster() {}

    public static void message(String message) {
        String translated = TextUtils.translateColorCodes(message);
        broadcast(translated);
    }

    public static void warning(String message) {
        String translated = TextUtils.translateColorCodes("&e&lWARNING: " + message);
        broadcast(translated);
    }

    public static void error(String message) {
        String translated = TextUtils.translateColorCodes("&c&lERROR: " + message);
        broadcast(translated);
    }

    public static void broadcastAsServer(MinecraftServer server, String message) {
        if (server == null) {
            GuncoreMod.LOGGER.error("[{}] Cannot send message: server is null!", ID);
            return;
        }

        GuncoreMod.LOGGER.info("[{}] {}", ID, message);
        server.getPlayerList().getPlayers()
            .forEach(p -> p.sendSystemMessage(Component.literal(message)));
    }

    private static void broadcast(String message) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            broadcastAsServer(server, message);
        } else {
            PacketHandler.CHANNEL.sendToServer(new BroadcastMessagePacket(message));
        }
    }
}
