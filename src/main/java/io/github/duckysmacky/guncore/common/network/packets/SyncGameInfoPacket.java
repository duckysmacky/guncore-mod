package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.common.config.Settings;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.game.EquipmentMode;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.LoadoutSystem;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.game.Team;
import io.netty.buffer.ByteBuf;
import io.github.duckysmacky.guncore.client.gui.menu.MenuGui;
import io.github.duckysmacky.guncore.client.gui.menu.MenuManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SyncGameInfoPacket implements IMessage {
    private GameMode gameMode;
    private GameMode.Variant gameModeVariant;
    private EquipmentMode equipmentMode;
    private GameState gameState;
    private int roundDurationSec;
    private int roundLengthSec;
    private int killTarget;
    private int startingLives;
    private boolean killOnlyLifeLoss;
    private boolean itemPickup;
    private boolean itemCleanup;
    private boolean mythicsEnabled;
    private int maxAttachments;
    private LoadoutSystem loadoutSystem;
    private String rarityPresetName;
    private List<String> rarityPresetNames;
    private Settings.RarityPreset rarityPreset;
    private Map<String, Boolean> gamerules;
    private Map<UUID, PlayerStats> playerStats;
    private Map<UUID, Team> playerTeams;

    public SyncGameInfoPacket() {}

    public SyncGameInfoPacket(
        GameMode gameMode,
        GameMode.Variant gameModeVariant,
        EquipmentMode equipmentMode,
        GameState gameState,
        int roundDurationSec,
        int roundLengthSec,
        int killTarget,
        int startingLives,
        boolean killOnlyLifeLoss,
        boolean itemPickup,
        boolean itemCleanup,
        boolean mythicsEnabled,
        int maxAttachments,
        LoadoutSystem loadoutSystem,
        String rarityPresetName,
        List<String> rarityPresetNames,
        Settings.RarityPreset rarityPreset,
        Map<String, Boolean> gamerules,
        Map<UUID, PlayerStats> playerStats,
        Map<UUID, Team> playerTeams
    ) {
        this.gameMode = gameMode;
        this.gameModeVariant = gameModeVariant;
        this.equipmentMode = equipmentMode;
        this.gameState = gameState;
        this.roundDurationSec = roundDurationSec;
        this.roundLengthSec = roundLengthSec;
        this.killTarget = killTarget;
        this.startingLives = startingLives;
        this.killOnlyLifeLoss = killOnlyLifeLoss;
        this.itemPickup = itemPickup;
        this.itemCleanup = itemCleanup;
        this.mythicsEnabled = mythicsEnabled;
        this.maxAttachments = maxAttachments;
        this.loadoutSystem = loadoutSystem;
        this.rarityPresetName = rarityPresetName;
        this.rarityPresetNames = rarityPresetNames;
        this.rarityPreset = rarityPreset;
        this.gamerules = gamerules;
        this.playerStats = playerStats;
        this.playerTeams = playerTeams;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gameMode.ordinal());
        buf.writeInt(gameModeVariant.ordinal());
        buf.writeInt(equipmentMode.ordinal());
        buf.writeInt(gameState.ordinal());
        buf.writeInt(roundDurationSec);
        buf.writeInt(roundLengthSec);
        buf.writeInt(killTarget);
        buf.writeInt(startingLives);
        buf.writeBoolean(killOnlyLifeLoss);
        buf.writeBoolean(itemPickup);
        buf.writeBoolean(itemCleanup);
        buf.writeBoolean(mythicsEnabled);
        buf.writeInt(maxAttachments);
        buf.writeInt(loadoutSystem.ordinal());
        ByteBufUtils.writeUTF8String(buf, rarityPresetName);
        buf.writeInt(rarityPresetNames.size());
        rarityPresetNames.forEach(name -> ByteBufUtils.writeUTF8String(buf, name));
        for (Rarity rarity : Rarity.values())
            buf.writeInt(rarityPreset.scoreOf(rarity));
        for (EquipmentMode mode : EquipmentMode.values())
            buf.writeInt(rarityPreset.maxFor(mode));

        buf.writeInt(gamerules.size());
        gamerules.forEach((rule, value) -> {
            ByteBufUtils.writeUTF8String(buf, rule);
            buf.writeBoolean(value);
        });

        buf.writeInt(playerStats.size());
        playerStats.forEach((uuid, s) -> {
            writeUUID(buf, uuid);
            ByteBufUtils.writeUTF8String(buf, s.getUsername());
            buf.writeInt(s.getKills());
            buf.writeInt(s.getDeaths());
            buf.writeInt(s.getLives());
            buf.writeInt(playerTeams.getOrDefault(uuid, Team.NONE).ordinal());
        });
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gameMode = GameMode.values()[buf.readInt()];
        gameModeVariant = GameMode.Variant.values()[buf.readInt()];
        equipmentMode = EquipmentMode.values()[buf.readInt()];
        gameState = GameState.values()[buf.readInt()];
        roundDurationSec = buf.readInt();
        roundLengthSec = buf.readInt();
        killTarget = buf.readInt();
        startingLives = buf.readInt();
        killOnlyLifeLoss = buf.readBoolean();
        itemPickup = buf.readBoolean();
        itemCleanup = buf.readBoolean();
        mythicsEnabled = buf.readBoolean();
        maxAttachments = buf.readInt();
        loadoutSystem = LoadoutSystem.values()[buf.readInt()];
        rarityPresetName = ByteBufUtils.readUTF8String(buf);
        int presetCount = buf.readInt();
        rarityPresetNames = new ArrayList<>();
        for (int i = 0; i < presetCount; i++)
            rarityPresetNames.add(ByteBufUtils.readUTF8String(buf));
        int[] scores = new int[Rarity.values().length];
        for (int i = 0; i < scores.length; i++)
            scores[i] = buf.readInt();
        int[] maxScores = new int[EquipmentMode.values().length];
        for (int i = 0; i < maxScores.length; i++)
            maxScores[i] = buf.readInt();
        rarityPreset = Settings.RarityPreset.create(scores, maxScores);

        int ruleCount = buf.readInt();
        gamerules = new HashMap<>();
        for (int i = 0; i < ruleCount; i++)
            gamerules.put(ByteBufUtils.readUTF8String(buf), buf.readBoolean());

        int size = buf.readInt();
        playerStats = new HashMap<>();
        playerTeams = new HashMap<>();
        for (int i = 0; i < size; i++) {
            UUID uuid = readUUID(buf);
            String username = ByteBufUtils.readUTF8String(buf);
            int kills = buf.readInt();
            int deaths = buf.readInt();
            int lives = buf.readInt();
            playerStats.put(uuid, new PlayerStats(username, kills, deaths, lives));
            playerTeams.put(uuid, Team.values()[buf.readInt()]);
        }
    }

    private void writeUUID(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    private UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static class Handler implements IMessageHandler<SyncGameInfoPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncGameInfoPacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    ClientGameInfo info = ClientGameInfo.instance();
                    // menu pages build their entries (filters, score lore) once, so they have to be rebuilt after the update
                    boolean rebuildMenu = message.mythicsEnabled != info.isMythicsEnabled()
                        || message.maxAttachments != info.getMaxAttachments()
                        || message.loadoutSystem != info.getLoadoutSystem()
                        || Arrays.stream(Rarity.values()).anyMatch(r -> message.rarityPreset.scoreOf(r) != info.getRarityPreset().scoreOf(r));

                    info.update(
                        message.gameMode,
                        message.gameModeVariant,
                        message.equipmentMode,
                        message.gameState,
                        message.roundDurationSec,
                        message.roundLengthSec,
                        message.killTarget,
                        message.startingLives,
                        message.killOnlyLifeLoss,
                        message.itemPickup,
                        message.itemCleanup,
                        message.mythicsEnabled,
                        message.maxAttachments,
                        message.loadoutSystem,
                        message.rarityPresetName,
                        message.rarityPresetNames,
                        message.rarityPreset,
                        message.gamerules,
                        message.playerStats,
                        message.playerTeams
                    );

                    if (rebuildMenu)
                        MenuManager.instance().refreshMenu();

                    GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                    if (screen instanceof MenuGui) ((MenuGui) screen).refresh();
                });
            }
            return null;
        }
    }
}
