package io.github.duckysmacky.guncore.client.gui.menu.entries;

import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.StartPromptPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

/**
 * An entry which closes the menu and asks the player a question in chat. The prompt itself is defined on the
 * server (see {@code ChatPrompt.register}) under {@code promptId}; the menu comes back once it is answered.
 */
public class PromptEntry extends ActionEntry {
    public PromptEntry(ItemStack icon, String promptId) {
        super(icon, p -> open(promptId));
    }

    /**
     * For icons showing values that change while the menu page exists
     */
    public PromptEntry(Supplier<ItemStack> iconSupplier, String promptId) {
        super(iconSupplier, p -> open(promptId));
    }

    private static void open(String promptId) {
        Minecraft.getMinecraft().displayGuiScreen(null); // close the menu so the chat is visible
        PacketHandler.instance().sendToServer(new StartPromptPacket(promptId));
    }
}
