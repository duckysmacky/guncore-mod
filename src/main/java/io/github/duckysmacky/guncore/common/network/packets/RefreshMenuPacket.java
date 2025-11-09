package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.client.gui.menu.MenuManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RefreshMenuPacket {
    public RefreshMenuPacket() {}

    public static void encode(RefreshMenuPacket msg, FriendlyByteBuf buf) {}

    public static RefreshMenuPacket decode(FriendlyByteBuf buf) {
        return new RefreshMenuPacket();
    }

    public static void handle(RefreshMenuPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                MenuManager.instance().refreshMenu();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}