package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.common.game.EquipmentMode;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EquipmentSettingsMenuPage extends StaticMenu {
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
    }
}
