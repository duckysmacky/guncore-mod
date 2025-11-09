package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

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
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                CommandExecutor.executeAsServer(server, msg.command);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}