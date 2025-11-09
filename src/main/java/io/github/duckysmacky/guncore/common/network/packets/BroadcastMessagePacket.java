package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.game.ServerBroadcaster;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class BroadcastMessagePacket {
    private final String message;

    public BroadcastMessagePacket(String message) {
        this.message = message;
    }

    public static void encode(BroadcastMessagePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.message);
    }

    public static BroadcastMessagePacket decode(FriendlyByteBuf buf) {
        return new BroadcastMessagePacket(buf.readUtf());
    }

    public static void handle(BroadcastMessagePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                ServerBroadcaster.broadcastAsServer(server, msg.message);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}