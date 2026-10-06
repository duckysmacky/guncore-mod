package io.github.duckysmacky.guncore.client.game;

import io.github.duckysmacky.guncore.common.game.Team;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Makes teammates glow in team modes. Entity#setGlowing on the client world only sets a local flag,
 * so enemies never see it; the outline color comes from the vanilla team color.
 */
public class AllyGlow {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase != TickEvent.Phase.END || mc.world == null || mc.player == null) return;

        ClientGameInfo info = ClientGameInfo.instance();
        Team own = info.getOwnTeam();
        boolean enabled = info.isTeamMode() && own != Team.NONE;

        for (EntityPlayer player : mc.world.playerEntities) {
            player.setGlowing(enabled && player != mc.player && info.getTeam(player.getUniqueID()) == own);
        }
    }
}
