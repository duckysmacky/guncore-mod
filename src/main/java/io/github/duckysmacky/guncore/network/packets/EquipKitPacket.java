package io.github.duckysmacky.guncore.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.data.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.game.EquipmentController;
import io.github.duckysmacky.guncore.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class EquipKitPacket implements IMessage {
    private KitEntry kitEntry;

    public EquipKitPacket() {}

    public EquipKitPacket(KitEntry kitEntry) {
        this.kitEntry = kitEntry;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Gson gson = new Gson();
        String json = ByteBufUtils.readUTF8String(buf);
        this.kitEntry = gson.fromJson(json, KitEntry.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        Gson gson = new Gson();
        String json = gson.toJson(this.kitEntry);
        ByteBufUtils.writeUTF8String(buf, json);
    }

    public static class Handler implements IMessageHandler<EquipKitPacket, IMessage> {
        @Override
        public IMessage onMessage(EquipKitPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    KitEntry kit = message.kitEntry;

                    EquipmentController.equipKit(player, kit);

                    PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
                });
            }
            return null;
        }
    }
}