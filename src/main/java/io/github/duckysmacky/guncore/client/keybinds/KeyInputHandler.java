package io.github.duckysmacky.guncore.client.keybinds;

import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.OpenMainMenuPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class KeyInputHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) return;

        while (ClientKeyBindings.OPEN_MENU_KEY.consumeClick()) {
            PacketHandler.CHANNEL.sendToServer(new OpenMainMenuPacket());
        }
    }
}
