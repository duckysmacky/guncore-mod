package io.github.duckysmacky.guncore.common.network.packets;

import com.google.gson.Gson;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;

public class EquipGunPacket {
    private static final Gson gson = new Gson();
    private final GunEntry gunEntry;

    public EquipGunPacket(GunEntry gunEntry) {
        this.gunEntry = gunEntry;
    }

    public static void encode(EquipGunPacket msg, FriendlyByteBuf buf) {
        String json = gson.toJson(msg.gunEntry);
        buf.writeUtf(json);
    }

    public static EquipGunPacket decode(FriendlyByteBuf buf) {
        String json = buf.readUtf();
        GunEntry gun = gson.fromJson(json, GunEntry.class);
        return new EquipGunPacket(gun);
    }

    public static void handle(EquipGunPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    EquipmentController.equipGun(player, msg.gunEntry);
                    PacketHandler.CHANNEL.sendTo(new ReopenMenuPacket(), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}