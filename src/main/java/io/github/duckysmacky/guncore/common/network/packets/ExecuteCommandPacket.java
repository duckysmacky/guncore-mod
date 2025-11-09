package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ExecuteCommandPacket {
    private final String command;

    public ExecuteCommandPacket(String command) {
        this.command = command;
    }

    public static void encode(ExecuteCommandPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.command);
    }

    public static ExecuteCommandPacket decode(FriendlyByteBuf buf) {
        return new ExecuteCommandPacket(buf.readUtf());
    }

    public static void handle(ExecuteCommandPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                CommandExecutor.executeAsServer(player.getServer(), msg.command);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}