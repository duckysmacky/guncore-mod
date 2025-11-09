package io.github.duckysmacky.guncore.common.network.packets;

import com.google.gson.Gson;
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
    private GunEntry gunEntry;

    public EquipGunPacket() {}

    public EquipGunPacket(GunEntry gunEntry) {
        this.gunEntry = gunEntry;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Gson gson = new Gson();
        String json = ByteBufUtils.readUTF8String(buf);
        this.gunEntry = gson.fromJson(json, GunEntry.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        Gson gson = new Gson();
        String json = gson.toJson(this.gunEntry);
        ByteBufUtils.writeUTF8String(buf, json);
        }

    public static class Handler implements IMessageHandler<EquipGunPacket, IMessage> {
        @Override
        public IMessage onMessage(EquipGunPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    GunEntry gun = message.gunEntry;

                    EquipmentController.equipGun(player, gun);

                    PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
                });
            }
            return null;
        }
    }
}