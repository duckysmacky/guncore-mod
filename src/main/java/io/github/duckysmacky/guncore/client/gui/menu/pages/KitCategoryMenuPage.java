package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionType;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.EquipKitPacket;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KitCategoryMenuPage extends DynamicMenu {
    private final Random random = new Random();
    /** Kits of the shown classes, the random button picks from these */
    private final List<KitEntry> shownKits = new ArrayList<>();

    public KitCategoryMenuPage(BaseMenu parent) {
        super("Kits", parent, 4, 9, true);

        List<KitEntry> kits = ConfigManager.instance().getCatalogManager().getCatalog(CatalogType.KITS);

        for (MenuSectionEntry kitClass : ConfigManager.instance().getMenuSectionManager().getSections(MenuSectionType.KIT_CLASSES)) {
            int before = shownKits.size();
            kits.stream()
                .filter(kit -> kit.getKitClass().equals(kitClass.getId()))
                .forEach(shownKits::add);
            if (shownKits.size() == before) continue;

            addEntry(new SubpageEntry(kitClass.getIcon(), new KitsMenuPage(this, kitClass)));
        }

        setTopEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.DIAMOND))
                .setName("&f&lRandom Kit")
                .addLoreLine("&7Select a random kit")
                .getItemStack(),
            this::selectRandomKit
        ));
    }

    private void selectRandomKit(EntityPlayer player) {
        if (shownKits.isEmpty()) return;

        KitEntry kit = shownKits.get(random.nextInt(shownKits.size()));

        PacketHandler.instance().sendToServer(new EquipKitPacket(kit));

        player.sendMessage(new TextComponentString("Random kit selected: " + kit.getName()));
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }
}
