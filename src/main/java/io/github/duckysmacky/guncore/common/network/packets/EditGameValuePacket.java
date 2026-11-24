package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.game.GameValue;
import io.github.duckysmacky.guncore.server.game.ValuePrompt;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Asks the server to prompt the sender in chat for a new value of a game setting
 */
public class EditGameValuePacket implements IMessage {
    private GameValue value;

    public EditGameValuePacket() {}

    public EditGameValuePacket(GameValue value) {
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        value = GameValue.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(value.ordinal());
    }

    public static class Handler implements IMessageHandler<EditGameValuePacket, IMessage> {
        @Override
        public IMessage onMessage(EditGameValuePacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                EntityPlayerMP player = context.getServerHandler().player;
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(
                    () -> ValuePrompt.start(player, message.value)
                );
            }
            return null;
        }
    }
}
