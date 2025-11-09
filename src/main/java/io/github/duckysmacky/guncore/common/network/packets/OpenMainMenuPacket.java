package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.MenuManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenMainMenuPacket {
    public OpenMainMenuPacket() {}

    public static void encode(OpenMainMenuPacket msg, FriendlyByteBuf buf) {}

    public static OpenMainMenuPacket decode(FriendlyByteBuf buf) {
        return new OpenMainMenuPacket();
    }

    public static void handle(OpenMainMenuPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                BaseMenu menu = MenuManager.instance().getMainMenu();
                // TODO: remove proxy
                GuncoreMod.PROXY.openMenuPage(menu);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}