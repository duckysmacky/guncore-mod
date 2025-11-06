package io.github.duckysmacky.guncore.network.packets;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.gui.menu.MenuManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ReopenMenuPacket implements IMessage {
    public ReopenMenuPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<ReopenMenuPacket, IMessage> {
        @Override
        public IMessage onMessage(ReopenMenuPacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    BaseMenu menu = MenuManager.instance().getLastOpenedMenu();

                    GuncoreMod.PROXY.openMenuPage(menu);
                });
            }
            return null;
        }
    }
}