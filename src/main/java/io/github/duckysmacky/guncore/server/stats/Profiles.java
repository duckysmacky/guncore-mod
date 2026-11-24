package io.github.duckysmacky.guncore.server.stats;

import io.github.duckysmacky.guncore.common.game.PlayerProfile;
import io.github.duckysmacky.guncore.common.game.UsageCategory;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.OpenProfilePacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** Resolves player profiles and shows them: as the profile menu to players, as chat lines to the console */
public final class Profiles {
    private Profiles() {}

    /**
     * @param targetName player to show, null or empty for the sender itself
     */
    public static void show(MinecraftServer server, ICommandSender sender, String targetName) {
        boolean self = targetName == null || targetName.isEmpty();

        if (self && !(sender instanceof EntityPlayerMP)) {
            message(sender, "&cConsole must specify a player: /game stats <player>");
            return;
        }

        PlayerProfile profile = self ? loadOwn((EntityPlayerMP) sender) : loadByName(server, targetName);
        if (profile == null) {
            message(sender, "&cNo stats found for player '" + targetName + "'");
            return;
        }

        if (sender instanceof EntityPlayerMP) {
            PacketHandler.instance().sendTo(new OpenProfilePacket(profile), (EntityPlayerMP) sender);
        } else {
            print(sender, profile);
        }
    }

    /** A player always has a profile, empty until their first finished round */
    private static PlayerProfile loadOwn(EntityPlayerMP player) {
        PlayerProfile profile = StatsDatabase.instance().loadProfile(player.getUniqueID());
        if (profile != null) return profile;

        profile = new PlayerProfile();
        profile.name = player.getName();
        return profile;
    }

    private static PlayerProfile loadByName(MinecraftServer server, String name) {
        EntityPlayerMP online = server.getPlayerList().getPlayerByUsername(name);
        if (online != null) return loadOwn(online);

        UUID uuid = StatsDatabase.instance().findUuidByName(name);
        return uuid == null ? null : StatsDatabase.instance().loadProfile(uuid);
    }

    private static void print(ICommandSender sender, PlayerProfile p) {
        message(sender, "&f&l--- Profile: " + p.name + " ---");
        message(sender, String.format("&7Games: &f%d &7(%d wins, %d losses)", p.games, p.wins, p.losses));
        message(sender, String.format("&7Kills: &f%d &7Deaths: &f%d &7Best round: &f%d &7Best streak: &f%d &7MVPs: &f%d",
            p.kills, p.deaths, p.bestRoundKills, p.bestStreak, p.mvps));

        for (UsageCategory category : UsageCategory.values()) {
            List<PlayerProfile.Usage> favorites = p.getFavorites(category);
            if (favorites.isEmpty()) continue;

            message(sender, "&7" + category.display + ": &f" + favorites.stream()
                .map(u -> u.item + " &7(" + u.uses + ")&f")
                .collect(Collectors.joining(", ")));
        }
    }

    private static void message(ICommandSender sender, String text) {
        sender.sendMessage(new TextComponentString(TextUtils.translateColorCodes(text)));
    }
}
