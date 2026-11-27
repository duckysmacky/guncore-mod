package io.github.duckysmacky.guncore.common.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class ServerSoundPlayer {
    private static final String ID = "ServerSoundPlayer";

    private ServerSoundPlayer() {}

    /** Plays the sound only for the given player */
    public static void playFor(EntityPlayer player, SoundEvent sound, float volume, float pitch) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketSoundEffect(
                sound, SoundCategory.MASTER, player.posX, player.posY, player.posZ, volume, pitch
            ));
        } else if (player.world.isRemote) {
            player.playSound(sound, volume, pitch);
        }
    }

    public static void playForAll(SoundEvent sound, float volume, float pitch) {
        play(null, sound, volume, pitch);
    }

    public static void playAsServer(MinecraftServer server, EntityPlayer target, SoundEvent sound, float volume, float pitch) {
        if (server == null) {
            GuncoreMod.LOGGER.error(String.format("[%s] Cannot play sound: server is null", ID));
            return;
        }

        if (target == null) {
            server.getPlayerList().getPlayers().forEach(p -> {
                WorldServer world = p.getServerWorld();
                world.playSound(null, p.getPosition(), sound, SoundCategory.MASTER, volume, pitch);
            });
        } else {
            WorldServer world = server.getWorld(0);
            world.playSound(target, target.getPosition(), sound, SoundCategory.MASTER, volume, pitch);
        }
    }

    private static void play(EntityPlayer target, SoundEvent sound, float volume, float pitch) {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            playAsServer(server, target, sound, volume, pitch);
        } else {
            // TODO: add client support
            GuncoreMod.LOGGER.error(String.format("[%s] Cannot play sound globally from client", ID));
        }
    }
}
