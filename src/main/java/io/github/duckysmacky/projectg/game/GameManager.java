package io.github.duckysmacky.projectg.game;

import io.github.duckysmacky.projectg.data.config.ConfigLoader;
import io.github.duckysmacky.projectg.data.config.GameConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    private static GameManager instance;
    private final MinecraftServer server;
    private final CommandExecutor commandExecutor;
    private final Map<UUID, PlayerStats> playerStats;
    private final Map<Team, List<UUID>> teams;
    private final ServerBroadcaster broadcaster;
    private GameMode gameMode;
    private GameMode.Variant gameModeVariant;
    private GameState state;
    private long roundStartTimeSec;
    private long roundTimeSec;

    private GameManager() {
        this.server = FMLCommonHandler.instance().getMinecraftServerInstance();
        this.commandExecutor = new CommandExecutor();
        this.playerStats = new HashMap<>();
        this.teams = new EnumMap<>(Team.class);
        Arrays.stream(Team.values()).forEach(t -> teams.put(t, new ArrayList<>()));

        server.getPlayerList().getPlayers().forEach(p -> {
            playerStats.put(p.getUniqueID(), new PlayerStats(p.getName()));
            teams.get(Team.NONE).add(p.getUniqueID());
        });

        this.broadcaster = new ServerBroadcaster();
        this.gameMode = GameMode.TDM;
        this.gameModeVariant = GameMode.Variant.LIVES;
        this.state = GameState.NOT_STARTED;
    }

    public static GameManager instance() {
        if (instance == null) {
            instance = new GameManager();
        }

        return instance;
    }

    public void startRound() {
        if (state != GameState.RUNNING && state != GameState.PAUSED) {
            state = GameState.RUNNING;
            roundStartTimeSec = System.currentTimeMillis();

            server.getPlayerList().getPlayers().forEach(player -> {
                PlayerStats stats = getStats(player);

                if (gameMode != GameMode.FFA && teams.get(Team.NONE).contains(player.getUniqueID()))
                    broadcaster.broadcast(String.format("&e&lWARNING: &fplayer %s didn't join any team!", player.getName()));

                stats.resetStats();

                if (gameModeVariant == GameMode.Variant.LIVES)
                    stats.setLives(getStartingLives());
            });

            broadcaster.broadcast("&a&lRound started");
            broadcaster.playSound(SoundEvents.BLOCK_END_PORTAL_SPAWN);
        } else {
            broadcaster.broadcast("&cThere is already a round in progress!");
            broadcaster.playSound(SoundEvents.ENTITY_VILLAGER_NO);
        }
    }

    public void toggleRoundPause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            broadcaster.broadcast("&e&lRound paused");
            broadcaster.playSound(SoundEvents.BLOCK_NOTE_BASS);
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            broadcaster.broadcast("&e&lRound continued");
            broadcaster.playSound(SoundEvents.BLOCK_NOTE_BASS);
        } else {
            broadcaster.broadcast("&cThere is no round in progress!");
            broadcaster.playSound(SoundEvents.ENTITY_VILLAGER_NO);
        }
    }

    public void resetRound() {
        state = GameState.NOT_STARTED;
        playerStats.values().forEach(PlayerStats::resetStats);
        broadcaster.broadcast("&c&lRound reset");
        broadcaster.playSound(SoundEvents.BLOCK_NOTE_BASS);
    }

    public void endRound() {
        if (state == GameState.RUNNING || state == GameState.PAUSED) {
            state = GameState.ENDED;
            broadcaster.broadcast("&a&lRound ended");
            broadcaster.playSound(SoundEvents.BLOCK_NOTE_BASS);
            determineWinner();
        } else {
            broadcaster.broadcast("&cThere is no round in progress!");
            broadcaster.playSound(SoundEvents.ENTITY_VILLAGER_NO);
        }
    }

    public void tick() {
        if (state != GameState.RUNNING) return;

        roundTimeSec = (System.currentTimeMillis() - roundStartTimeSec) / 1000L;

        if (gameModeVariant == GameMode.Variant.TIME && roundTimeSec >= getRoundDurationSecs()) {
            endRound();
        }
    }

    public void onPlayerKill(EntityPlayer killer, EntityPlayer victim) {
        if (state != GameState.RUNNING) return;

        PlayerStats killerStats = getStats(killer);
        killerStats.addKills(1);

        PlayerStats victimStats = getStats(victim);
        victimStats.registerDeath();

        if (gameModeVariant == GameMode.Variant.LIVES && victimStats.getLives() <= 0) {
            victim.setGameType(GameType.SPECTATOR);
            broadcaster.broadcast(String.format("&f&l%s &cis out of lives!", victim.getName()));
            broadcaster.playSound(SoundEvents.ENTITY_ENDERDRAGON_GROWL);
        }

        checkRoundEndConditions();
    }

    public void printGameScoreboard() {
        broadcaster.broadcast("&f&l--- Scoreboard ---");

        playerStats.values()
            .forEach(p ->
                broadcaster.broadcast(
                    String.format("&f%s: &7%s kills, %s deaths, %s lives",
                        p.getUsername(), p.getKills(), p.getDeaths(), p.getLives())
                )
            );
    }

    public void printTeams() {
        broadcaster.broadcast("&f&l--- Teams ---");

        teams.forEach((team, uuids) -> {
            broadcaster.broadcast(team.color + team.display + " Team:");
            uuids.forEach(uuid -> {
                PlayerStats p = playerStats.get(uuid);

                if (p != null)
                    broadcaster.broadcast(String.format(" - &f%s: &7(%s kills, %s deaths, %s lives)",
                        p.getUsername(), p.getKills(), p.getDeaths(), p.getLives()));
            });
        });
    }

    public void setupScoreboardTeams() {
        String[] commandChain = new String[]{
            "scoreboard objectives add Deaths deathCount",
            "scoreboard objectives setdisplay sidebar Deaths",
            "scoreboard players set @a Deaths 0",
            "scoreboard teams add none",
            "scoreboard teams add blue",
            "scoreboard teams add red",
            "scoreboard teams add yellow",
            "scoreboard teams add green",
            "scoreboard teams add purple",
            "scoreboard teams option none nametagVisibility never",
            "scoreboard teams option blue nametagVisibility hideForOtherTeams",
            "scoreboard teams option red nametagVisibility hideForOtherTeams",
            "scoreboard teams option yellow nametagVisibility hideForOtherTeams",
            "scoreboard teams option green nametagVisibility hideForOtherTeams",
            "scoreboard teams option purple nametagVisibility hideForOtherTeams"
        };

        Arrays.stream(commandChain).forEach(commandExecutor::execute);
        broadcaster.broadcast("&a&lTeams setup complete");
        broadcaster.playSound(SoundEvents.BLOCK_NOTE_HARP);
    }

    public void resetScoreboardDeaths() {
        commandExecutor.execute("scoreboard players set @a Deaths 0");
        broadcaster.broadcast("&a&lScoreboard death counter reset");
        broadcaster.playSound(SoundEvents.BLOCK_NOTE_HARP);
    }

    public void joinTeam(EntityPlayer player, Team team) {
        if (gameMode == GameMode.FFA)
            setGameMode(GameMode.TDM);

        switchTeamTo(player.getUniqueID(), team);
        String command = String.format("scoreboard teams join %s %s", team.display.toLowerCase(), player.getName());
        commandExecutor.execute(command);

        broadcaster.broadcast(String.format(
            "&7Player &f%s &7joined the %s%s Team",
            player.getName(), team.color, team.display
        ));
        broadcaster.playSound(SoundEvents.BLOCK_NOTE_HARP);
    }

    public void setGameMode(GameMode gameMode) {
        if (gameMode == GameMode.FFA) {
            playerStats.keySet().forEach(uuid -> switchTeamTo(uuid, Team.NONE));
            commandExecutor.execute("scoreboard teams join none @a");
        }

        this.gameMode = gameMode;
        broadcaster.broadcast("&a&lGame mode set to &f" + gameMode.display);
        broadcaster.playSound(SoundEvents.BLOCK_NOTE_HARP);
    }

    public void setGameModeVariant(GameMode.Variant gameModeVariant) {
        this.gameModeVariant = gameModeVariant;
        broadcaster.broadcast("&a&lGame mode variant set to &f" + gameModeVariant.display);
        broadcaster.playSound(SoundEvents.BLOCK_NOTE_HARP);
    }

    public PlayerStats getStats(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        playerStats.computeIfAbsent(uuid, k -> new PlayerStats(player.getName()));
        return playerStats.get(uuid);
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameMode.Variant getGameModeVariant() {
        return gameModeVariant;
    }

    public long getRoundTime() {
        return roundTimeSec;
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

    private int getStartingLives() {
        GameConfig gameConfig = ConfigLoader.instance().getCachedGameConfig();

        switch (gameMode) {
            case FFA:
                return gameConfig.ffaConfig.startingLives;
            case TDM:
                return gameConfig.tdmConfig.startingLives;
            case HOSTAGE:
                return gameConfig.hostageConfig.startingLives;
            default:
                return 5;
        }
    }

    private int getRoundDurationSecs() {
        GameConfig gameConfig = ConfigLoader.instance().getCachedGameConfig();

        switch (gameMode) {
            case FFA:
                return gameConfig.ffaConfig.roundDurationSecs;
            case TDM:
                return gameConfig.tdmConfig.roundDurationSecs;
            case HOSTAGE:
                return gameConfig.hostageConfig.roundDurationSecs;
            default:
                return 600;
        }
    }

    private void checkRoundEndConditions() {
        if (gameModeVariant == GameMode.Variant.LIVES) {
            switch (gameMode) {
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
        }
    }

    private void determineWinner() {
        switch (gameMode) {
            case FFA: {
                PlayerStats topPlayer = playerStats.values().stream()
                    .max(Comparator.comparingInt(PlayerStats::getKills))
                    .orElse(null);

                if (topPlayer != null) {
                    broadcaster.broadcast(String.format("&6&lWinner: &f%s &7(%s kills)", topPlayer.getUsername(), topPlayer.getKills()));
                    printGameScoreboard();
                }
                else {
                    broadcaster.broadcast("&c&lNobody won lmao");
                    broadcaster.broadcast("&7yall suck");
                }

                break;
            }
            default: {
                Map<Team, Integer> teamKills = new EnumMap<>(Team.class);
                playerStats.forEach((uuid, stats) ->
                    teamKills.merge(getPlayerTeam(uuid), stats.getKills(), Integer::sum)
                );

                Team topTeam = teamKills.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(Team.NONE);

                broadcaster.broadcast(String.format("&6&lWinning team: %s%s Team", topTeam.color, topTeam.display));
                printTeams();

                break;
            }
        }
    }
}
