package io.github.duckysmacky.guncore.server;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.server.game.GameManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerEventHandler {
    private int tickCount = 0;

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ConfigManager.instance().load();
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
        Entity killerEntity = event.getSource().getDirectEntity();

        if (victimEntity instanceof ServerPlayer victim && killerEntity instanceof ServerPlayer killer) {
            GameManager.instance().onPlayerKill(killer, victim);
        }
    }
}
