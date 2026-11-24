package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.EquippableEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipBuildPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/** Lists the catalog of a single build-a-class slot (items or armor), rarest last */
public class BuildSelectionMenuPage extends DynamicMenu {
    public BuildSelectionMenuPage(BaseMenu parent, EquipmentType type) {
        super(type.display, parent, 9, 9);

        List<EquippableEntry> entries = ConfigManager.instance().getCatalogManager().<CatalogEntry>getCatalog(type.catalog).stream()
            .map(entry -> (EquippableEntry) entry)
            .filter(entry -> ClientGameInfo.instance().isAllowed(entry.getRarity()))
            .sorted(Comparator.comparingInt(entry -> entry.getRarity().sortOrder))
            .collect(Collectors.toList());

        BiConsumer<EntityPlayer, EquippableEntry> select = (player, entry) -> {
            PacketHandler.instance().sendToServer(new EquipBuildPacket(type, entry.getName()));

            player.sendMessage(new TextComponentString(type.display + " selected: " + entry.getName()));
            player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

            open(player);
        };
        entries.forEach(entry -> addEntry(new ActionEntry(ClientGameInfo.instance().withRarityScore(entry.getIcon(), entry.getRarity()), player -> select.accept(player, entry))));
        setRandomEntry("&7Select a random " + type.display.toLowerCase(), entries, select);
    }
}
