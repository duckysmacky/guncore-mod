package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.DisplayEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.GameSettingToggleEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.PromptEntry;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.common.game.EquipmentMode;
import io.github.duckysmacky.guncore.common.game.LoadoutSystem;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.function.BooleanSupplier;

public class EquipmentSettingsMenuPage extends StaticMenu {
    private static final BooleanSupplier RARITY_SCORE_SYSTEM =
        () -> ClientGameInfo.instance().getLoadoutSystem() == LoadoutSystem.RARITY_SCORE;

    public EquipmentSettingsMenuPage(BaseMenu parent) {
        super("Equipment Settings", parent, 6, 9);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.ARMOR_STAND))
                .setName("&f&l" + EquipmentMode.KITS.display)
                .addLoreLine("&7Players choose from premade kits")
                .addLoreLine("&7Global setting, can't be changed during a round")
                .getItemStack(),
            p -> CommandExecutor.execute("game equipment " + EquipmentMode.KITS.id),
            () -> ClientGameInfo.instance().getEquipmentMode() == EquipmentMode.KITS
        ), 1, 1);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.CRAFTING_TABLE))
                .setName("&f&l" + EquipmentMode.BUILDER.display)
                .addLoreLine("&7Players put together their own loadout")
                .addLoreLine("&7Global setting, can't be changed during a round")
                .getItemStack(),
            p -> CommandExecutor.execute("game equipment " + EquipmentMode.BUILDER.id),
            () -> ClientGameInfo.instance().getEquipmentMode() == EquipmentMode.BUILDER
        ), 1, 2);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.NETHER_STAR))
                .setName(Rarity.MYTHIC.color + "&lMythic Items")
                .addLoreLine("&fIf disabled, mythic items and kits are hidden and can't be selected")
                .addLoreLine("&7Already equipped mythic items are kept")
                .getItemStack()
        ), 1, 7);

        addEntry(new GameSettingToggleEntry(
            this, () -> ClientGameInfo.instance().isMythicsEnabled(), "mythics"
        ), 2, 7);

        // loadout systems
        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SLIME_BALL))
                .setName("&f&l" + LoadoutSystem.FLEXIBLE.display)
                .addLoreLine("&7Any items can be combined into a loadout")
                .getItemStack(),
            p -> CommandExecutor.execute("game loadout_system " + LoadoutSystem.FLEXIBLE.id),
            () -> ClientGameInfo.instance().getLoadoutSystem() == LoadoutSystem.FLEXIBLE
        ), 1, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.EMERALD))
                .setName("&f&l" + LoadoutSystem.RARITY_SCORE.display)
                .addLoreLine("&7Every item costs a rarity score, a loadout can't go over the max score")
                .addLoreLine("&7Scores come from the selected rarity preset")
                .getItemStack(),
            p -> CommandExecutor.execute("game loadout_system " + LoadoutSystem.RARITY_SCORE.id),
            () -> ClientGameInfo.instance().getLoadoutSystem() == LoadoutSystem.RARITY_SCORE
        ), 1, 5);

        // values of the selected rarity preset, click to edit in chat (only shown in the Rarity Score system)
        addEntry(new PromptEntry(() -> new ItemStackCustomizer(new ItemStack(Items.NAME_TAG))
            .setName("&f&lRarity Preset")
            .addLoreLine("&fSelected: &a" + ClientGameInfo.instance().getRarityPresetName())
            .addLoreLine("&fAvailable: &7" + String.join(", ", ClientGameInfo.instance().getRarityPresetNames()))
            .addLoreLine("&7The score settings edit the selected preset")
            .addLoreLine("&7New presets can be added in config/guncore/settings.json")
            .addLoreLine("&eClick to select a preset in chat")
            .getItemStack(), LoadoutSystem.PRESET_PROMPT_ID), 2, 4, RARITY_SCORE_SYSTEM);

        addMaxScoreEntry(EquipmentMode.KITS, 1);
        addMaxScoreEntry(EquipmentMode.BUILDER, 2);

        for (Rarity rarity : Rarity.values()) {
            addEntry(new PromptEntry(() -> valueIcon(
                rarity.color + "&l" + rarity.display + " Score",
                ClientGameInfo.instance().getRarityPreset().scoreOf(rarity),
                "&7Rarity score of each " + rarity.display.toLowerCase() + " item"
            ), LoadoutSystem.scorePromptId(rarity)), 3, 1 + rarity.ordinal(), RARITY_SCORE_SYSTEM);
        }
    }

    private void addMaxScoreEntry(EquipmentMode mode, int col) {
        addEntry(new PromptEntry(() -> valueIcon(
            "&f&lMax Rarity Score",
            ClientGameInfo.instance().getRarityPreset().maxFor(mode),
            "&7Highest total rarity score of a " + mode.display.toLowerCase() + " loadout"
        ), LoadoutSystem.maxScorePromptId(mode)), 2, col, RARITY_SCORE_SYSTEM);
    }

    private static ItemStack valueIcon(String name, int value, String usage) {
        return new ItemStackCustomizer(new ItemStack(Items.FEATHER))
            .setName(name)
            .addLoreLine("&fValue: &a" + value + " &7(" + ClientGameInfo.instance().getRarityPresetName() + " preset)")
            .addLoreLine(usage)
            .addLoreLine("&eClick to change the value in chat")
            .addLoreLine("&7Also editable in config/guncore/settings.json")
            .getItemStack();
    }
}
