package io.github.duckysmacky.guncore.common.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.server.game.GameManager;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SetGameModeVariantPacket implements IMessage {
    private final Gson gson;
    private GameMode.Variant gameModeVariant;

    public SetGameModeVariantPacket() {
        this.gson = new Gson();
    }

    public SetGameModeVariantPacket(GameMode.Variant gameModeVariant) {
        this.gson = new Gson();
        this.gameModeVariant = gameModeVariant;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        String json = ByteBufUtils.readUTF8String(buf);
        this.gameModeVariant = gson.fromJson(json, GameMode.Variant.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String json = gson.toJson(this.gameModeVariant);
        ByteBufUtils.writeUTF8String(buf, json);
    }

    public static class Handler implements IMessageHandler<SetGameModeVariantPacket, IMessage> {
        @Override
        public IMessage onMessage(SetGameModeVariantPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    GameManager.instance().setGameModeVariant(message.gameModeVariant);
                });
            }
            return null;
        }
    }
}