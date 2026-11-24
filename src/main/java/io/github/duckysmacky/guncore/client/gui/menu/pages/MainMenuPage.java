package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentMode;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.RequestProfilePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;

public class MainMenuPage extends StaticMenu {
    public MainMenuPage() {
        super("Menu", null, 3, 9);

        BaseMenu kitEquipmentPage = new EquipmentMenuPage(this);
        BaseMenu buildEquipmentPage = new BuildEquipmentMenuPage(this);
        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.IRON_CHESTPLATE))
                .setName("&f&lEquipment")
                .addLoreLine("&7Choose your equipment")
                .getItemStack(),
            player -> {
                boolean buildAClass = ClientGameInfo.instance().getEquipmentMode() == EquipmentMode.BUILDER;
                (buildAClass ? buildEquipmentPage : kitEquipmentPage).open(player);
            }
        ), 1, 0);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.COMPASS))
                .setName("&f&lLocations")
                .addLoreLine("&7Choose a location to fight in")
                .getItemStack(),
            new MapsMenuPage(this)
        ), 1, 2);

        addEntry(new ActionEntry(
            () -> ProfileMenuPage.playerHead(Minecraft.getMinecraft().getSession().getUsername())
                .setName("&f&lProfile")
                .addLoreLine("&7Your stats and favorite loadouts")
                .getItemStack(),
            player -> PacketHandler.instance().sendToServer(new RequestProfilePacket())
        ), 1, 4);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.CLOCK))
                .setName("&f&lGame")
                .addLoreLine("&7Set the game modes, join teams and other")
                .getItemStack(),
            new GameMenuPage(this)
        ), 1, 6);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.COMPARATOR))
                .setName("&f&lSettings")
                .addLoreLine("&7Adjust different settings")
                .getItemStack(),
            new SettingsMenuPage(this)
        ), 1, 8);
    }
}
