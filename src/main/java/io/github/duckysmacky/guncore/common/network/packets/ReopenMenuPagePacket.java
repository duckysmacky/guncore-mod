package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.MenuManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ReopenMenuPagePacket {
    public ReopenMenuPagePacket() {}

    public static void encode(ReopenMenuPagePacket msg, FriendlyByteBuf buf) {}

    public static ReopenMenuPagePacket decode(FriendlyByteBuf buf) {
        return new ReopenMenuPagePacket();
    }

    public static void handle(ReopenMenuPagePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ctx.get().getSender();

                if (player != null) {
                    BaseMenuPage lastOpened = MenuManager.instance().getLastOpenedMenu(player);
                    (lastOpened != null ? lastOpened : MenuManager.instance().getMainMenu()).open(player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}