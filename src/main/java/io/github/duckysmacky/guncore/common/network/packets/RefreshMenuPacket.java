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

public class RefreshMenuPacket implements IMessage {
    public RefreshMenuPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<RefreshMenuPacket, IMessage> {
        @Override
        public IMessage onMessage(RefreshMenuPacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    MenuManager.instance().refreshMenu();
                });
            }
            return null;
        }
    }
}