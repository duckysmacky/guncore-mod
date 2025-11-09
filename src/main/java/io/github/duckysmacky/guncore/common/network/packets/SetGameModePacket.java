package io.github.duckysmacky.guncore.common.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.server.game.GameManager;
import io.github.duckysmacky.guncore.common.game.GameMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class SetGameModePacket {
    private static final Gson gson = new Gson();
    private final GameMode gameMode;

    public SetGameModePacket(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public static void encode(SetGameModePacket msg, FriendlyByteBuf buf) {
        String json = gson.toJson(msg.gameMode);
        buf.writeUtf(json);
    }

    public static SetGameModePacket decode(FriendlyByteBuf buf) {
        String json = buf.readUtf();
        GameMode gameMode = gson.fromJson(json, GameMode.class);
        return new SetGameModePacket(gameMode);
    }

    public static void handle(SetGameModePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GameManager.instance().setGameMode(msg.gameMode);
        });
        ctx.get().setPacketHandled(true);
    }
}