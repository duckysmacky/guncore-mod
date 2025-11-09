package io.github.duckysmacky.guncore.common.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.common.config.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class EquipGadgetPacket {
    private static final Gson gson = new Gson();
    private final GadgetEntry gadgetEntry;

    public EquipGadgetPacket(GadgetEntry gadgetEntry) {
        this.gadgetEntry = gadgetEntry;
    }

    public static void encode(EquipGadgetPacket msg, FriendlyByteBuf buf) {
        String json = gson.toJson(msg.gadgetEntry);
        buf.writeUtf(json);
    }

    public static EquipGadgetPacket decode(FriendlyByteBuf buf) {
        String json = buf.readUtf();
        GadgetEntry gadgetEntry = gson.fromJson(json, GadgetEntry.class);
        return new EquipGadgetPacket(gadgetEntry);
    }

    public static void handle(EquipGadgetPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ctx.get().getSender();

                if (player != null) {
                    EquipmentController.equipGadget(player, msg.gadgetEntry);
                    PacketHandler.CHANNEL.sendTo(new ReopenMenuPacket(), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}