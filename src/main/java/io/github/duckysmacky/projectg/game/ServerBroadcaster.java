package io.github.duckysmacky.projectg.game;

import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.util.TextUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.function.Consumer;

public class ServerBroadcaster {
    private static final String ID = "ServerBroadcaster";
    private final MinecraftServer server;

    public ServerBroadcaster() {
        this.server = FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public void broadcast(String message) {
        ProjectGMod.LOGGER.info("[{}] {}", ID, message);
        String translated = TextUtils.translateColorCodes(message);
        forEachPlayer(p -> p.sendMessage(new TextComponentString(translated)));
    }

    public void playSound(SoundEvent sound) {
        forEachPlayer(p -> p.playSound(sound, 1f, 1f));
    }

    public void forEachPlayer(Consumer<EntityPlayer> callback) {
        server.getPlayerList().getPlayers().forEach(callback);
    }
}
