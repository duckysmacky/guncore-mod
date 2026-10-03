package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipBaseKitPacket;
import io.github.duckysmacky.guncore.common.network.packets.EquipBuildPacket;
import io.github.duckysmacky.guncore.common.network.packets.EquipGunPacket;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.Random;

/** Equipment menu of the build-a-class mode */
public class BuildEquipmentMenuPage extends StaticMenu {
    private final Random random = new Random();

    public BuildEquipmentMenuPage(BaseMenu parent) {
        super("Build a class", parent, 7, 9);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.DIAMOND))
                .setName("&f&lGet base items")
                .addLoreLine("&7Gives the items every class starts with")
                .addLoreLine("&c&lWARNING: &7Resets the inventory, selected equipment is given back")
                .getItemStack(),
            player -> {
                PacketHandler.instance().sendToServer(new EquipBaseKitPacket());
                player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
        ), 1, 4);

        addWeaponEntries(false, "mw:m4a1", "&f&lPrimary weapon", "&7Select your main weapon", 2);
        addWeaponEntries(true, "mw:m17", "&f&lSecondary weapon", "&7Select your secondary weapon", 6);

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

        addEntry(randomButton(player -> selectRandomGun(player, secondary)), 3, col);
    }

    private void addSlot(EquipmentType type, ItemStack icon, String lore, int col) {
        addEntry(new SubpageEntry(
            new ItemStackCustomizer(icon).setName("&f&l" + type.display).addLoreLine(lore).getItemStack(),
            new BuildSelectionMenuPage(this, type)
        ), 4, col);

        addEntry(randomButton(player -> selectRandom(player, type)), 5, col);
    }

    private ActionEntry randomButton(java.util.function.Consumer<EntityPlayer> action) {
        return new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.PAPER))
                .setName("&f&lSelect random")
                .getItemStack(),
            action
        );
    }

    private void selectRandomGun(EntityPlayer player, boolean secondary) {
        CatalogType type = secondary ? CatalogType.SECONDARY_WEAPONS : CatalogType.PRIMARY_WEAPONS;
        List<GunEntry> guns = ConfigManager.instance().getCatalogManager().getCatalog(type);
        if (guns.isEmpty()) return;

        GunEntry gun = guns.get(random.nextInt(guns.size()));
        PacketHandler.instance().sendToServer(new EquipGunPacket(secondary, gun));
        notifySelected(player, (secondary ? "Secondary weapon" : "Main weapon"), gun.getName());
    }

    private void selectRandom(EntityPlayer player, EquipmentType type) {
        List<CatalogEntry> entries = ConfigManager.instance().getCatalogManager().getCatalog(type.catalog);
        if (entries.isEmpty()) return;

        CatalogEntry entry = entries.get(random.nextInt(entries.size()));
        PacketHandler.instance().sendToServer(new EquipBuildPacket(type, entry.getName()));
        notifySelected(player, type.display, entry.getName());
    }

    private void notifySelected(EntityPlayer player, String slot, String name) {
        player.sendMessage(new TextComponentString("Random " + slot.toLowerCase() + " selected: " + name));
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
}
