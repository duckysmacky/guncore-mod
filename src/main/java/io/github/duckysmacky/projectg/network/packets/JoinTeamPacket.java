package io.github.duckysmacky.projectg.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.projectg.game.GameManager;
import io.github.duckysmacky.projectg.game.Team;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class JoinTeamPacket implements IMessage {
    private final Gson gson;
    private Team team;

    public JoinTeamPacket() {
        this.gson = new Gson();
    }

    public JoinTeamPacket(Team team) {
        this.gson = new Gson();
        this.team = team;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        String json = ByteBufUtils.readUTF8String(buf);
        this.team = gson.fromJson(json, Team.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String json = gson.toJson(this.team);
        ByteBufUtils.writeUTF8String(buf, json);
    }

    public static class Handler implements IMessageHandler<JoinTeamPacket, IMessage> {
        @Override
        public IMessage onMessage(JoinTeamPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    GameManager.instance().joinTeam(player, message.team);
                });
            }
            return null;
        }
    }
}