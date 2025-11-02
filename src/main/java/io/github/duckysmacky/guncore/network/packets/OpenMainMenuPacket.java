package io.github.duckysmacky.guncore.network.packets;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.gui.menu.pages.MainMenuPage;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenMainMenuPacket implements IMessage {
    public OpenMainMenuPacket() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<OpenMainMenuPacket, IMessage> {
        @Override
        public IMessage onMessage(OpenMainMenuPacket message, MessageContext context) {
            GuncoreMod.PROXY.openMenuPage(new MainMenuPage());
            return null;
        }
    }
}