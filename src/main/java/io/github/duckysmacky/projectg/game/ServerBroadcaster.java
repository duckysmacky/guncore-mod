package io.github.duckysmacky.projectg.game;

import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.network.PacketHandler;
import io.github.duckysmacky.projectg.network.packets.BroadcastMessagePacket;
import io.github.duckysmacky.projectg.network.packets.ExecuteCommandPacket;
import io.github.duckysmacky.projectg.util.TextUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundEvent;
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

    public static void playSound(SoundEvent sound) {
        // TODO: figure this shit out
        // forEachPlayer(p -> p.playSound(sound, 1f, 1f));
    }

    public static void broadcastAsServer(MinecraftServer server, String message) {
        if (server == null) {
            ProjectGMod.LOGGER.error("[{}] Cannot execute command: server is null!", ID);
            return;
        }

        ProjectGMod.LOGGER.info("[{}] {}", ID, message);
        server.getPlayerList().getPlayers()
            .forEach(p -> p.sendMessage(new TextComponentString(message)));
        }

    private static void broadcast(String message) {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            broadcastAsServer(server, message);
        } else {
            ProjectGMod.LOGGER.info("[{}] Sending message to server: {}", ID, message);
            PacketHandler.instance().sendToServer(new BroadcastMessagePacket(message));
        }
    }
}
