package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.server.stats.StatsDatabase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/** Asks the server for the global stats, answered with {@link OpenLeaderboardsPacket} */
public class RequestLeaderboardsPacket implements IMessage {
    public RequestLeaderboardsPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<RequestLeaderboardsPacket, IMessage> {
        @Override
        public IMessage onMessage(RequestLeaderboardsPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    PacketHandler.instance().sendTo(new OpenLeaderboardsPacket(StatsDatabase.instance().loadLeaderboards()), player);
                });
            }
            return null;
        }
    }
}
