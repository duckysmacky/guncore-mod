package io.github.duckysmacky.guncore.common.config.catalog.entries;

import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.util.ItemUtils;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public class GunEntry extends CatalogEntry implements EquippableEntry {
    private final GunCategory category;
    private final Rarity rarity;
    private final String gunId;
    private final String fireMode;
    private final String ammoId;
    private final int ammoAmount;

    public GunEntry(
        boolean enabled,
        String name,
        GunCategory category,
        Rarity rarity,
        String gunId,
        String fireMode,
        String ammoId,
        int ammoAmount,
        List<String> descriptionLines
    ) {
        super(enabled, name, descriptionLines);
        this.category = Objects.requireNonNull(category);
        this.rarity = Objects.requireNonNull(rarity);
        this.gunId = Objects.requireNonNull(gunId);
        this.fireMode = fireMode;
        this.ammoId = Objects.requireNonNull(ammoId);
        this.ammoAmount = ammoAmount;
    }

    public static GunEntry createExample() {
        return new GunEntry(
            true,
            "Glock 17",
            GunCategory.ASSAULT_RIFLE,
            Rarity.COMMON,
            "tacz:glock_17",
            null,
            "tacz:9mm",
            32,
            List.of(
                "&7The classic pistol"
            )
        );
    }

    public ItemStack getGunItem() {
        ItemStack item = ItemUtils.getTACZGun(gunId, fireMode);

        CompoundTag displayTag = item.getOrCreateTagElement("display");

        String coloredName = rarity.color + "" + ChatFormatting.BOLD + name;
        displayTag.putString("Name", Component.Serializer.toJson(Component.literal(coloredName)));

        ListTag loreList = new ListTag();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines) {
                String coloredLine = TextUtils.translateColorCodes(line);
                loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(coloredLine))));
            }
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));
        }

        ItemStack ammo = getAmmoItem();
        String ammoLine = TextUtils.translateColorCodes(String.format("&aIncluded ammo: &f%dx %s", ammoAmount, ammo.getDisplayName().getString()));
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(ammoLine))));
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));

        String rarityLine = rarity.color + "" + ChatFormatting.BOLD + rarity.display.toUpperCase();
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(rarityLine))));

        displayTag.put("Lore", loreList);
        item.addTagElement("display", displayTag);

        return item;
    }

    public ItemStack getAmmoItem() {
        return ItemUtils.getTACZAmmo(ammoId, ammoAmount);
    }

    public GunCategory getCategory() {
        return category;
    }

    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public ItemStack getIcon() {
        return getGunItem();
    }

    @Override
    public ItemStack getItemStack() {
        return getGunItem();
    }

    @Override
    public List<ItemStack> getAdditionalItemStacks() {
        return List.of(getAmmoItem());
    }
}
