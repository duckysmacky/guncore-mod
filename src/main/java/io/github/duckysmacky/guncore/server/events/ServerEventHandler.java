package io.github.duckysmacky.guncore.server.events;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.RefreshMenuPacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.server.game.GameManager;
import io.github.duckysmacky.guncore.server.game.ChatPrompt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
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

        EntityPlayerMP player = (EntityPlayerMP) event.player;

        ConfigManager.instance().load();
        GameManager.instance().updatePlayerListAsServer(player.getServer());
        EquipmentController.syncSelection(player);
        PacketHandler.instance().sendTo(new RefreshMenuPacket(), player);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) ChatPrompt.clear((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        if (ChatPrompt.handleChat(event.getPlayer(), event.getMessage())) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCount++;

            GameManager.instance().tick(tickCount);

            if (tickCount >= 20) tickCount = 0;
        }
    }

    /** Stops equipped items from being thrown out and re-picked up to dodge selection rules */
    @SubscribeEvent
    public void onItemToss(ItemTossEvent event) {
        if (GameManager.instance().isRunning()) return;
        if (!EquipmentController.isEquipped(event.getEntityItem().getItem())) return;

        // cancelling only stops the entity spawning: the stack is already out of the inventory, so put it back
        event.setCanceled(true);

        EntityPlayer player = event.getPlayer();
        ItemStack stack = event.getEntityItem().getItem();
        InventoryPlayer inventory = player.inventory;
        // Q-drop empties the held slot: return the stack to where it was
        if (inventory.getCurrentItem().isEmpty()) inventory.setInventorySlotContents(inventory.currentItem, stack);
        else inventory.addItemStackToInventory(stack);

        player.sendMessage(new TextComponentString(
            TextUtils.translateColorCodes("&cCannot drop equipped items")
        ));
        player.openContainer.detectAndSendChanges();
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        if (!GameManager.instance().isItemPickup()) event.setCanceled(true);
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
