package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.DisplayEntry;
import io.github.duckysmacky.guncore.common.game.LeaderboardStat;
import io.github.duckysmacky.guncore.common.game.Leaderboards;
import io.github.duckysmacky.guncore.common.game.UsageCategory;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.DoubleFunction;

public class LeaderboardsMenuPage extends StaticMenu {
    private static final UsageCategory[] EQUIPMENT_CATEGORIES = {
        UsageCategory.LETHAL, UsageCategory.TACTICAL, UsageCategory.GADGET,
        UsageCategory.UTILITY, UsageCategory.CONSUMABLE, UsageCategory.ARMOR
    };
    private static final int[] EQUIPMENT_COLS = {1, 2, 3, 5, 6, 7};

    public LeaderboardsMenuPage(BaseMenu parent, Leaderboards boards) {
        super("Global Stats", parent, 6, 9);

        // row 0 left empty, row 1: stats (cols 1-7), row 2: records (cols 2-6)
        Item[] statIcons = {
            Items.PAPER, Items.GOLD_INGOT, Items.COAL, Items.EMERALD, Items.IRON_SWORD, Items.BONE, Items.DIAMOND_SWORD,
            Items.BLAZE_ROD, Items.BLAZE_POWDER, Items.NETHER_STAR, Items.CLOCK, Items.ARROW
        };
        int statCol = 1, recordCol = 2;
        for (LeaderboardStat stat : LeaderboardStat.values()) {
            String title = "Top " + stat.display.toLowerCase() + (stat.minGames > 0 ? " &7(" + stat.minGames + "+ games)" : "");
            DisplayEntry entry = topEntry(statIcons[stat.ordinal()], title, boards.get(stat), stat::format);

            if (stat.basic) addEntry(entry, 1, statCol++);
            else addEntry(entry, 2, recordCol++);
        }

        addEntry(favoritesEntry(boards, UsageCategory.KIT, Item.getItemFromBlock(Blocks.CHEST)), 3, 2);
        addEntry(favoritesEntry(boards, UsageCategory.WEAPON, Items.BOW), 3, 4);
        addEntry(favoritesEntry(boards, UsageCategory.GAME_MODE, Items.GOLDEN_SWORD), 3, 6);

        Item[] icons = {
            Item.getItemFromBlock(Blocks.TNT), Items.SNOWBALL, Items.COMPARATOR,
            Items.SHEARS, Items.GOLDEN_APPLE, Items.DIAMOND_CHESTPLATE
        };
        for (int i = 0; i < EQUIPMENT_CATEGORIES.length; i++)
            addEntry(favoritesEntry(boards, EQUIPMENT_CATEGORIES[i], icons[i]), 4, EQUIPMENT_COLS[i]);
    }

    private static DisplayEntry favoritesEntry(Leaderboards boards, UsageCategory category, Item icon) {
        return topEntry(icon, "Most picked " + category.display.toLowerCase(), boards.get(category),
            uses -> (long) uses + ((long) uses == 1 ? " game" : " games"));
    }

    private static DisplayEntry topEntry(Item icon, String title, List<Leaderboards.Entry> entries, DoubleFunction<String> format) {
        ItemStackCustomizer item = new ItemStackCustomizer(new ItemStack(icon)).setName("&f&l" + title);

        if (entries.isEmpty()) item.addLoreLine("&8No data yet");

        for (int i = 0; i < entries.size(); i++) {
            Leaderboards.Entry entry = entries.get(i);
            item.addLoreLine(String.format("&e%d. &f%s &7- %s", i + 1, entry.name, format.apply(entry.value)));
        }

        return new DisplayEntry(item.getItemStack());
    }
}
