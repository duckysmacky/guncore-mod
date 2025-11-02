package io.github.duckysmacky.projectg.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.projectg.game.GameManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ControlRoundPacket implements IMessage {
    private final Gson gson;
    private RoundAction action;

    public ControlRoundPacket() {
        this.gson = new Gson();
    }

    public ControlRoundPacket(RoundAction action) {
        this.gson = new Gson();
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        String json = ByteBufUtils.readUTF8String(buf);
        this.action = gson.fromJson(json, RoundAction.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String json = gson.toJson(this.action);
        ByteBufUtils.writeUTF8String(buf, json);
    }

    public static class Handler implements IMessageHandler<ControlRoundPacket, IMessage> {
        @Override
        public IMessage onMessage(ControlRoundPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    GameManager gameManager = GameManager.instance();

                    switch (message.action) {
                        case START:
                            gameManager.startRound();
                            break;
                        case PAUSE:
                            gameManager.toggleRoundPause();
                            break;
                        case END:
                            gameManager.endRound();
                            break;
                        case RESET:
                            gameManager.resetRound();
                            break;
                    }
                });
            }
            return null;
        }
    }

    public enum RoundAction {
        START, PAUSE, END, RESET;
    }
}