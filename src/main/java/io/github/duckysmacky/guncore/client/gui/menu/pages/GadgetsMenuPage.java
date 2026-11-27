package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.entries.RarityScoreEntry;
import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.items.ItemEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.network.packets.EquipmentActionPacket;
import io.github.duckysmacky.guncore.common.network.packets.EquipGadgetPacket;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class GadgetsMenuPage extends DynamicMenu {
    public GadgetsMenuPage(BaseMenu parent) {
        super("Gadgets", parent, 9, 9);

        List<ItemEntry> gadgets = ConfigManager.instance().getCatalogManager().<ItemEntry>getCatalog(CatalogType.KIT_GADGETS).stream()
            .filter(gadget -> ClientGameInfo.instance().isAllowed(gadget.getRarity()))
            .sorted(Comparator.comparingInt(gadget -> gadget.getRarity().sortOrder))
            .collect(Collectors.toList());

        BiConsumer<EntityPlayer, ItemEntry> select = (player, gadget) -> {
            PacketHandler.instance().sendToServer(new EquipGadgetPacket(gadget));
            open(player);
        };
        gadgets.forEach(gadget -> addEntry(new ActionEntry(ClientGameInfo.instance().withRarityScore(gadget.getItemStack(), gadget.getRarity()), player -> select.accept(player, gadget), () -> ClientGameInfo.instance().isSelected(EquipmentType.GADGET, gadget.getName()))));
        setCenterEntry(new RarityScoreEntry());
        setDeselectEntry("&7Deselect your gadget", player -> EquipmentActionPacket.send(EquipmentActionPacket.Action.DESELECT, EquipmentType.GADGET));
        setRandomEntry("&7Select a random gadget", gadgets, select);
    }
}
