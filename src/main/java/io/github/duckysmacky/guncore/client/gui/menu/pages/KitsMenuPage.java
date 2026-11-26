package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipmentActionPacket;
import io.github.duckysmacky.guncore.common.network.packets.EquipKitPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class KitsMenuPage extends DynamicMenu {
    public KitsMenuPage(BaseMenu parent, MenuSectionEntry kitClass) {
        super(kitClass.getName(), parent, 5, 9);

        List<KitEntry> kits = ConfigManager.instance().getCatalogManager().<KitEntry>getCatalog(CatalogType.KITS).stream()
            .filter(kit -> kit.getKitClass().equals(kitClass.getId()))
            .filter(kit -> ClientGameInfo.instance().isAllowed(kit.getRarity()))
            .sorted(Comparator.comparingInt(kit -> kit.getTier().sortOrder))
            .collect(Collectors.toList());

        BiConsumer<EntityPlayer, KitEntry> select = (player, kit) -> {
            equip(player, kit);
            open(player);
        };
        kits.forEach(kit -> addEntry(new ActionEntry(ClientGameInfo.instance().withRarityScore(kit.getIconItem(), kit.getRarity()), player -> select.accept(player, kit))));
        setDeselectEntry("&7Deselect your kit, selected weapons and gadget are kept", player -> EquipmentActionPacket.send(EquipmentActionPacket.Action.DESELECT_KIT, null));
        setRandomEntry("&7Select a random kit from this class", kits, select);
    }

    public static void equip(EntityPlayer player, KitEntry kit) {
        PacketHandler.instance().sendToServer(new EquipKitPacket(kit));

        player.sendMessage(new TextComponentString("Selected kit: " + kit.getName()));
        player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f, 1f);
    }
}
