package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
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

public class EquipGunPacket implements IMessage {
    private boolean secondary;
    private String gunName;

    public EquipGunPacket() {}

    public EquipGunPacket(boolean secondary, GunEntry gun) {
        this.secondary = secondary;
        this.gunName = gun.getName();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.secondary = buf.readBoolean();
        this.gunName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(secondary);
        ByteBufUtils.writeUTF8String(buf, gunName);
    }

    public static class Handler implements IMessageHandler<EquipGunPacket, IMessage> {
        @Override
        public IMessage onMessage(EquipGunPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    CatalogType type = message.secondary ? CatalogType.SECONDARY_WEAPONS : CatalogType.PRIMARY_WEAPONS;

                    ConfigManager.instance().getCatalogManager().<GunEntry>findEntry(type, message.gunName)
                        .ifPresent(gun -> EquipmentController.equipGun(player, gun, message.secondary));

                    PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
                });
            }
            return null;
        }
    }
}
