package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.attachments.AttachmentEntry;
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

/** Selects an attachment, or deselects it if already selected; looked up on the server by name */
public class EquipAttachmentPacket implements IMessage {
    private String attachmentName;

    public EquipAttachmentPacket() {}

    public EquipAttachmentPacket(AttachmentEntry attachment) {
        this.attachmentName = attachment.getName();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.attachmentName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, attachmentName);
    }

    public static class Handler implements IMessageHandler<EquipAttachmentPacket, IMessage> {
        @Override
        public IMessage onMessage(EquipAttachmentPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;

                    ConfigManager.instance().getCatalogManager().<AttachmentEntry>findEntry(CatalogType.ATTACHMENTS, message.attachmentName)
                        .ifPresent(attachment -> EquipmentController.toggleAttachment(player, attachment));

                    PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
                });
            }
            return null;
        }
    }
}
