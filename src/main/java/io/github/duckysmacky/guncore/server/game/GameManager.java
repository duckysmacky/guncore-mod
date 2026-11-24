package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.Settings;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.game.*;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.SyncGameInfoPacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameManager {
    private static final String ID = "GameManager";
    private static final int[] announcementIntervalsSecs = new int[]{1200, 900, 600, 300, 180, 120, 60, 30, 10, 5, 4, 3, 2, 1};
    private static GameManager instance;
    private final Map<UUID, PlayerStats> playerStats;
    private final Map<Team, List<UUID>> teams;
    private GameState state;
    private int roundDurationSec;

    private GameManager() {
        this.playerStats = new HashMap<>();
        this.teams = new EnumMap<>(Team.class);
        Arrays.stream(Team.values()).forEach(t -> teams.put(t, new ArrayList<>()));
        this.state = GameState.NOT_STARTED;
    }

    public static GameManager instance() {
        if (instance == null) {
            instance = new GameManager();
            instance.updatePlayerList();
        }

        return instance;
    }

    public boolean isRunning() {
        return state == GameState.RUNNING;
    }

    // Creates the vanilla scoreboard teams mirroring the mod ones; safe to call repeatedly
    private static void ensureVanillaTeams() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return;

        Scoreboard scoreboard = server.getWorld(0).getScoreboard();

        for (Team team : Team.values()) {
            String name = team.scoreboardName();

            if (scoreboard.getTeam(name) == null)
                CommandExecutor.execute("scoreboard teams add " + name);

            if (team == Team.NONE) {
                CommandExecutor.execute("scoreboard teams option none nametagVisibility never");
                CommandExecutor.execute("scoreboard teams option none seeFriendlyInvisibles false");
            } else {
                CommandExecutor.execute("scoreboard teams option " + name + " nametagVisibility hideForOtherTeams");
                CommandExecutor.execute("scoreboard teams option " + name + " color " + team.color.getFriendlyName());
            }
        }
    }

    public static void setupWorldSettings() {
        ensureVanillaTeams();

        settings().world = Settings.createDefault().world;
        ConfigManager.instance().saveSettings();

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        applyWorldSettings(server);
        setGamerule(server, "spawnRadius", "0");

        announceSetting("World settings", "defaults");
    }

    /**
     * Announces a changed setting to everyone. Every setting change goes through here to keep the format uniform.
     */
    private static void announceSetting(String setting, String value) {
        ServerBroadcaster.message(String.format("&6&lSetting &8| &f%s &7set to &e&l%s", setting, value));
        ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
    }

    // set directly instead of through the /gamerule command, which would also print its own feedback to ops
    private static void setGamerule(MinecraftServer server, String rule, String value) {
        server.getWorld(0).getGameRules().setOrCreateGameRule(rule, value);
    }

    /**
     * Runs every saved gamerule on the given server. Called on server start and config reload.
     */
    public static void applyWorldSettings(MinecraftServer server) {
        // not configurable: the mod hands out kits itself, so dropped loadouts must never happen
        setGamerule(server, "keepInventory", "true");

        settings().world.gamerules.forEach((rule, value) -> setGamerule(server, rule, String.valueOf(value)));
    }

    public static Set<String> worldGamerules() {
        return settings().world.gamerules.keySet();
    }

    public static boolean isWorldGamerule(String rule) {
        return settings().world.gamerules.containsKey(rule);
    }

    public static void setWorldGamerule(String rule, boolean value) {
        if (!isWorldGamerule(rule)) return;

        settings().world.gamerules.put(rule, value);
        ConfigManager.instance().saveSettings();
        GameManager.instance().syncToClients();
        setGamerule(FMLCommonHandler.instance().getMinecraftServerInstance(), rule, String.valueOf(value));

        announceSetting(rule, value ? "enabled" : "disabled");
    }

    // GameManager only runs on the logical server (dedicated or integrated), so the server instance is always available here
    private void updatePlayerList() {
        updatePlayerListAsServer(FMLCommonHandler.instance().getMinecraftServerInstance());
    }

    public void updatePlayerListAsServer(MinecraftServer server) {
        if (server == null) {
            GuncoreMod.LOGGER.error("[{}] Cannot update player list: server is null!", ID);
            return;
        }

        GuncoreMod.LOGGER.info("[{}] {}", ID, "Updating player list from server");
        ensureVanillaTeams();
        server.getPlayerList().getPlayers().forEach(
            p -> playerStats.computeIfAbsent(p.getUniqueID(), k -> {
                switchTeamTo(p.getUniqueID(), Team.NONE);
                String command = String.format("scoreboard teams join none %s", p.getName());
                CommandExecutor.execute(command);
                return new PlayerStats(p.getName());
            })
        );
    }

    public void startRound() {
        if (state != GameState.RUNNING && state != GameState.PAUSED) {
            updatePlayerList();

            state = GameState.RUNNING;
            roundDurationSec = 0;

            playerStats.values().forEach(p -> {
                p.resetStats();

                if (getGameModeVariant() == GameMode.Variant.LIVES)
                    p.setLives(getStartingLives());
            });

            if (getGameMode() != GameMode.FFA) {
                teams.get(Team.NONE).forEach(p -> {
                    ServerBroadcaster.warning(String.format("&7Player &f&l%s &7didn't join any team!", playerStats.get(p).getUsername()));
                });
            }

            ServerBroadcaster.message("&a&lRound started");
            ServerSoundPlayer.playForAll(SoundEvents.BLOCK_END_PORTAL_SPAWN, 1f, 1f);
        } else {
            ServerBroadcaster.error("&7There is already a round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

    public void toggleRoundPause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            ServerBroadcaster.message("&e&lRound paused");
            ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_BASS, 1f, 1f);
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            ServerBroadcaster.message("&e&lRound continued");
            ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_BASS, 1f, 1f);
        } else {
            ServerBroadcaster.error("&7There is no round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

    public void resetRound() {
        state = GameState.NOT_STARTED;
        roundDurationSec = 0;
        playerStats.values().forEach(PlayerStats::resetStats);
        ServerBroadcaster.message("&c&lRound reset");
        ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_BASS, 1f, 1f);
    }

    public void endRound() {
        if (state == GameState.RUNNING || state == GameState.PAUSED) {
            state = GameState.ENDED;
            roundDurationSec = 0;
            ServerBroadcaster.message("&a&lRound ended");
            ServerSoundPlayer.playForAll(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
            determineWinner();
        } else {
            ServerBroadcaster.error("&7There is no round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

    public void tick(int tickCount) {
        // every second
        if (state == GameState.RUNNING & tickCount % 20 == 0) {
            roundDurationSec++;

            if (getGameModeVariant() == GameMode.Variant.TIME) {
                int roundTimeLeftSecs = getRoundLengthSec() - roundDurationSec;

                if (Arrays.stream(announcementIntervalsSecs).anyMatch(s -> s == roundTimeLeftSecs))
                    printRoundTimeLeft(roundTimeLeftSecs);

                if (roundTimeLeftSecs <= 0)
                    endRound();
            }
        }

        // every 0.5 second
        if (tickCount % 10 == 0) syncToClients();
    }

    /**
     * Sends the game info and settings to every client; also called right after a setting changes
     */
    private void syncToClients() {
        Map<UUID, Team> playerTeams = new HashMap<>();
        playerStats.keySet().forEach(uuid -> playerTeams.put(uuid, getPlayerTeam(uuid)));

        SyncGameInfoPacket packet = new SyncGameInfoPacket(
            getGameMode(), getGameModeVariant(), getEquipmentMode(), state, roundDurationSec,
            getRoundLengthSec(), getKillTarget(), getStartingLives(),
            isKillOnlyLifeLoss(), isItemPickup(), isMythicsEnabled(), getLoadoutSystem(),
            settings().equipment.rarityPreset, new ArrayList<>(settings().equipment.rarityPresets.keySet()), getRarityPreset(),
            settings().world.gamerules,
            playerStats, playerTeams
        );
        PacketHandler.instance().sendToAll(packet);
    }

    public void onPlayerKill(EntityPlayer killer, EntityPlayer victim) {
        if (state != GameState.RUNNING) return;

        PlayerStats killerStats = getStats(killer);
        killerStats.setKills(killerStats.getKills() + 1);

        // a player kill always costs the victim a life, regardless of the kill-only life loss setting
        registerDeath(victim, true);

        ServerSoundPlayer.playFor(killer, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5f, 1f);
    }

    /**
     * Registers a death with no attributable player killer (fall damage, self-inflicted, environmental).
     * Only costs the victim a life when kill-only life loss is disabled.
     */
    public void onEnvironmentalDeath(EntityPlayer victim) {
        if (state != GameState.RUNNING) return;

        registerDeath(victim, !isKillOnlyLifeLoss());
    }

    private void registerDeath(EntityPlayer victim, boolean loseLife) {
        PlayerStats victimStats = getStats(victim);
        victimStats.registerDeath();

        if (loseLife) {
            victimStats.loseLife();
            announceLivesLeft(victim, victimStats);
        }

        checkRoundEndConditions();
    }

    private void announceLivesLeft(EntityPlayer victim, PlayerStats victimStats) {
        if (getGameModeVariant() != GameMode.Variant.LIVES) return;

        if (victimStats.getLives() <= 0) {
            victim.setGameType(GameType.SPECTATOR);
            ServerBroadcaster.message(String.format("&f&l%s &c&lis out of lives!", victim.getName()));
            ServerSoundPlayer.playForAll(SoundEvents.ENTITY_ENDERDRAGON_GROWL, 1f, 1f);
        } else if (victimStats.getLives() == 1) {
            String message = TextUtils.translateColorCodes("&c&lYou only have &f&l1 &e&llife left");
            victim.sendMessage(new TextComponentString(message));
            ServerSoundPlayer.playFor(victim, SoundEvents.BLOCK_GLASS_BREAK, 1f, 1f);
        } else {
            String message = TextUtils.translateColorCodes(String.format("&e&lYou have &f&l%s &e&llives left", victimStats.getLives()));
            victim.sendMessage(new TextComponentString(message));
            ServerSoundPlayer.playFor(victim, SoundEvents.BLOCK_GLASS_BREAK, 1f, 1f);
        }
    }

    public void printRoundTimeLeft(int timeLeftSecs) {
        ServerBroadcaster.message(String.format("&e&lTime Left: &f%s", TextUtils.formatTime(timeLeftSecs)));
        ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_HAT, 1f, 1f);
    }

    public void printGameScoreboard() {
        updatePlayerList();

        ServerBroadcaster.message("&f&l--- Scoreboard ---");

        AtomicInteger place = new AtomicInteger(1);
        playerStats.values().stream()
            .sorted(PlayerStats.RANKING)
            .forEach(p -> ServerBroadcaster.message(String.format(
                "&f %d - %s &7(%s kills, %s deaths, %s lives)",
                place.getAndIncrement(), p.getUsername(), p.getKills(), p.getDeaths(), p.getLives())
            ));
    }

    public void printTeams() {
        updatePlayerList();

        ServerBroadcaster.message("&f&l--- Teams ---");

        teams.forEach((team, uuids) -> {
            ServerBroadcaster.message(team.color + team.display + " Team:");
            uuids.stream()
                .map(playerStats::get)
                .filter(Objects::nonNull)
                .sorted(PlayerStats.RANKING)
                .forEach(p -> ServerBroadcaster.message(String.format(" - &f%s: &7(%s kills, %s deaths, %s lives)",
                    p.getUsername(), p.getKills(), p.getDeaths(), p.getLives())));
        });
    }

    public void setSpawnpoint(EntityPlayerMP target) {
        BlockPos pos = target.getPosition();

        target.setSpawnPoint(pos, true);
        target.setGameType(GameType.SURVIVAL);

        WorldServer world = target.getServerWorld();
        world.setBlockState(pos.down(), Blocks.BEDROCK.getDefaultState());

        ServerBroadcaster.message(String.format("&fSet &f&l%s&f's spawnpoint at &7%s", target.getName(), pos));
        ServerSoundPlayer.playFor(target, SoundEvents.BLOCK_ANVIL_LAND, 1f, 1f);
    }

    public void joinTeam(EntityPlayer player, Team team) {
        if (getGameMode() == GameMode.FFA)
            setGameMode(GameMode.TDM);

        ensureVanillaTeams();
        switchTeamTo(player.getUniqueID(), team);
        String command = String.format("scoreboard teams join %s %s", team.scoreboardName(), player.getName());
        CommandExecutor.execute(command);

        ServerBroadcaster.message(String.format(
            "&7Player &f%s &7joined the %s%s Team",
            player.getName(), team.color, team.display
        ));
        ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
    }

    public void setGameMode(GameMode gameMode) {
        if (state != GameState.RUNNING && state != GameState.PAUSED) {
            if (gameMode == GameMode.FFA) {
                playerStats.keySet().forEach(uuid -> switchTeamTo(uuid, Team.NONE));
                CommandExecutor.execute("scoreboard teams join none @a");
            }

            settings().game.mode = gameMode;
            ConfigManager.instance().saveSettings();
            syncToClients();
            announceSetting("Game mode", gameMode.display);
        } else {
            ServerBroadcaster.error("&7There is already a round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

    public void setGameModeVariant(GameMode.Variant gameModeVariant) {
        if (state != GameState.RUNNING && state != GameState.PAUSED) {
            settings().game.variant = gameModeVariant;
            ConfigManager.instance().saveSettings();
            syncToClients();
            announceSetting("Win condition", gameModeVariant.display);
        } else {
            ServerBroadcaster.error("&7There is already a round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

    /**
     * Chat prompts for the per-gamemode values; they edit the values of the game mode selected when they start
     */
    public static void registerPrompts() {
        for (GameValue value : GameValue.values()) {
            ChatPrompt.register(value.promptId(), player -> {
                GameMode mode = instance().getGameMode();
                return ChatPrompt.integer(
                    String.format("Enter the new %s for %s (%s)", value.display.toLowerCase(), mode.display, value.unit),
                    1, 9999, amount -> instance().setGameValue(mode, value, amount)
                );
            });
        }

        ChatPrompt.register(LoadoutSystem.PRESET_PROMPT_ID, player -> {
            String available = String.join(", ", settings().equipment.rarityPresets.keySet());
            return new ChatPrompt(
                "Enter the name of the rarity preset to use (" + available + ")",
                answer -> instance().selectRarityPreset(answer) ? null : "Unknown preset, available: " + available
            );
        });

        for (Rarity rarity : Rarity.values()) {
            ChatPrompt.register(LoadoutSystem.scorePromptId(rarity), player -> ChatPrompt.integer(
                String.format("Enter the new %s rarity score for the %s preset", rarity.display.toLowerCase(), settings().equipment.rarityPreset),
                0, 999, score -> instance().setRarityScore(rarity, score)
            ));
        }

        for (EquipmentMode mode : EquipmentMode.values()) {
            ChatPrompt.register(LoadoutSystem.maxScorePromptId(mode), player -> ChatPrompt.integer(
                String.format("Enter the new max rarity score of %s loadouts for the %s preset", mode.display.toLowerCase(), settings().equipment.rarityPreset),
                0, 9999, score -> instance().setMaxRarityScore(mode, score)
            ));
        }
    }

    public void setGameValue(GameMode mode, GameValue value, int amount) {
        Settings.GameModeSettings config = settings().game.configFor(mode);
        int stored = amount * value.factor;

        switch (value) {
            case ROUND_TIME: config.roundLengthSec = stored; break;
            case LIVES: config.startingLives = stored; break;
            case KILL_TARGET: config.killTarget = stored; break;
        }

        ConfigManager.instance().saveSettings();
        syncToClients();

        String shown = value == GameValue.ROUND_TIME ? TextUtils.formatTime(stored) : String.valueOf(stored);
        announceSetting(value.display + " (" + mode.display + ")", shown);
    }

    public PlayerStats getStats(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        playerStats.computeIfAbsent(uuid, k -> new PlayerStats(player.getName()));
        return playerStats.get(uuid);
    }

    private static Settings settings() {
        return ConfigManager.instance().getSettings();
    }

    private int getStartingLives() {
        return settings().game.configFor(getGameMode()).startingLives;
    }

    private int getRoundLengthSec() {
        return settings().game.configFor(getGameMode()).roundLengthSec;
    }

    private int getKillTarget() {
        return settings().game.configFor(getGameMode()).killTarget;
    }

    public GameMode getGameMode() {
        return settings().game.mode;
    }

    public GameMode.Variant getGameModeVariant() {
        return settings().game.variant;
    }

    public EquipmentMode getEquipmentMode() {
        return settings().equipment.mode;
    }

    public void setEquipmentMode(EquipmentMode equipmentMode) {
        if (isRunning() || state == GameState.PAUSED) {
            ServerBroadcaster.error("&7There is already a round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        settings().equipment.mode = equipmentMode;
        ConfigManager.instance().saveSettings();
        EquipmentManager.instance().clearAll(); // picks of the other mode would count towards the rarity score
        syncToClients();
        announceSetting("Equipment system", equipmentMode.display);
    }

    public LoadoutSystem getLoadoutSystem() {
        return settings().equipment.loadoutSystem;
    }

    public void setLoadoutSystem(LoadoutSystem loadoutSystem) {
        settings().equipment.loadoutSystem = loadoutSystem;
        ConfigManager.instance().saveSettings();
        syncToClients();
        announceSetting("Loadout system", loadoutSystem.display);
    }

    public Settings.RarityPreset getRarityPreset() {
        return settings().equipment.selectedPreset();
    }

    /**
     * Selects a rarity preset by name, ignoring case; returns false if there is no such preset
     */
    public boolean selectRarityPreset(String name) {
        Optional<String> preset = settings().equipment.rarityPresets.keySet().stream()
            .filter(n -> n.equalsIgnoreCase(name))
            .findFirst();
        if (!preset.isPresent()) return false;

        settings().equipment.rarityPreset = preset.get();
        ConfigManager.instance().saveSettings();
        syncToClients();
        announceSetting("Rarity preset", preset.get());
        return true;
    }

    /** Edits the selected rarity preset */
    public void setRarityScore(Rarity rarity, int score) {
        getRarityPreset().scores.put(Settings.RarityPreset.key(rarity), score);
        ConfigManager.instance().saveSettings();
        syncToClients();
        announceSetting(String.format("%s rarity score (%s)", rarity.display, settings().equipment.rarityPreset), String.valueOf(score));
    }

    /** Edits the selected rarity preset */
    public void setMaxRarityScore(EquipmentMode mode, int score) {
        getRarityPreset().maxScore.put(mode.id, score);
        ConfigManager.instance().saveSettings();
        syncToClients();
        announceSetting(String.format("Max rarity score (%s, %s)", mode.display, settings().equipment.rarityPreset), String.valueOf(score));
    }

    public boolean isMythicsEnabled() {
        return settings().equipment.mythicsEnabled;
    }

    public void setMythicsEnabled(boolean mythicsEnabled) {
        settings().equipment.mythicsEnabled = mythicsEnabled;
        ConfigManager.instance().saveSettings();
        syncToClients();
        announceSetting("Mythic items", mythicsEnabled ? "enabled" : "disabled");
    }

    public boolean isKillOnlyLifeLoss() {
        return settings().game.killOnlyLifeLoss;
    }

    public void setKillOnlyLifeLoss(boolean killOnlyLifeLoss) {
        settings().game.killOnlyLifeLoss = killOnlyLifeLoss;
        ConfigManager.instance().saveSettings();
        syncToClients();
        announceSetting("Kill-only life loss", killOnlyLifeLoss ? "enabled" : "disabled");
    }

    public boolean isItemPickup() {
        return settings().world.itemPickup;
    }

    public void setItemPickup(boolean itemPickup) {
        settings().world.itemPickup = itemPickup;
        ConfigManager.instance().saveSettings();
        syncToClients();
        announceSetting("Item pickup", itemPickup ? "enabled" : "disabled");
    }

    public long getRoundDurationSec() {
        return roundDurationSec;
    }

    private void switchTeamTo(UUID uuid, Team targetTeam) {
        teams.forEach((team, uuids) -> uuids.remove(uuid));
        teams.get(targetTeam).add(uuid);
    }

    private Team getPlayerTeam(UUID uuid) {
        return teams.entrySet().stream()
            .filter(e -> e.getValue().contains(uuid))
            .findFirst()
            .map(Map.Entry::getKey)
            .orElseGet(() -> {
                teams.get(Team.NONE).add(uuid);
                return Team.NONE;
            });
    }

    private void checkRoundEndConditions() {
        if (getGameModeVariant() == GameMode.Variant.LIVES) {
            switch (getGameMode()) {
                case FFA: {
                    long playersAlive = playerStats.values().stream()
                        .filter(p -> p.getLives() > 0)
                        .count();

                    if (playersAlive <= 1)
                        endRound();

                    break;
                }
                default: {
                    Set<Team> aliveTeams = playerStats.entrySet().stream()
                        .filter(e -> e.getValue().getLives() > 0)
                        .map(Map.Entry::getKey)
                        .map(this::getPlayerTeam)
                        .collect(Collectors.toSet());

                    if (aliveTeams.size() <= 1)
                        endRound();

                    break;
                }
            }
        } else if (getGameModeVariant() == GameMode.Variant.KILLS) {
            switch (getGameMode()) {
                case FFA: {
                    boolean targetReached = playerStats.values().stream()
                        .anyMatch(p -> p.getKills() >= getKillTarget());

                    if (targetReached)
                        endRound();

                    break;
                }
                default: {
                    Map<Team, Integer> teamKills = new EnumMap<>(Team.class);
                    playerStats.forEach((uuid, stats) ->
                        teamKills.merge(getPlayerTeam(uuid), stats.getKills(), Integer::sum)
                    );

                    boolean targetReached = teamKills.values().stream()
                        .anyMatch(kills -> kills >= getKillTarget());

                    if (targetReached)
                        endRound();

                    break;
                }
            }
        }
    }

    private void determineWinner() {
        switch (getGameMode()) {
            case FFA: {
                PlayerStats topPlayer = pickFfaWinner();

                if (topPlayer != null) {
                    ServerBroadcaster.message(String.format("&6&lWinner: &f%s &7(%s kills)", topPlayer.getUsername(), topPlayer.getKills()));
                    printGameScoreboard();
                } else {
                    ServerBroadcaster.message("&c&lNobody won lmao");
                    ServerBroadcaster.message("&7yall suck");
                }

                break;
            }
            default: {
                Team topTeam = pickTeamWinner();

                ServerBroadcaster.message(String.format("&6&lWinning team: %s%s Team", topTeam.color, topTeam.display));
                printTeams();

                break;
            }
        }
    }

    private PlayerStats pickFfaWinner() {
        if (getGameModeVariant() == GameMode.Variant.LIVES) {
            Optional<PlayerStats> survivor = playerStats.values().stream()
                .filter(p -> p.getLives() > 0)
                .min(PlayerStats.RANKING);

            if (survivor.isPresent())
                return survivor.get();
        }

        return playerStats.values().stream().min(PlayerStats.RANKING).orElse(null);
    }

    private Team pickTeamWinner() {
        if (getGameModeVariant() == GameMode.Variant.LIVES) {
            Set<Team> aliveTeams = playerStats.entrySet().stream()
                .filter(e -> e.getValue().getLives() > 0)
                .map(Map.Entry::getKey)
                .map(this::getPlayerTeam)
                .collect(Collectors.toSet());

            if (aliveTeams.size() == 1)
                return aliveTeams.iterator().next();
        }

        Map<Team, Integer> teamKills = new EnumMap<>(Team.class);
        playerStats.forEach((uuid, stats) ->
            teamKills.merge(getPlayerTeam(uuid), stats.getKills(), Integer::sum)
        );

        return teamKills.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(Team.NONE);
    }
}