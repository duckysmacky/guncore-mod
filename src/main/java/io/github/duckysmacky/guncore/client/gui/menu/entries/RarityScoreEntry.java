package io.github.duckysmacky.guncore.client.gui.menu.entries;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.game.LoadoutSystem;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/** Shows the loadout's current rarity score; rebuilt on every open, empty unless the Rarity Score system is used */
public class RarityScoreEntry extends DisplayEntry {
    public RarityScoreEntry() {
        super(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getIcon() {
        ClientGameInfo info = ClientGameInfo.instance();
        if (info.getLoadoutSystem() != LoadoutSystem.RARITY_SCORE) return ItemStack.EMPTY;

        int score = info.getRarityScore();
        int max = info.getMaxRarityScore();
        return new ItemStackCustomizer(new ItemStack(Items.EXPERIENCE_BOTTLE))
            .setName("&e&lRarity score: &f" + score + "&7/&f" + max)
            .addLoreLine("&7Points left: &f" + Math.max(0, max - score))
            .getItemStack();
    }
}
