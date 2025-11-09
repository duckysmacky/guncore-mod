package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.server.game.GameManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdatePlayerListPacket {

    public UpdatePlayerListPacket() {}

    public static void encode(UpdatePlayerListPacket msg, FriendlyByteBuf buf) {}

    public static UpdatePlayerListPacket decode(FriendlyByteBuf buf) {
        return new UpdatePlayerListPacket();
    }

    public static void handle(UpdatePlayerListPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                GameManager.instance().updatePlayerListAsServer(player.getServer());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}