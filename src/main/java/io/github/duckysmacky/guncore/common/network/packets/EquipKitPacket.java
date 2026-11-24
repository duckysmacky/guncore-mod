package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/** Equips a kit; the kit is looked up on the server by name, so clients can't change its tier */
public class EquipKitPacket implements IMessage {
    private String kitName;

    public EquipKitPacket() {}

    public EquipKitPacket(KitEntry kit) {
        this.kitName = kit.getName();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.kitName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, kitName);
    }

    public static class Handler implements IMessageHandler<EquipKitPacket, IMessage> {
        @Override
        public IMessage onMessage(EquipKitPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;

                    ConfigManager.instance().getCatalogManager().<KitEntry>findEntry(CatalogType.KITS, message.kitName)
                        .ifPresent(kit -> EquipmentController.equipKit(player, kit));

                    PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
                });
            }
            return null;
        }
    }
}
