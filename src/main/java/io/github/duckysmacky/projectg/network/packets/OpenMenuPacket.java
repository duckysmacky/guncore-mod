package io.github.duckysmacky.projectg.network.packets;

import io.github.duckysmacky.projectg.gui.menus.MainMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenMenuPacket implements IMessage {
    public OpenMenuPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<OpenMenuPacket, IMessage> {
        @Override
        public IMessage onMessage(OpenMenuPacket message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                new MainMenu().open(player);
            });
            return null;
        }
    }
}