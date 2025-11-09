package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class LoadConfigPacket {
    public LoadConfigPacket() {}

    public static void encode(LoadConfigPacket msg, FriendlyByteBuf buf) {}

    public static LoadConfigPacket decode(FriendlyByteBuf buf) {
        return new LoadConfigPacket();
    }

    public static void handle(LoadConfigPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ConfigManager.instance().load();
        });
        ctx.get().setPacketHandled(true);
    }
}
