package io.github.duckysmacky.guncore.common.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.server.game.GameManager;
import io.github.duckysmacky.guncore.common.game.GameMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetGameModeVariantPacket {
    private static final Gson gson = new Gson();
    private final GameMode.Variant gameModeVariant;

    public SetGameModeVariantPacket(GameMode.Variant gameModeVariant) {
        this.gameModeVariant = gameModeVariant;
    }

    public static void encode(SetGameModeVariantPacket msg, FriendlyByteBuf buf) {
        String json = gson.toJson(msg.gameModeVariant);
        buf.writeUtf(json);
    }

    public static SetGameModeVariantPacket decode(FriendlyByteBuf buf) {
        String json = buf.readUtf();
        GameMode.Variant gameMode = gson.fromJson(json, GameMode.Variant.class);
        return new SetGameModeVariantPacket(gameMode);
    }

    public static void handle(SetGameModeVariantPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                GameManager.instance().setGameModeVariant(msg.gameModeVariant);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}