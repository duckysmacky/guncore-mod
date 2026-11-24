package io.github.duckysmacky.guncore.common.game;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Lifetime stats of a player, read from the stats database and sent to clients for the profile menu */
public class PlayerProfile {
    public static final int TOP_ENTRIES = 5;

    public String name = "";
    public int games, wins, losses, kills, deaths, bestRoundKills, bestStreak, mvps;
    public long timePlayedSec;
    /** Epoch millis, 0 when the player never finished a round */
    public long firstPlayed, lastPlayed;
    /** Most used items per category, most used first, at most {@link #TOP_ENTRIES} each */
    public final Map<UsageCategory, List<Usage>> favorites = new EnumMap<>(UsageCategory.class);

    public static class Usage {
        public final String item;
        public final int uses;

        public Usage(String item, int uses) {
            this.item = item;
            this.uses = uses;
        }
    }

    public List<Usage> getFavorites(UsageCategory category) {
        return favorites.computeIfAbsent(category, c -> new ArrayList<>());
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        for (int value : new int[]{games, wins, losses, kills, deaths, bestRoundKills, bestStreak, mvps})
            buf.writeInt(value);
        buf.writeLong(timePlayedSec);
        buf.writeLong(firstPlayed);
        buf.writeLong(lastPlayed);

        for (UsageCategory category : UsageCategory.values()) {
            List<Usage> usages = getFavorites(category);
            buf.writeByte(usages.size());
            for (Usage usage : usages) {
                ByteBufUtils.writeUTF8String(buf, usage.item);
                buf.writeInt(usage.uses);
            }
        }
    }

    public static PlayerProfile fromBytes(ByteBuf buf) {
        PlayerProfile profile = new PlayerProfile();
        profile.name = ByteBufUtils.readUTF8String(buf);
        profile.games = buf.readInt();
        profile.wins = buf.readInt();
        profile.losses = buf.readInt();
        profile.kills = buf.readInt();
        profile.deaths = buf.readInt();
        profile.bestRoundKills = buf.readInt();
        profile.bestStreak = buf.readInt();
        profile.mvps = buf.readInt();
        profile.timePlayedSec = buf.readLong();
        profile.firstPlayed = buf.readLong();
        profile.lastPlayed = buf.readLong();

        for (UsageCategory category : UsageCategory.values()) {
            List<Usage> usages = profile.getFavorites(category);
            int count = buf.readByte();
            for (int i = 0; i < count; i++)
                usages.add(new Usage(ByteBufUtils.readUTF8String(buf), buf.readInt()));
        }

        return profile;
    }
}
