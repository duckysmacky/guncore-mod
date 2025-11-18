package io.github.duckysmacky.guncore.server;

import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.server.game.GameManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GuncoreMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {
    private static int tickCount = 0;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        ConfigManager.instance().load();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCount++;

            GameManager.instance().tick(tickCount);

            if (tickCount >= 20) tickCount = 0;
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        Entity victimEntity = event.getEntity();
        Entity killerEntity = event.getSource().getDirectEntity();

        if (victimEntity instanceof ServerPlayer victim) {
            ServerPlayer killer = (killerEntity instanceof ServerPlayer) ? (ServerPlayer) killerEntity : null;

            GameManager.instance().onPlayerDeath(victim, killer);
        }
    }

    @SubscribeEvent
    public void onGunKill(EntityKillByGunEvent event) {
        if (event.getLogicalSide() != LogicalSide.SERVER) return;

        LivingEntity victimEntity = event.getKilledEntity();
        LivingEntity killerEntity = event.getAttacker();

        if (victimEntity instanceof ServerPlayer victim) {
            ServerPlayer killer = (killerEntity instanceof ServerPlayer) ? (ServerPlayer) killerEntity : null;

            GameManager.instance().onPlayerDeath(victim, killer);
        }
    }
}
