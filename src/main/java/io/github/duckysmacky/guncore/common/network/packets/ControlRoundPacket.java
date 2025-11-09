package io.github.duckysmacky.guncore.common.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.server.game.GameManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ControlRoundPacket {
    private static final Gson gson = new Gson();
    private final RoundAction action;

    public ControlRoundPacket(RoundAction action) {
        this.action = action;
    }

    public static void encode(ControlRoundPacket msg, FriendlyByteBuf buf) {
        String json = gson.toJson(msg.action);
        buf.writeUtf(json);
    }

    public static ControlRoundPacket decode(FriendlyByteBuf buf) {
        String json = buf.readUtf();
        RoundAction action = gson.fromJson(json, RoundAction.class);
        return new ControlRoundPacket(action);
    }

    public static void handle(ControlRoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GameManager gameManager = GameManager.instance();
            switch (msg.action) {
                case START -> gameManager.startRound();
                case PAUSE -> gameManager.toggleRoundPause();
                case END -> gameManager.endRound();
                case RESET -> gameManager.resetRound();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public enum RoundAction {
        START, PAUSE, END, RESET;
    }
}