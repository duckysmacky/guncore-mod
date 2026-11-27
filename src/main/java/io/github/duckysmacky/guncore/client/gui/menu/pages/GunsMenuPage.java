package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.entries.RarityScoreEntry;
import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.network.packets.EquipmentActionPacket;
import io.github.duckysmacky.guncore.common.network.packets.EquipGunPacket;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class GunsMenuPage extends DynamicMenu {
    public GunsMenuPage(BaseMenu parent, boolean secondary, MenuSectionEntry gunCategory) {
        super(gunCategory.getName(), parent, 6, 9);

        List<GunEntry> guns = ConfigManager.instance().getCatalogManager().<GunEntry>getCatalog(secondary ? CatalogType.SECONDARY_WEAPONS : CatalogType.PRIMARY_WEAPONS).stream()
            .filter(gun -> gun.getCategory().equals(gunCategory.getId()))
            .filter(gun -> ClientGameInfo.instance().isAllowed(gun.getRarity()))
            .sorted(Comparator.comparingInt(gun -> gun.getRarity().sortOrder))
            .collect(Collectors.toList());

        BiConsumer<EntityPlayer, GunEntry> select = (player, gun) -> {
            equip(player, secondary, gun);
            open(player);
        };
        guns.forEach(gun -> addEntry(new ActionEntry(ClientGameInfo.instance().withRarityScore(gun.getGunItemStack(), gun.getRarity()), player -> select.accept(player, gun), () -> ClientGameInfo.instance().isSelected(secondary ? EquipmentType.SECONDARY_WEAPON : EquipmentType.MAIN_WEAPON, gun.getName()))));
        setCenterEntry(new RarityScoreEntry());
        setDeselectEntry("&7Deselect your " + (secondary ? "secondary" : "main") + " weapon", player -> EquipmentActionPacket.send(EquipmentActionPacket.Action.DESELECT, secondary ? EquipmentType.SECONDARY_WEAPON : EquipmentType.MAIN_WEAPON));
        setRandomEntry("&7Select a random weapon from this category", guns, select);
    }

    public static void equip(EntityPlayer player, boolean secondary, GunEntry gun) {
        PacketHandler.instance().sendToServer(new EquipGunPacket(secondary, gun));
    }
}
