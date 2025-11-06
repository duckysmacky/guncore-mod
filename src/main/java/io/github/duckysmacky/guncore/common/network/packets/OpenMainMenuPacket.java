package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.MenuManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class OpenMainMenuPacket implements IMessage {
    public OpenMainMenuPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<OpenMainMenuPacket, IMessage> {
        @Override
        public IMessage onMessage(OpenMainMenuPacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    BaseMenu menu = MenuManager.instance().getMainMenu();

                    GuncoreMod.PROXY.openMenuPage(menu);
                });
            }
            return null;
        }
    }
}