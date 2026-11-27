package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.server.game.EquipmentManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The player's selected equipment: rarities (scored by the client with its synced rarity preset) and the names of the
 * selected entries, which the menus highlight
 */
public class SyncSelectionPacket implements IMessage {
    private List<Rarity> rarities;
    private Map<EquipmentType, String> names;
    private String kit;
    private Set<String> attachments;

    public SyncSelectionPacket() {}

    public SyncSelectionPacket(EquipmentManager.PlayerEquipment equipment) {
        this.rarities = equipment.rarities();
        this.names = new EnumMap<>(EquipmentType.class);
        equipment.getAll().forEach((type, entry) -> names.put(type, entry.getName()));
        this.kit = equipment.getKit() != null ? equipment.getKit().getName() : null;
        this.attachments = new HashSet<>();
        equipment.getAttachments().forEach(attachment -> attachments.add(attachment.getName()));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        rarities = new ArrayList<>(size);
        for (int i = 0; i < size; i++) rarities.add(Rarity.values()[buf.readInt()]);

        size = buf.readInt();
        names = new EnumMap<>(EquipmentType.class);
        for (int i = 0; i < size; i++) names.put(EquipmentType.values()[buf.readInt()], ByteBufUtils.readUTF8String(buf));

        kit = buf.readBoolean() ? ByteBufUtils.readUTF8String(buf) : null;

        size = buf.readInt();
        attachments = new HashSet<>(size);
        for (int i = 0; i < size; i++) attachments.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rarities.size());
        rarities.forEach(rarity -> buf.writeInt(rarity.ordinal()));

        buf.writeInt(names.size());
        names.forEach((type, name) -> {
            buf.writeInt(type.ordinal());
            ByteBufUtils.writeUTF8String(buf, name);
        });

        buf.writeBoolean(kit != null);
        if (kit != null) ByteBufUtils.writeUTF8String(buf, kit);

        buf.writeInt(attachments.size());
        attachments.forEach(name -> ByteBufUtils.writeUTF8String(buf, name));
    }

    public static class Handler implements IMessageHandler<SyncSelectionPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncSelectionPacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() ->
                    ClientGameInfo.instance().setSelection(message.rarities, message.names, message.kit, message.attachments)
                );
            }
            return null;
        }
    }
}
