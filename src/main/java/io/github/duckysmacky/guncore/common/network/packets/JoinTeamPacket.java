package io.github.duckysmacky.guncore.common.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.server.game.GameManager;
import io.github.duckysmacky.guncore.common.game.Team;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class JoinTeamPacket {
    private static final Gson gson = new Gson();
    private final Team team;

    public JoinTeamPacket(Team team) {
        this.team = team;
    }

    public static void encode(JoinTeamPacket msg, FriendlyByteBuf buf) {
        String json = gson.toJson(msg.team);
        buf.writeUtf(json);
    }

    public static JoinTeamPacket decode(FriendlyByteBuf buf) {
        String json = buf.readUtf();
        Team team = gson.fromJson(json, Team.class);
        return new JoinTeamPacket(team);
    }

    public static void handle(JoinTeamPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                GameManager.instance().joinTeam(player, msg.team);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}