package io.github.duckysmacky.guncore.common.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.BroadcastMessagePacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
            GuncoreMod.LOGGER.error(String.format("[%s] Cannot send message: server is null!", ID));
            return;
        }

        GuncoreMod.LOGGER.info(String.format("[%s] %s", ID, message));
        server.getPlayerList().getPlayers()
            .forEach(p -> p.sendMessage(new TextComponentString(message)));
    }

    private static void broadcast(String message) {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            broadcastAsServer(server, message);
        } else {
            PacketHandler.instance().sendToServer(new BroadcastMessagePacket(message));
        }
    }
}
