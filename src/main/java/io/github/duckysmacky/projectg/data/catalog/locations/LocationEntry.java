package io.github.duckysmacky.projectg.data.catalog.locations;

import com.google.gson.annotations.SerializedName;
import io.github.duckysmacky.projectg.data.catalog.CatalogEntry;
import io.github.duckysmacky.projectg.util.TextUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LocationEntry extends CatalogEntry {
    @SerializedName("mapId")
    private final CityMap map;
    private final LocationCoordinates coordinates;

    public LocationEntry(
        boolean enabled,
        CityMap map,
        String name,
        LocationCoordinates coordinates,
        List<String> descriptionLines
    ) {
        super(enabled, name, descriptionLines);
        this.map = Objects.requireNonNull(map);
        this.coordinates = Objects.requireNonNull(coordinates);
    }

    public static LocationEntry createExample() {
        return new LocationEntry(
            true,
            CityMap.NEWPORT,
            "Spawn",
            new LocationCoordinates(0, 80, 0),
            Arrays.asList(
                "&7The main spawn point of the city.",
                "&7This is an example location."
            )
        );
    }

    public CityMap getMap() {
        return map;
    }

    public LocationCoordinates getCoordinates() {
        return coordinates;
    }

    public ItemStack getIconItem() {
        ItemStack item = new ItemStack(Items.ENDER_PEARL);

        NBTTagCompound displayTag = new NBTTagCompound();

        String coloredName = TextFormatting.WHITE + name;
        displayTag.setString("Name", coloredName);

        NBTTagList loreList = new NBTTagList();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines) {
                String coloredLine = TextUtils.translateColorCodes(line);
                loreList.appendTag(new NBTTagString(coloredLine));
            }
            loreList.appendTag(new NBTTagString(""));
        }

        String mapLine = TextFormatting.GREEN + "Map: " + TextFormatting.WHITE + map.display;
        loreList.appendTag(new NBTTagString(mapLine));

        String coordinatesLine = TextFormatting.GREEN + "Coordinates: " + TextFormatting.WHITE +
            "(" + coordinates.x + ", " + coordinates.y + ", " + coordinates.z + ")";
        loreList.appendTag(new NBTTagString(coordinatesLine));

        displayTag.setTag("Lore", loreList);
        item.setTagInfo("display", displayTag);

        return item;
    }

    public static class LocationCoordinates {
        public final int x;
        public final int y;
        public final int z;

        public LocationCoordinates(
            int x,
            int y,
            int z
        ) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
