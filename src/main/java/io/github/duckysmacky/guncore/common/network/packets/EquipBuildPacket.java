package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/** Equips a build-a-class item (non-weapon slots); the entry is looked up on the server by name */
public class EquipBuildPacket implements IMessage {
    private EquipmentType type;
    private String entryName;

    public EquipBuildPacket() {}

    public EquipBuildPacket(EquipmentType type, String entryName) {
        this.type = type;
        this.entryName = entryName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.type = EquipmentType.values()[buf.readInt()];
        this.entryName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
        ByteBufUtils.writeUTF8String(buf, entryName);
    }

    public static class Handler implements IMessageHandler<EquipBuildPacket, IMessage> {
        @Override
        public IMessage onMessage(EquipBuildPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    EquipmentType type = message.type;

                    // weapons have their own packet
                    if (type != EquipmentType.MAIN_WEAPON && type != EquipmentType.SECONDARY_WEAPON) {
                        ConfigManager.instance().getCatalogManager().<CatalogEntry>findEntry(type.catalog, message.entryName)
                            .ifPresent(entry -> EquipmentController.equipBuildItem(player, type, entry));
                    }

                    PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
                });
            }
            return null;
        }
    }
}
