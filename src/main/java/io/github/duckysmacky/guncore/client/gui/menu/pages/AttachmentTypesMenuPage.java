package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.RarityScoreEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.attachments.AttachmentEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionType;
import io.github.duckysmacky.guncore.common.network.packets.EquipmentActionPacket;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;

import java.util.List;

public class AttachmentTypesMenuPage extends DynamicMenu {
    /** Button opening this page, for the equipment menus */
    public static SubpageEntry button(BaseMenu parent) {
        return new SubpageEntry(
            ItemStackCustomizer.from("minecraft:tripwire_hook")
                .setName("&f&lAttachments")
                .addLoreLine("&7Select up to " + ClientGameInfo.instance().getMaxAttachments() + " attachments for your weapons")
                .addLoreLine("&7Click a selected attachment again to deselect it")
                .getItemStack(),
            new AttachmentTypesMenuPage(parent)
        );
    }

    public AttachmentTypesMenuPage(BaseMenu parent) {
        super("Attachments", parent, 4, 9, true);

        List<AttachmentEntry> attachments = ConfigManager.instance().getCatalogManager().getCatalog(CatalogType.ATTACHMENTS);

        for (MenuSectionEntry type : ConfigManager.instance().getMenuSectionManager().getSections(MenuSectionType.ATTACHMENT_TYPES)) {
            boolean hasAttachments = attachments.stream()
                .anyMatch(a -> a.getType().equals(type.getId()) && ClientGameInfo.instance().isAllowed(a.getRarity()));
            if (hasAttachments) addEntry(new SubpageEntry(type.getIcon(), new AttachmentsMenuPage(this, type)));
        }

        setCenterEntry(new RarityScoreEntry());
        setDeselectEntry("&7Deselect all of your attachments", player -> EquipmentActionPacket.send(EquipmentActionPacket.Action.DESELECT_ATTACHMENTS, null));
    }
}
