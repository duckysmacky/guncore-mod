package io.github.duckysmacky.guncore.common.config.catalog.entries;

import com.google.gson.annotations.SerializedName;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
            List.of(
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

        CompoundTag displayTag = item.getOrCreateTagElement("display");

        String coloredName = ChatFormatting.WHITE + "" + ChatFormatting.BOLD + name;
        displayTag.putString("Name", Component.Serializer.toJson(Component.literal(coloredName)));

        ListTag loreList = new ListTag();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines) {
                String coloredLine = TextUtils.translateColorCodes(line);
                loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(coloredLine))));
            }
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));
        }

        String mapLine = ChatFormatting.GREEN + "Map: " + ChatFormatting.WHITE + map.display;
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(mapLine))));

        String coordinatesLine = TextUtils.translateColorCodes(String.format("&aCoordinates: &f(%d, %d, %d)", coordinates.x, coordinates.y, coordinates.z));
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(coordinatesLine))));

        displayTag.put("Lore", loreList);
        item.addTagElement("display", displayTag);

        return item;
    }

    public record LocationCoordinates(int x, int y, int z) {}
}
