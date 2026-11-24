package io.github.duckysmacky.guncore.common.game;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Server-wide top lists for the global stats menu */
public class Leaderboards {
    public static final int TOP_ENTRIES = 10;

    /** Top players per stat */
    public final Map<LeaderboardStat, List<Entry>> stats = new EnumMap<>(LeaderboardStat.class);
    /** Most picked items per category, value = total uses */
    public final Map<UsageCategory, List<Entry>> favorites = new EnumMap<>(UsageCategory.class);

    public static class Entry {
        public final String name;
        public final double value;

        public Entry(String name, double value) {
            this.name = name;
            this.value = value;
        }
    }

    public List<Entry> get(LeaderboardStat stat) {
        return stats.computeIfAbsent(stat, s -> new ArrayList<>());
    }

    public List<Entry> get(UsageCategory category) {
        return favorites.computeIfAbsent(category, c -> new ArrayList<>());
    }

    public void toBytes(ByteBuf buf) {
        for (LeaderboardStat stat : LeaderboardStat.values()) writeEntries(buf, get(stat));
        for (UsageCategory category : UsageCategory.values()) writeEntries(buf, get(category));
    }

    public static Leaderboards fromBytes(ByteBuf buf) {
        Leaderboards boards = new Leaderboards();
        for (LeaderboardStat stat : LeaderboardStat.values()) readEntries(buf, boards.get(stat));
        for (UsageCategory category : UsageCategory.values()) readEntries(buf, boards.get(category));
        return boards;
    }

    private static void writeEntries(ByteBuf buf, List<Entry> entries) {
        buf.writeByte(entries.size());
        for (Entry entry : entries) {
            ByteBufUtils.writeUTF8String(buf, entry.name);
            buf.writeDouble(entry.value);
        }
    }

    private static void readEntries(ByteBuf buf, List<Entry> entries) {
        int count = buf.readByte();
        for (int i = 0; i < count; i++)
            entries.add(new Entry(ByteBufUtils.readUTF8String(buf), buf.readDouble()));
    }
}
