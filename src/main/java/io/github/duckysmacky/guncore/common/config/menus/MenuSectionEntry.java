package io.github.duckysmacky.guncore.common.config.menus;

import io.github.duckysmacky.guncore.common.config.registry.RegistryEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Menu section (gun category, attachment type, kit class, map) which catalog entries are grouped by, referenced by its id */
public class MenuSectionEntry implements RegistryEntry {
    private final boolean enabled;
    private final String id;
    private final String name;
    private final String iconItemId;
    private final List<String> descriptionLines;

    public MenuSectionEntry(
        boolean enabled,
        String id,
        String name,
        String iconItemId,
        List<String> descriptionLines
    ) {
        this.enabled = enabled;
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.iconItemId = Objects.requireNonNull(iconItemId);
        this.descriptionLines = Objects.requireNonNull(descriptionLines);
    }

    private static MenuSectionEntry of(String id, String name, String iconItemId) {
        return new MenuSectionEntry(true, id, name, iconItemId, Collections.emptyList());
    }

    public static List<MenuSectionEntry> defaultGunCategories() {
        return Arrays.asList(
            of("assault_rifle", "Assault Rifles", "minecraft:iron_sword"),
            of("battle_rifle", "Battle Rifles", "minecraft:diamond_sword"),
            of("dmr", "DMRs", "minecraft:arrow"),
            of("lmg", "LMGs", "minecraft:anvil"),
            of("smg", "SMGs", "minecraft:stone_sword"),
            of("shotgun", "Shotguns", "minecraft:fire_charge"),
            of("sniper_rifle", "Sniper Rifles", "minecraft:bow"),
            of("pistol", "Pistols", "minecraft:wooden_sword"),
            of("melee", "Melee Weapons", "minecraft:iron_axe"),
            of("special", "Special Weapons", "minecraft:tnt")
        );
    }

    public static List<MenuSectionEntry> defaultAttachmentTypes() {
        return Arrays.asList(
            of("sights", "Sights", "minecraft:ender_eye"),
            of("muzzles", "Muzzles", "minecraft:flint_and_steel"),
            of("foregrips", "Foregrips", "minecraft:stick"),
            of("lasers", "Lasers", "minecraft:redstone_torch"),
            of("mounts", "Mounts", "minecraft:rail"),
            of("handguards", "Handguards", "minecraft:iron_bars"),
            of("stocks", "Stocks", "minecraft:planks"),
            of("grips", "Grips", "minecraft:lever"),
            of("receivers", "Receivers", "minecraft:hopper"),
            of("conversions", "Conversions", "minecraft:piston"),
            of("magazines", "Magazines", "minecraft:iron_ingot"),
            of("skins", "Skins", "minecraft:dye"),
            of("other", "Other", "minecraft:chest")
        );
    }

    public static List<MenuSectionEntry> defaultKitClasses() {
        return Collections.singletonList(new MenuSectionEntry(
            true, "default", "Default Class", "minecraft:iron_chestplate",
            Collections.singletonList("&7This is an example kit class")
        ));
    }

    public static List<MenuSectionEntry> defaultMaps() {
        return Collections.singletonList(new MenuSectionEntry(
            true, "default", "Default Map", "minecraft:grass",
            Collections.singletonList("&7This is an example map")
        ));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        ItemStackCustomizer icon = ItemStackCustomizer.from(iconItemId).setName("&f&l" + name);
        // Gson skips the constructor, so an omitted field stays null
        if (descriptionLines != null) descriptionLines.forEach(icon::addLoreLine);
        return icon.getItemStack();
    }
}
