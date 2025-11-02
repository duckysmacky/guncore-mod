package io.github.duckysmacky.guncore.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.game.GameManager;
import io.github.duckysmacky.guncore.game.GameMode;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SetGameModePacket implements IMessage {
    private final Gson gson;
    private GameMode gameMode;

    public SetGameModePacket() {
        this.gson = new Gson();
    }

    public SetGameModePacket(GameMode gameMode) {
        this.gson = new Gson();
        this.gameMode = gameMode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        String json = ByteBufUtils.readUTF8String(buf);
        this.gameMode = gson.fromJson(json, GameMode.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String json = gson.toJson(this.gameMode);
        ByteBufUtils.writeUTF8String(buf, json);
    }

    public static class Handler implements IMessageHandler<SetGameModePacket, IMessage> {
        @Override
        public IMessage onMessage(SetGameModePacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    GameManager.instance().setGameMode(message.gameMode);
                });
            }
            return null;
        }
    }
}