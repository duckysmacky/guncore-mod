package io.github.duckysmacky.guncore.client.game;

import io.github.duckysmacky.guncore.common.config.Settings;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.game.EquipmentMode;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.GameState;
import io.github.duckysmacky.guncore.common.game.LoadoutSystem;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.game.Team;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraft.util.text.TextFormatting;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.ToIntFunction;

public final class ClientGameInfo {
    private static ClientGameInfo instance;
    private GameMode gameMode;
    private GameMode.Variant gameModeVariant;
    private EquipmentMode equipmentMode = EquipmentMode.BUILDER;
    private GameState gameState;
    private int roundDurationSec;
    private int roundLengthSec;
    private int killTarget;
    private int startingLives;
    private boolean killOnlyLifeLoss = true;
    private boolean itemPickup = true;
    private boolean itemCleanup;
    private boolean mythicsEnabled = true;
    private LoadoutSystem loadoutSystem = LoadoutSystem.FLEXIBLE;
    private String rarityPresetName = Settings.DEFAULT_RARITY_PRESET;
    private List<String> rarityPresetNames = Collections.emptyList();
    private Settings.RarityPreset rarityPreset = Settings.createDefault().equipment.selectedPreset();
    private List<Rarity> selectedRarities = Collections.emptyList();
    private Map<String, Boolean> gamerules = Collections.emptyMap();
    private Map<UUID, PlayerStats> playerStats;
    private Map<UUID, Team> playerTeams;

    private ClientGameInfo() {
        this.gameMode = GameMode.FFA;
        this.gameModeVariant = GameMode.Variant.LIVES;
        this.gameState = GameState.NOT_STARTED;
        this.playerStats = Collections.emptyMap();
        this.playerTeams = Collections.emptyMap();
    }

    public static ClientGameInfo instance() {
        if (instance == null) {
            instance = new ClientGameInfo();
        }

        return instance;
    }

    public void update(
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
        this.loadoutSystem = loadoutSystem;
        this.rarityPresetName = rarityPresetName;
        this.rarityPresetNames = rarityPresetNames;
        this.rarityPreset = rarityPreset;
        this.gamerules = gamerules;
        this.playerStats = playerStats;
        this.playerTeams = playerTeams;
    }

    public EquipmentMode getEquipmentMode() {
        return equipmentMode;
    }

    public PlayerStats getStats(UUID uuid, String fallbackName) {
        PlayerStats stats = playerStats.get(uuid);
        return stats != null ? stats : new PlayerStats(fallbackName);
    }

    public PlayerStats getOwnStats() {
        Minecraft mc = Minecraft.getMinecraft();
        return getStats(mc.player.getUniqueID(), mc.player.getName());
    }

    public Team getTeam(UUID uuid) {
        return playerTeams.getOrDefault(uuid, Team.NONE);
    }

    public Team getOwnTeam() {
        return getTeam(Minecraft.getMinecraft().player.getUniqueID());
    }

    /** Sums a stat over all team members, including offline ones (same as the server's win logic) */
    public int getTeamTotal(Team team, ToIntFunction<PlayerStats> stat) {
        return playerStats.entrySet().stream()
            .filter(e -> getTeam(e.getKey()) == team)
            .mapToInt(e -> stat.applyAsInt(e.getValue()))
            .sum();
    }

    public int getTeamKills(Team team) {
        return getTeamTotal(team, PlayerStats::getKills);
    }

    public boolean isTeamMode() {
        return gameMode != GameMode.FFA;
    }

    public boolean isRoundActive() {
        return gameState == GameState.RUNNING || gameState == GameState.PAUSED;
    }

    public String goalText() {
        switch (gameModeVariant) {
            case TIME:
                return "Most kills in " + TextUtils.formatTime(roundLengthSec);
            case LIVES:
                return isTeamMode() ? "Last team standing" : "Last one standing";
            case KILLS:
                return "First to " + killTarget + " kills";
            default:
                return "";
        }
    }

    public boolean hasTime() {
        return gameState != GameState.NOT_STARTED;
    }

    private boolean showsTimeLeft() {
        return gameModeVariant == GameMode.Variant.TIME && gameState != GameState.ENDED;
    }

    public String timeLabel() {
        return showsTimeLeft() ? "Time left" : "Time";
    }

    public String timeValue() {
        return TextUtils.formatTime(showsTimeLeft() ? roundLengthSec - roundDurationSec : roundDurationSec);
    }

    public TextFormatting stateColor() {
        switch (gameState) {
            case RUNNING: return TextFormatting.GREEN;
            case PAUSED: return TextFormatting.YELLOW;
            case ENDED: return TextFormatting.RED;
            default: return TextFormatting.GRAY;
        }
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameMode.Variant getGameModeVariant() {
        return gameModeVariant;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getRoundDurationSec() {
        return roundDurationSec;
    }

    public int getRoundLengthSec() {
        return roundLengthSec;
    }

    public int getKillTarget() {
        return killTarget;
    }

    public int getStartingLives() {
        return startingLives;
    }

    public boolean isKillOnlyLifeLoss() {
        return killOnlyLifeLoss;
    }

    public boolean isItemPickup() {
        return itemPickup;
    }

    public boolean isItemCleanup() {
        return itemCleanup;
    }

    public boolean isMythicsEnabled() {
        return mythicsEnabled;
    }

    /** Whether items of this rarity can be selected; mythics can be disabled */
    public boolean isAllowed(Rarity rarity) {
        return mythicsEnabled || rarity != Rarity.MYTHIC;
    }

    /** Adds the rarity score below the icon's rarity line (its last lore line) while the Rarity Score system is used */
    public ItemStack withRarityScore(ItemStack icon, Rarity rarity) {
        if (loadoutSystem != LoadoutSystem.RARITY_SCORE)
            return icon;

        NBTTagCompound display = icon.getOrCreateSubCompound("display");
        NBTTagList lore = display.getTagList("Lore", Constants.NBT.TAG_STRING);
        lore.appendTag(new NBTTagString(TextFormatting.GRAY + "Rarity Score: " + TextFormatting.WHITE + rarityPreset.scoreOf(rarity)));
        display.setTag("Lore", lore);
        return icon;
    }

    public LoadoutSystem getLoadoutSystem() {
        return loadoutSystem;
    }

    public String getRarityPresetName() {
        return rarityPresetName;
    }

    public List<String> getRarityPresetNames() {
        return rarityPresetNames;
    }

    /** Values of the selected rarity preset */
    public Settings.RarityPreset getRarityPreset() {
        return rarityPreset;
    }

    public void setSelectedRarities(List<Rarity> selectedRarities) {
        this.selectedRarities = selectedRarities;
    }

    /** Rarity score of the own selected equipment, scored with the selected preset */
    public int getRarityScore() {
        return selectedRarities.stream().mapToInt(rarityPreset::scoreOf).sum();
    }

    public int getMaxRarityScore() {
        return rarityPreset.maxFor(equipmentMode);
    }

    public boolean getGamerule(String rule) {
        return gamerules.getOrDefault(rule, false);
    }
}
