package io.github.duckysmacky.guncore.common.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class ServerSoundPlayer {
    private static final String ID = "ServerSoundPlayer";

    private ServerSoundPlayer() {}

    public static void playFor(Player player, SoundEvent sound, float volume, float pitch) {
        play(player, sound, volume, pitch);
    }

    public static void playForAll(SoundEvent sound, float volume, float pitch) {
        play(null, sound, volume, pitch);
    }

    public static void playAsServer(MinecraftServer server, Player target, SoundEvent sound, float volume, float pitch) {
        if (server == null) {
            GuncoreMod.LOGGER.error("[{}] Cannot play sound: server is null", ID);
            return;
        }

        server.getPlayerList().getPlayers().forEach(player -> {
            if (target == null || target.getUUID().equals(player.getUUID())) {
                player.connection.send(new ClientboundSoundPacket(
                    Holder.direct(sound),
                    SoundSource.MASTER,
                    player.getX(), player.getY(), player.getZ(),
                    volume, pitch, server.getNextTickTime()
                ));
            }
        });
    }

    private static void play(Player target, SoundEvent sound, float volume, float pitch) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            playAsServer(server, target, sound, volume, pitch);
        } else {
            // TODO: add client support
            GuncoreMod.LOGGER.error("[{}] Cannot play sound globally from client", ID);
        }
    }
}
