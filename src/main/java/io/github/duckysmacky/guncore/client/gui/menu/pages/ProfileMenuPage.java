package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.DisplayEntry;
import io.github.duckysmacky.guncore.common.game.PlayerProfile;
import io.github.duckysmacky.guncore.common.game.UsageCategory;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.RequestLeaderboardsPacket;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfileMenuPage extends StaticMenu {
    private static final UsageCategory[] EQUIPMENT_CATEGORIES = {
        UsageCategory.LETHAL, UsageCategory.TACTICAL, UsageCategory.GADGET,
        UsageCategory.UTILITY, UsageCategory.CONSUMABLE, UsageCategory.ARMOR
    };
    private static final int[] EQUIPMENT_COLS = {1, 2, 3, 5, 6, 7};

    public ProfileMenuPage(BaseMenu parent, PlayerProfile profile) {
        super(profile.name + "'s Profile", parent, 6, 9);

        addEntry(new DisplayEntry(playerHead(profile.name)
            .setName("&f&l" + profile.name)
            .addLoreLine(line("First played", date(profile.firstPlayed)))
            .addLoreLine(line("Last played", date(profile.lastPlayed)))
            .getItemStack()
        ), 1, 4);

        addEntry(new DisplayEntry(new ItemStackCustomizer(new ItemStack(Items.BOOK))
            .setName("&f&lStats")
            .addLoreLine(line("Games played", profile.games))
            .addLoreLine(line("Wins", profile.wins))
            .addLoreLine(line("Losses", profile.losses))
            .addLoreLine(line("Win rate", percent(profile.wins, profile.games)))
            .addLoreLine(line("Kills", profile.kills))
            .addLoreLine(line("Deaths", profile.deaths))
            .addLoreLine(line("K/D", String.format("%.2f", (double) profile.kills / Math.max(1, profile.deaths))))
            .getItemStack()
        ), 2, 3);

        addEntry(new DisplayEntry(new ItemStackCustomizer(new ItemStack(Items.NETHER_STAR))
            .setName("&f&lRecords")
            .addLoreLine(line("Most kills in a round", profile.bestRoundKills))
            .addLoreLine(line("Best kill streak", profile.bestStreak))
            .addLoreLine(line("Times MVP", profile.mvps))
            .addLoreLine(line("Time played", TextUtils.formatDuration(profile.timePlayedSec)))
            .addLoreLine(line("Avg. kills per round", String.format("%.1f", (double) profile.kills / Math.max(1, profile.games))))
            .getItemStack()
        ), 2, 5);

        addEntry(favoritesEntry(profile, UsageCategory.KIT, Item.getItemFromBlock(Blocks.CHEST)), 3, 2);
        addEntry(favoritesEntry(profile, UsageCategory.WEAPON, Items.BOW), 3, 4);
        addEntry(favoritesEntry(profile, UsageCategory.GAME_MODE, Items.GOLDEN_SWORD), 3, 6);

        Item[] icons = {
            Item.getItemFromBlock(Blocks.TNT), Items.SNOWBALL, Items.COMPARATOR,
            Items.SHEARS, Items.GOLDEN_APPLE, Items.DIAMOND_CHESTPLATE
        };
        for (int i = 0; i < EQUIPMENT_CATEGORIES.length; i++)
            addEntry(favoritesEntry(profile, EQUIPMENT_CATEGORIES[i], icons[i]), 4, EQUIPMENT_COLS[i]);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.GOLD_BLOCK))
                .setName("&6&lGlobal stats")
                .addLoreLine("&7Top 10 players and most picked equipment on the server")
                .getItemStack(),
            player -> PacketHandler.instance().sendToServer(new RequestLeaderboardsPacket())
        ), 4, 4);
    }

    private static DisplayEntry favoritesEntry(PlayerProfile profile, UsageCategory category, Item icon) {
        ItemStackCustomizer item = new ItemStackCustomizer(new ItemStack(icon))
            .setName("&f&lFavorite " + category.display.toLowerCase());

        List<PlayerProfile.Usage> favorites = profile.getFavorites(category);
        if (favorites.isEmpty()) item.addLoreLine("&8No data yet");

        for (int i = 0; i < favorites.size(); i++) {
            PlayerProfile.Usage usage = favorites.get(i);
            item.addLoreLine(String.format("&e%d. &f%s &7- %d %s", i + 1, usage.item, usage.uses, usage.uses == 1 ? "game" : "games"));
        }

        return new DisplayEntry(item.getItemStack());
    }

    /** Player head showing the given player's skin */
    public static ItemStackCustomizer playerHead(String name) {
        ItemStack head = new ItemStack(Items.SKULL, 1, 3);
        head.setTagInfo("SkullOwner", new NBTTagString(name));
        return new ItemStackCustomizer(head);
    }

    private static String line(String label, Object value) {
        return "&7" + label + ": &f" + value;
    }

    private static String percent(int part, int total) {
        return total == 0 ? "-" : Math.round(100.0 * part / total) + "%";
    }

    private static String date(long epochMillis) {
        return epochMillis == 0 ? "-" : new SimpleDateFormat("yyyy-MM-dd").format(new Date(epochMillis));
    }
}
