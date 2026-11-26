package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipBaseKitPacket;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

/** Equipment menu of the build-a-class mode */
public class BuildEquipmentMenuPage extends StaticMenu {
    public BuildEquipmentMenuPage(BaseMenu parent) {
        super("Build a class", parent, 6, 9);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.DIAMOND))
                .setName("&f&lGet base items")
                .addLoreLine("&7Gives the items every class starts with")
                .getItemStack(),
            player -> {
                PacketHandler.instance().sendToServer(new EquipBaseKitPacket());
                player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
        ), 1, 4);

        addWeaponEntries(false, "minecraft:iron_sword", "&f&lPrimary weapon", "&7Select your main weapon", 2);
        addWeaponEntries(true, "minecraft:wooden_sword", "&f&lSecondary weapon", "&7Select your secondary weapon", 6);

        addSlot(EquipmentType.LETHAL, new ItemStack(Blocks.TNT), "&7Explosives and items made to kill", 1);
        addSlot(EquipmentType.TACTICAL, new ItemStack(Items.ENDER_EYE), "&7Debuff and utility grenades", 2);
        addSlot(EquipmentType.GADGET, new ItemStack(Items.DIAMOND_PICKAXE), "&7The main special item of your class", 3);
        addSlot(EquipmentType.UTILITY_1, new ItemStack(Items.COMPASS), "&7A helpful utility item", 4);
        addSlot(EquipmentType.UTILITY_2, new ItemStack(Items.CLOCK), "&7Another helpful utility item", 5);
        addSlot(EquipmentType.CONSUMABLE, new ItemStack(Items.GOLDEN_APPLE), "&7Healing, food, blocks and other single-use items", 6);
        addSlot(EquipmentType.ARMOR, new ItemStack(Items.IRON_CHESTPLATE), "&7Pick a set of armor", 7);
    }

    private void addWeaponEntries(boolean secondary, String iconId, String name, String lore, int col) {
        addEntry(new SubpageEntry(
            ItemStackCustomizer.from(iconId).setName(name).addLoreLine(lore).getItemStack(),
            new WeaponCategoriesMenuPage(this, secondary)
        ), 2, col);
    }

    private void addSlot(EquipmentType type, ItemStack icon, String lore, int col) {
        addEntry(new SubpageEntry(
            new ItemStackCustomizer(icon).setName("&f&l" + type.display).addLoreLine(lore).getItemStack(),
            new BuildSelectionMenuPage(this, type)
        ), 4, col);
    }
}
