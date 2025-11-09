package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
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
    private final Map<UUID, PlayerStats> playerStats;

    public SyncGameInfoPacket(GameMode gameMode, GameMode.Variant gameModeVariant, GameState gameState, int roundDurationSec, Map<UUID, PlayerStats> playerStats) {
        this.gameMode = gameMode;
        this.gameModeVariant = gameModeVariant;
        this.gameState = gameState;
        this.roundDurationSec = roundDurationSec;
        this.playerStats = playerStats;
    }

    public static void encode(SyncGameInfoPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.gameMode);
        buf.writeEnum(msg.gameModeVariant);
        buf.writeEnum(msg.gameState);
        buf.writeInt(msg.roundDurationSec);

        buf.writeInt(msg.playerStats.size());
        msg.playerStats.forEach((uuid, stats) -> {
            buf.writeUUID(uuid);
            buf.writeUtf(stats.getUsername());
            buf.writeInt(stats.getKills());
            buf.writeInt(stats.getDeaths());
            buf.writeInt(stats.getLives());
        });
    }

    public static SyncGameInfoPacket decode(FriendlyByteBuf buf) {
        GameMode gameMode = buf.readEnum(GameMode.class);
        GameMode.Variant gameModeVariant = buf.readEnum(GameMode.Variant.class);
        GameState gameState = buf.readEnum(GameState.class);
        int roundDurationSec = buf.readInt();

        int size = buf.readInt();
        Map<UUID, PlayerStats> playerStats = new HashMap<>();
        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUUID();
            String username = buf.readUtf();
            int kills = buf.readInt();
            int deaths = buf.readInt();
            int lives = buf.readInt();
            playerStats.put(uuid, new PlayerStats(username, kills, deaths, lives));
        }

        return new SyncGameInfoPacket(gameMode, gameModeVariant, gameState, roundDurationSec, playerStats);
    }

    public static void handle(SyncGameInfoPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                ClientGameInfo gameInfo = ClientGameInfo.instance();

                gameInfo.setGameMode(msg.gameMode);
                gameInfo.setGameModeVariant(msg.gameModeVariant);
                gameInfo.setGameState(msg.gameState);
                gameInfo.setRoundDurationSec(msg.roundDurationSec);
                gameInfo.updatePlayerStats(msg.playerStats);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}