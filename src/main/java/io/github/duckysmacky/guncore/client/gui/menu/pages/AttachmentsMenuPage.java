package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.RarityScoreEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.attachments.AttachmentEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipAttachmentPacket;
import io.github.duckysmacky.guncore.common.network.packets.EquipmentActionPacket;
import net.minecraft.init.SoundEvents;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Attachments of one type; clicking toggles the attachment */
public class AttachmentsMenuPage extends DynamicMenu {
    public AttachmentsMenuPage(BaseMenu parent, MenuSectionEntry attachmentType) {
        super(attachmentType.getName(), parent, 6, 9);

        ClientGameInfo info = ClientGameInfo.instance();
        List<AttachmentEntry> attachments = ConfigManager.instance().getCatalogManager().<AttachmentEntry>getCatalog(CatalogType.ATTACHMENTS).stream()
            .filter(attachment -> attachment.getType().equals(attachmentType.getId()))
            .filter(attachment -> info.isAllowed(attachment.getRarity()))
            .sorted(Comparator.comparingInt(attachment -> attachment.getRarity().sortOrder))
            .collect(Collectors.toList());

        attachments.forEach(attachment -> addEntry(new ActionEntry(
            info.withRarityScore(attachment.getItemStack(), attachment.getRarity()),
            player -> {
                PacketHandler.instance().sendToServer(new EquipAttachmentPacket(attachment));
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                open(player);
            },
            () -> info.isSelectedAttachment(attachment.getName())
        )));
        setCenterEntry(new RarityScoreEntry());
        setDeselectEntry("&7Deselect all of your attachments", player -> EquipmentActionPacket.send(EquipmentActionPacket.Action.DESELECT_ATTACHMENTS, null));
    }
}
