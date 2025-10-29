package io.github.duckysmacky.projectg.network.packets;

import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.gui.menus.MainMenu;
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
            ProjectGMod.PROXY.openMenuPage(new MainMenu());
            return null;
        }
    }
}