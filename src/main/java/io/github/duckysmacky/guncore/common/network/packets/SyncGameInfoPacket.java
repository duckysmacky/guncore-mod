package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncGameInfoPacket implements IMessage {
    private GameMode gameMode;
    private GameMode.Variant gameModeVariant;
    private GameState gameState;
    private long roundStartTime;
    private Map<UUID, PlayerStats> playerStats;

    public SyncGameInfoPacket() {}

    public SyncGameInfoPacket(
        GameMode gameMode,
        GameMode.Variant gameModeVariant,
        GameState gameState,
        long roundStartTime,
        Map<UUID, PlayerStats> playerStats
    ) {
        this.gameMode = gameMode;
        this.gameModeVariant = gameModeVariant;
        this.gameState = gameState;
        this.roundStartTime = roundStartTime;
        this.playerStats = playerStats;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gameMode.ordinal());
        buf.writeInt(gameModeVariant.ordinal());
        buf.writeInt(gameState.ordinal());
        buf.writeLong(roundStartTime);

        buf.writeInt(playerStats.size());
        playerStats.forEach((uuid, s) -> {
            writeUUID(buf, uuid);
            ByteBufUtils.writeUTF8String(buf, s.getUsername());
            buf.writeInt(s.getKills());
            buf.writeInt(s.getDeaths());
            buf.writeInt(s.getLives());
        });
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gameMode = GameMode.values()[buf.readInt()];
        gameModeVariant = GameMode.Variant.values()[buf.readInt()];
        gameState = GameState.values()[buf.readInt()];
        roundStartTime = buf.readLong();

        int size = buf.readInt();
        playerStats = new HashMap<>();
        for (int i = 0; i < size; i++) {
            UUID uuid = readUUID(buf);
            String username = ByteBufUtils.readUTF8String(buf);
            int kills = buf.readInt();
            int deaths = buf.readInt();
            int lives = buf.readInt();
            playerStats.put(uuid, new PlayerStats(username, kills, deaths, lives));
        }
    }

    private void writeUUID(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    private UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static class Handler implements IMessageHandler<SyncGameInfoPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncGameInfoPacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    ClientGameInfo gameInfo = ClientGameInfo.instance();

                    gameInfo.setGameMode(message.gameMode);
                    gameInfo.setGameModeVariant(message.gameModeVariant);
                    gameInfo.setGameState(message.gameState);
                    gameInfo.setRoundStartTime(message.roundStartTime);
                    gameInfo.updatePlayerStats(message.playerStats);
                });
            }
            return null;
        }
    }
}