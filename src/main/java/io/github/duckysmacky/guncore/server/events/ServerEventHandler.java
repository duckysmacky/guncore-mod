package io.github.duckysmacky.guncore.server.events;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.RefreshMenuPacket;
import io.github.duckysmacky.guncore.server.game.GameManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class ServerEventHandler {
    private int tickCount = 0;

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        ConfigManager.instance().load();
        PacketHandler.instance().sendTo(new RefreshMenuPacket(), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCount++;

            GameManager.instance().tick(tickCount);

            if (tickCount >= 20) tickCount = 0;
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        Entity victimEntity = event.getEntity();
        if (!(victimEntity instanceof EntityPlayer)) return;
        EntityPlayer victim = (EntityPlayer) victimEntity;

        Entity killerEntity = event.getSource().getTrueSource();

        if (killerEntity instanceof EntityPlayer) {
            GameManager.instance().onPlayerKill((EntityPlayer) killerEntity, victim);
        } else {
            GameManager.instance().onEnvironmentalDeath(victim);
        }
    }
}
