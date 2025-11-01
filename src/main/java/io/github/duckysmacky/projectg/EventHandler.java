package io.github.duckysmacky.projectg;

import io.github.duckysmacky.projectg.game.GameManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class EventHandler {
    private int tickCounter = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;

            boolean isSecondTick = false;
            if (tickCounter >= 20) {
                tickCounter = 0;
                isSecondTick = true;
            }

            GameManager.instance().tick(isSecondTick);
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        Entity victimEntity = event.getEntity();
        Entity killerEntity = event.getSource().getTrueSource();

        if (victimEntity instanceof EntityPlayer && killerEntity instanceof EntityPlayer) {
            EntityPlayer victim = (EntityPlayer) victimEntity;
            EntityPlayer killer = (EntityPlayer) killerEntity;

            GameManager.instance().onPlayerKill(killer, victim);
        }
    }
}
