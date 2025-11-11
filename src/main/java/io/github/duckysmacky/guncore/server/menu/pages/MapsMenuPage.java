package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.config.catalog.entries.CityMap;
import io.github.duckysmacky.guncore.server.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.StaticMenuPage;
import io.github.duckysmacky.guncore.server.menu.pages.dynamic.LocationsMenuPage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class MapsMenuPage extends StaticMenuPage {
    public MapsMenuPage(BaseMenuPage parent) {
        super("Locations", parent, 3, 9);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.GLASS))
                .setName("&f&lOther")
                .addLoreLine("&7Other maps")
                .getItemStack(),
            new LocationsMenuPage(this, CityMap.OTHER)
        ), 1, 4);
    }
}
