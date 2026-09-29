package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.game.Team;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncGameInfoPacket {
    private final GameMode gameMode;
    private final GameMode.Variant gameModeVariant;
    private final GameState gameState;
    private final int roundDurationSec;
    private final int roundLengthSec;
    private final int killTarget;
    private final Map<UUID, PlayerStats> playerStats;
    private final Map<UUID, Team> playerTeams;

    public SyncGameInfoPacket(
        GameMode gameMode,
        GameMode.Variant gameModeVariant,
        GameState gameState,
        int roundDurationSec,
        int roundLengthSec,
        int killTarget,
        Map<UUID, PlayerStats> playerStats,
        Map<UUID, Team> playerTeams
    ) {
        this.gameMode = gameMode;
        this.gameModeVariant = gameModeVariant;
        this.gameState = gameState;
        this.roundDurationSec = roundDurationSec;
        this.roundLengthSec = roundLengthSec;
        this.killTarget = killTarget;
        this.playerStats = playerStats;
        this.playerTeams = playerTeams;
    }

    public static void encode(SyncGameInfoPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.gameMode);
        buf.writeEnum(msg.gameModeVariant);
        buf.writeEnum(msg.gameState);
        buf.writeInt(msg.roundDurationSec);
        buf.writeInt(msg.roundLengthSec);
        buf.writeInt(msg.killTarget);

        buf.writeInt(msg.playerStats.size());
        msg.playerStats.forEach((uuid, stats) -> {
            buf.writeUUID(uuid);
            buf.writeUtf(stats.getUsername());
            buf.writeInt(stats.getKills());
            buf.writeInt(stats.getDeaths());
            buf.writeInt(stats.getLives());
            buf.writeEnum(msg.playerTeams.getOrDefault(uuid, Team.NONE));
        });
    }

    public static SyncGameInfoPacket decode(FriendlyByteBuf buf) {
        GameMode gameMode = buf.readEnum(GameMode.class);
        GameMode.Variant gameModeVariant = buf.readEnum(GameMode.Variant.class);
        GameState gameState = buf.readEnum(GameState.class);
        int roundDurationSec = buf.readInt();
        int roundLengthSec = buf.readInt();
        int killTarget = buf.readInt();

        int size = buf.readInt();
        Map<UUID, PlayerStats> playerStats = new HashMap<>();
        Map<UUID, Team> playerTeams = new HashMap<>();
        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUUID();
            String username = buf.readUtf();
            int kills = buf.readInt();
            int deaths = buf.readInt();
            int lives = buf.readInt();
            playerStats.put(uuid, new PlayerStats(username, kills, deaths, lives));
            playerTeams.put(uuid, buf.readEnum(Team.class));
        }

        return new SyncGameInfoPacket(gameMode, gameModeVariant, gameState, roundDurationSec, roundLengthSec, killTarget, playerStats, playerTeams);
    }

    public static void handle(SyncGameInfoPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                ClientGameInfo.instance().update(
                    msg.gameMode,
                    msg.gameModeVariant,
                    msg.gameState,
                    msg.roundDurationSec,
                    msg.roundLengthSec,
                    msg.killTarget,
                    msg.playerStats,
                    msg.playerTeams
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
