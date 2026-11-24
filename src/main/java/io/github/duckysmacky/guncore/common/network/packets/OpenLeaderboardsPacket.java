package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.client.gui.menu.MenuManager;
import io.github.duckysmacky.guncore.client.gui.menu.pages.LeaderboardsMenuPage;
import io.github.duckysmacky.guncore.common.game.Leaderboards;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class OpenLeaderboardsPacket implements IMessage {
    private Leaderboards leaderboards;

    public OpenLeaderboardsPacket() {}

    public OpenLeaderboardsPacket(Leaderboards leaderboards) {
        this.leaderboards = leaderboards;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        leaderboards = Leaderboards.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        leaderboards.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<OpenLeaderboardsPacket, IMessage> {
        @Override
        public IMessage onMessage(OpenLeaderboardsPacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    // opened from the profile page, so back returns there
                    MenuManager menus = MenuManager.instance();
                    GuncoreMod.PROXY.openMenuPage(new LeaderboardsMenuPage(menus.getLastOpenedMenu(), message.leaderboards));
                });
            }
            return null;
        }
    }
}
