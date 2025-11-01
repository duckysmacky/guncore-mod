package io.github.duckysmacky.projectg.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.projectg.data.config.catalog.guns.GunEntry;
import io.github.duckysmacky.projectg.game.EquipmentManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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

                    EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);

                    if (gun.isSecondary()) {
                        equipment.getSecondaryWeapon().ifPresent(g -> removeGun(player, g));
                        giveGun(player, gun, 1);
                        equipment.setSecondaryWeapon(gun);
                    } else {
                        equipment.getMainWeapon().ifPresent(g -> removeGun(player, g));
                        giveGun(player, gun, 0);
                        equipment.setMainWeapon(gun);
                    }
                });
            }
            return null;
        }

        private void removeGun(EntityPlayerMP player, GunEntry gun) {
            InventoryPlayer inventory = player.inventory;
            int slots = 9 * 4;

            for (int i = 0; i < slots; i++) {
                ItemStack item = inventory.getStackInSlot(i);

                ResourceLocation registryName = item.getItem().getRegistryName();
                if (registryName == null) continue;

                String itemId = registryName.toString();
                if (itemId.equals(gun.getGunItemId()) || itemId.equals(gun.getAmmoItemId()))
                    inventory.setInventorySlotContents(i, new ItemStack(Items.AIR));
            }
        }

        private void giveGun(EntityPlayerMP player, GunEntry gun, int hotbarSlot) {
            player.inventory.mainInventory.set(hotbarSlot, gun.getGunItemStack());

            int ammoSlot = hotbarSlot + 9 * 3; // above that slot
            player.inventory.mainInventory.set(ammoSlot, gun.getAmmoItemStack());
        }
    }
}