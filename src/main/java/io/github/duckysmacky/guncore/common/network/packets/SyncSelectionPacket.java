package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

/** Rarities of the player's selected equipment; the client scores them with its synced rarity preset */
public class SyncSelectionPacket implements IMessage {
    private List<Rarity> rarities;

    public SyncSelectionPacket() {}

    public SyncSelectionPacket(List<Rarity> rarities) {
        this.rarities = rarities;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        rarities = new ArrayList<>(size);
        for (int i = 0; i < size; i++) rarities.add(Rarity.values()[buf.readInt()]);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rarities.size());
        rarities.forEach(rarity -> buf.writeInt(rarity.ordinal()));
    }

    public static class Handler implements IMessageHandler<SyncSelectionPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncSelectionPacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> ClientGameInfo.instance().setSelectedRarities(message.rarities));
            }
            return null;
        }
    }
}
