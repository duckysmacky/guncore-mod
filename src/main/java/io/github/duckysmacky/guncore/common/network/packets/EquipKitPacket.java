package io.github.duckysmacky.guncore.common.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class EquipKitPacket {
    private static final Gson gson = new Gson();
    private final KitEntry kitEntry;

    public EquipKitPacket(KitEntry kitEntry) {
        this.kitEntry = kitEntry;
    }

    public static void encode(EquipKitPacket msg, FriendlyByteBuf buf) {
        String json = gson.toJson(msg.kitEntry);
        buf.writeUtf(json);
    }

    public static EquipKitPacket decode(FriendlyByteBuf buf) {
        String json = buf.readUtf();
        KitEntry kitEntry = gson.fromJson(json, KitEntry.class);
        return new EquipKitPacket(kitEntry);
    }

    public static void handle(EquipKitPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    EquipmentController.equipKit(player, msg.kitEntry);
                    PacketHandler.CHANNEL.sendTo(new ReopenMenuPacket(), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}