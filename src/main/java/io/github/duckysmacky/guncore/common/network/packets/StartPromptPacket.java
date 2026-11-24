package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.server.game.ChatPrompt;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Asks the server to start the chat prompt registered under the given id for the sender
 */
public class StartPromptPacket implements IMessage {
    private String promptId;

    public StartPromptPacket() {}

    public StartPromptPacket(String promptId) {
        this.promptId = promptId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        promptId = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, promptId);
    }

    public static class Handler implements IMessageHandler<StartPromptPacket, IMessage> {
        @Override
        public IMessage onMessage(StartPromptPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                EntityPlayerMP player = context.getServerHandler().player;
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(
                    () -> ChatPrompt.start(player, message.promptId)
                );
            }
            return null;
        }
    }
}
