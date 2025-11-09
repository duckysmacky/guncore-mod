package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.MenuManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ReopenMenuPacket {
    public ReopenMenuPacket() {}

    public static void encode(ReopenMenuPacket msg, FriendlyByteBuf buf) {}

    public static ReopenMenuPacket decode(FriendlyByteBuf buf) {
        return new ReopenMenuPacket();
    }

    public static void handle(ReopenMenuPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                BaseMenu menu = MenuManager.instance().getLastOpenedMenu();
                // TODO: remove proxy code
                GuncoreMod.PROXY.openMenuPage(menu);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}