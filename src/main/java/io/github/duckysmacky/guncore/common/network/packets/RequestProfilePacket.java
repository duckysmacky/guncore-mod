package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.server.stats.Profiles;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/** Asks the server for a player's profile, answered with {@link OpenProfilePacket} */
public class RequestProfilePacket implements IMessage {
    /** Empty for the sender's own profile */
    private String targetName;

    public RequestProfilePacket() {
        this.targetName = "";
    }

    public RequestProfilePacket(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        targetName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, targetName);
    }

    public static class Handler implements IMessageHandler<RequestProfilePacket, IMessage> {
        @Override
        public IMessage onMessage(RequestProfilePacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    Profiles.show(player.getServer(), player, message.targetName);
                });
            }
            return null;
        }
    }
}
