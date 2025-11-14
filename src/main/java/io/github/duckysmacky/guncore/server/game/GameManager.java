package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.GameConfig;
import io.github.duckysmacky.guncore.common.game.*;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.SyncGameInfoPacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.GameType;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameManager {
    private static final String ID = "GameManager";
    private static final int[] announcementIntervalsSecs = new int[]{1200, 900, 600, 300, 180, 120, 60, 30, 10, 5, 4, 3, 2, 1};
    private static GameManager instance;
    private final Map<UUID, PlayerStats> playerStats;
    private final Map<Team, List<UUID>> teams;
    private GameMode gameMode;
    private GameMode.Variant gameModeVariant;
    private GameState state;
    private int roundDurationSec;

    private GameManager() {
        this.playerStats = new HashMap<>();
        this.teams = new EnumMap<>(Team.class);
        Arrays.stream(Team.values()).forEach(t -> teams.put(t, new ArrayList<>()));
        this.gameMode = GameMode.FFA;
        this.gameModeVariant = GameMode.Variant.LIVES;
        this.state = GameState.NOT_STARTED;
    }

    public static GameManager instance() {
        if (instance == null) {
            instance = new GameManager();
            instance.updatePlayerList();
        }

        return instance;
    }

    public static void setupWorldSettings() {
        String[] commandChain = new String[]{
            "team add none",
            "team add blue",
            "team add red",
            "team add yellow",
            "team add green",
            "team add purple",
            "team modify none nametagVisibility never",
            "team modify none seeFriendlyInvisibles false",
            "team modify blue nametagVisibility hideForOtherTeams",
            "team modify red nametagVisibility hideForOtherTeams",
            "team modify yellow nametagVisibility hideForOtherTeams",
            "team modify green nametagVisibility hideForOtherTeams",
            "team modify purple nametagVisibility hideForOtherTeams",
            "gamerule doWeatherCycle false",
            "gamerule doDaylightCycle false",
            "gamerule doFireTick false",
            "gamerule doTileDrops false",
            "gamerule keepInventory true",
            "gamerule doMobSpawning false",
            "gamerule spawnRadius 0",
            "gamerule naturalRegeneration true"
        };

        Arrays.stream(commandChain).forEach(CommandExecutor::execute);
        ServerBroadcaster.message("&a&lWorld setup complete");
        ServerSoundPlayer.playForAll(SoundEvents.NOTE_BLOCK_HARP.get(), 1f, 1f);
    }

    private void updatePlayerList() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) {
            GuncoreMod.LOGGER.error("[{}] Cannot update player list: server is null!", ID);
            return;
        }

        GuncoreMod.LOGGER.info("[{}] {}", ID, "Updating player list from server");
        server.getPlayerList().getPlayers().forEach(
            p -> playerStats.computeIfAbsent(p.getUUID(), k -> {
                switchTeamTo(p.getUUID(), Team.NONE);
                String command = String.format("team join none %s", p.getScoreboardName());
                CommandExecutor.execute(command);
                return new PlayerStats(p.getScoreboardName());
            })
        );
    }

    public void startRound() {
        if (state != GameState.RUNNING && state != GameState.PAUSED) {
            state = GameState.RUNNING;
            roundDurationSec = 0;

            playerStats.values().forEach(p -> {
                p.resetStats();

                if (gameModeVariant == GameMode.Variant.LIVES)
                    p.setLives(getStartingLives());
            });

            if (gameMode != GameMode.FFA) {
                teams.get(Team.NONE).forEach(
                    p -> ServerBroadcaster.warning(String.format("&7Player &f&l%s &7didn't join any team!", playerStats.get(p).getUsername()))
                );
            }

            ServerBroadcaster.message("&a&lRound started");
            ServerSoundPlayer.playForAll(SoundEvents.END_PORTAL_SPAWN, 1f, 1f);
        } else {
            ServerBroadcaster.error("&7There is already a round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.VILLAGER_NO, 1f, 1f);
        }
    }

    public void toggleRoundPause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            ServerBroadcaster.message("&e&lRound paused");
            ServerSoundPlayer.playForAll(SoundEvents.NOTE_BLOCK_BASS.get(), 1f, 1f);
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            ServerBroadcaster.message("&e&lRound continued");
            ServerSoundPlayer.playForAll(SoundEvents.NOTE_BLOCK_BASS.get(), 1f, 1f);
        } else {
            ServerBroadcaster.error("&7There is no round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.VILLAGER_NO, 1f, 1f);
        }
    }

    public void resetRound() {
        state = GameState.NOT_STARTED;
        roundDurationSec = 0;
        playerStats.values().forEach(PlayerStats::resetStats);
        ServerBroadcaster.message("&c&lRound reset");
        ServerSoundPlayer.playForAll(SoundEvents.NOTE_BLOCK_BASS.get(), 1f, 1f);
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
            ServerSoundPlayer.playForAll(SoundEvents.VILLAGER_NO, 1f, 1f);
        }
    }

    public void tick(int tickCount) {
        // every second
        if (state == GameState.RUNNING & tickCount % 20 == 0) {
            roundDurationSec++;

            if (gameModeVariant == GameMode.Variant.TIME) {
                int roundTimeLeftSecs = getRoundLengthSec() - roundDurationSec;

                if (Arrays.stream(announcementIntervalsSecs).anyMatch(s -> s == roundTimeLeftSecs))
                    printRoundTimeLeft(roundTimeLeftSecs);

                if (roundTimeLeftSecs <= 0)
                    endRound();
            }
        }

        // every 0.5 second
        if (tickCount % 10 == 0) {
            SyncGameInfoPacket packet = new SyncGameInfoPacket(gameMode, gameModeVariant, state, roundDurationSec, playerStats);
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
        }
    }

    public void onPlayerDeath(ServerPlayer victim, ServerPlayer killer) {
        if (state != GameState.RUNNING) return;

        PlayerStats victimStats = getStats(victim);
        victimStats.setDeaths(victimStats.getDeaths() + 1);

        if (killer == null) return;

        victimStats.setLives(victimStats.getLives() - 1);

        PlayerStats killerStats = getStats(killer);
        killerStats.setKills(killerStats.getKills() + 1);
        ServerSoundPlayer.playFor(killer, SoundEvents.EXPERIENCE_ORB_PICKUP, 1.5f, 1f);

        if (gameModeVariant == GameMode.Variant.LIVES) {
            if (victimStats.getLives() <= 0) {
                victim.setGameMode(GameType.SPECTATOR);
                ServerBroadcaster.message(String.format("&f&l%s &c&lis out of lives!", victim.getName()));
                ServerSoundPlayer.playForAll(SoundEvents.ENDER_DRAGON_GROWL, 1f, 1f);
            } else if (victimStats.getLives() == 1) {
                String message = TextUtils.translateColorCodes("&c&lYou only have &f&l1 &e&llife left");
                victim.sendSystemMessage(Component.literal(message));
                ServerSoundPlayer.playFor(victim, SoundEvents.GLASS_BREAK, 1f, 1f);
            } else {
                String message = TextUtils.translateColorCodes(String.format("&e&lYou have &f&l%s &e&llives left", victimStats.getLives()));
                victim.sendSystemMessage(Component.literal(message));
                ServerSoundPlayer.playFor(victim, SoundEvents.GLASS_BREAK, 1f, 1f);
            }
        }

        checkRoundEndConditions();
    }

    public void printRoundTimeLeft(int timeLeftSecs) {
        ServerBroadcaster.message(String.format("&e&lTime Left: &f%s", TextUtils.formatTime(timeLeftSecs)));
        ServerSoundPlayer.playForAll(SoundEvents.NOTE_BLOCK_HAT.get(), 1f, 1f);
    }

    public void printGameScoreboard() {
        updatePlayerList();

        ServerBroadcaster.message("&f&l--- Scoreboard ---");

        AtomicInteger place = new AtomicInteger(1);
        playerStats.values().stream()
            .sorted(Comparator
                .comparingInt(PlayerStats::getKills)
                .thenComparing(PlayerStats::getDeaths, Comparator.reverseOrder())
                .thenComparing(PlayerStats::getLives)
            )
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
                .sorted(Comparator.comparingInt(u -> playerStats.get(u).getKills()))
                .forEach(uuid -> {
                PlayerStats p = playerStats.get(uuid);

                if (p != null)
                    ServerBroadcaster.message(String.format(" - &f%s: &7(%s kills, %s deaths, %s lives)",
                        p.getUsername(), p.getKills(), p.getDeaths(), p.getLives()));
            });
        });
    }

    public void joinTeam(ServerPlayer player, Team team) {
        if (gameMode == GameMode.FFA)
            setGameMode(GameMode.TDM);

        switchTeamTo(player.getUUID(), team);
        String command = String.format("team join %s %s", team.display.toLowerCase(), player.getScoreboardName());
        CommandExecutor.execute(command);

        ServerBroadcaster.message(String.format(
            "&7Player &f%s &7joined the %s%s Team",
            player.getName(), team.color, team.display
        ));
        ServerSoundPlayer.playForAll(SoundEvents.NOTE_BLOCK_HARP.get(), 1f, 1f);
    }

    public void setGameMode(GameMode gameMode) {
        if (state != GameState.RUNNING && state != GameState.PAUSED) {
            if (gameMode == GameMode.FFA) {
                playerStats.keySet().forEach(uuid -> switchTeamTo(uuid, Team.NONE));
                CommandExecutor.execute("team join none @a");
            }

            this.gameMode = gameMode;
            ServerBroadcaster.message("&fGame mode set to &l" + gameMode.display);
            ServerSoundPlayer.playForAll(SoundEvents.NOTE_BLOCK_HARP.get(), 1f, 1f);
        } else {
            ServerBroadcaster.error("&7There is already a round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.VILLAGER_NO, 1f, 1f);
        }
    }

    public void setGameModeVariant(GameMode.Variant gameModeVariant) {
        if (state != GameState.RUNNING && state != GameState.PAUSED) {
            this.gameModeVariant = gameModeVariant;
            ServerBroadcaster.message("&fGame mode variant set to &l" + gameModeVariant.display);
            ServerSoundPlayer.playForAll(SoundEvents.NOTE_BLOCK_HARP.get(), 1f, 1f);
        } else {
            ServerBroadcaster.error("&7There is already a round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.VILLAGER_NO, 1f, 1f);
        }
    }

    public PlayerStats getStats(ServerPlayer player) {
        UUID uuid = player.getUUID();
        playerStats.computeIfAbsent(uuid, k -> new PlayerStats(player.getScoreboardName()));
        return playerStats.get(uuid);
    }

    private int getRoundLengthSec() {
        GameConfig gameConfig = ConfigManager.instance().getGameConfig();

        return switch (gameMode) {
            case FFA -> gameConfig.ffaConfig().roundLengthSec();
            case TDM -> gameConfig.tdmConfig().roundLengthSec();
            case HOSTAGE -> gameConfig.hostageConfig().roundLengthSec();
        };
    }

    private int getStartingLives() {
        GameConfig gameConfig = ConfigManager.instance().getGameConfig();

        return switch (gameMode) {
            case FFA -> gameConfig.ffaConfig().startingLives();
            case TDM -> gameConfig.tdmConfig().startingLives();
            case HOSTAGE -> gameConfig.hostageConfig().startingLives();
        };
    }

    private int getKillTarget() {
        GameConfig gameConfig = ConfigManager.instance().getGameConfig();

        return switch (gameMode) {
            case FFA -> gameConfig.ffaConfig().killTarget();
            case TDM -> gameConfig.tdmConfig().killTarget();
            case HOSTAGE -> gameConfig.hostageConfig().killTarget();
        };
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameMode.Variant getGameModeVariant() {
        return gameModeVariant;
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
        if (gameModeVariant == GameMode.Variant.LIVES) {
            if (gameMode == GameMode.FFA) {
                long playersAlive = playerStats.values().stream()
                    .filter(p -> p.getLives() > 0)
                    .count();

                if (playersAlive <= 1)
                    endRound();
            } else {
                Set<Team> aliveTeams = playerStats.entrySet().stream()
                    .filter(e -> e.getValue().getLives() > 0)
                    .map(Map.Entry::getKey)
                    .map(this::getPlayerTeam)
                    .collect(Collectors.toSet());

                if (aliveTeams.size() <= 1)
                    endRound();
            }
        } else if (gameModeVariant == GameMode.Variant.KILLS) {
            if (gameMode == GameMode.FFA) {
                if (playerStats.values().stream().anyMatch(p -> p.getKills() >= getKillTarget()))
                    endRound();
            } else {
                Map<Team, Integer> teamKills = teams.entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                            .mapToInt(uuid -> playerStats.get(uuid).getKills())
                            .sum()
                    ));

                if (teamKills.values().stream().anyMatch(k -> k >= getKillTarget()))
                    endRound();
            }
        }
    }

    private void determineWinner() {
        if (gameMode == GameMode.FFA) {
            PlayerStats topPlayer = playerStats.values().stream()
                .max(Comparator
                    .comparingInt(PlayerStats::getKills)
                    .thenComparing(PlayerStats::getDeaths, Comparator.reverseOrder())
                    .thenComparing(PlayerStats::getLives)
                )
                .orElse(null);

            if (topPlayer != null) {
                ServerBroadcaster.message(String.format("&6&lWinner: &f%s &7(%s kills)", topPlayer.getUsername(), topPlayer.getKills()));
                printGameScoreboard();
            } else {
                ServerBroadcaster.message("&c&lNobody won lmao");
                ServerBroadcaster.message("&7yall suck");
            }
        } else {
            Map<Team, Integer> teamKills = teams.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                        .mapToInt(uuid -> playerStats.get(uuid).getKills())
                        .sum()
                ));

            Team topTeam = teamKills.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Team.NONE);

            ServerBroadcaster.message(String.format("&6&lWinning team: %s%s Team", topTeam.color, topTeam.display));
            printTeams();
        }
    }
}