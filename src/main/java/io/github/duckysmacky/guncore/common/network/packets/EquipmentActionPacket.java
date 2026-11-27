package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/** Equipment menu action which does not pick a catalog entry; {@code type} is only used by slot actions */
public class EquipmentActionPacket implements IMessage {
    public enum Action {
        BASE_ITEMS,
        RESTORE,
        RESET,
        DESELECT,
        DESELECT_KIT,
        DESELECT_ATTACHMENTS
    }

    private Action action;
    private EquipmentType type;

    public EquipmentActionPacket() {}

    public EquipmentActionPacket(Action action) {
        this(action, null);
    }

    public EquipmentActionPacket(Action action, EquipmentType type) {
        this.action = action;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.action = Action.values()[buf.readInt()];
        int typeOrdinal = buf.readInt();
        this.type = typeOrdinal < 0 ? null : EquipmentType.values()[typeOrdinal];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(action.ordinal());
        buf.writeInt(type == null ? -1 : type.ordinal());
    }

    /** Client: sends the action to the server */
    public static void send(Action action, EquipmentType type) {
        PacketHandler.instance().sendToServer(new EquipmentActionPacket(action, type));
    }

    public static class Handler implements IMessageHandler<EquipmentActionPacket, IMessage> {
        @Override
        public IMessage onMessage(EquipmentActionPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;

                    switch (message.action) {
                        case BASE_ITEMS:
                            EquipmentController.giveBaseItems(player);
                            break;
                        case RESTORE:
                            EquipmentController.restoreSelection(player);
                            break;
                        case RESET:
                            EquipmentController.resetSelection(player);
                            break;
                        case DESELECT:
                            if (message.type != null) EquipmentController.deselect(player, message.type);
                            break;
                        case DESELECT_KIT:
                            EquipmentController.deselectKit(player);
                            break;
                        case DESELECT_ATTACHMENTS:
                            EquipmentController.deselectAttachments(player);
                            break;
                    }

                    PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
                });
            }
            return null;
        }
    }
}
