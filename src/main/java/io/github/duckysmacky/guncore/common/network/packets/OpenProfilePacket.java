package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.client.gui.menu.MenuManager;
import io.github.duckysmacky.guncore.client.gui.menu.pages.ProfileMenuPage;
import io.github.duckysmacky.guncore.common.game.PlayerProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class OpenProfilePacket implements IMessage {
    private PlayerProfile profile;

    public OpenProfilePacket() {}

    public OpenProfilePacket(PlayerProfile profile) {
        this.profile = profile;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        profile = PlayerProfile.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        profile.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<OpenProfilePacket, IMessage> {
        @Override
        public IMessage onMessage(OpenProfilePacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() ->
                    GuncoreMod.PROXY.openMenuPage(new ProfileMenuPage(MenuManager.instance().getMainMenu(), message.profile))
                );
            }
            return null;
        }
    }
}
