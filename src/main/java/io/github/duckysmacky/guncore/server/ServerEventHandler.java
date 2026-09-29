package io.github.duckysmacky.guncore.server;

import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.server.game.GameManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
        if (event.getEntity() instanceof ServerPlayer victim) {
            // getEntity() is the one responsible for the damage (e.g. the shooter of an arrow), unlike getDirectEntity() (the arrow itself)
            registerPlayerDeath(victim, event.getSource().getEntity());
        }
    }

    @SubscribeEvent
    public void onGunKill(EntityKillByGunEvent event) {
        if (event.getLogicalSide() != LogicalSide.SERVER) return;

        if (event.getKilledEntity() instanceof ServerPlayer victim) {
            registerPlayerDeath(victim, event.getAttacker());
        }
    }

    private static void registerPlayerDeath(ServerPlayer victim, Entity killerEntity) {
        if (killerEntity instanceof ServerPlayer killer && killer != victim) {
            GameManager.instance().onPlayerKill(killer, victim);
        } else {
            GameManager.instance().onEnvironmentalDeath(victim);
        }
    }
}
