package io.github.duckysmacky.guncore.server.game;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.GameConfig;
import io.github.duckysmacky.guncore.common.game.*;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.UpdatePlayerListPacket;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
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
    private GameMode gameMode;
    private GameMode.Variant gameModeVariant;
    private GameState state;
    private long roundStartTime;

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
            "scoreboard teams add none",
            "scoreboard teams add blue",
            "scoreboard teams add red",
            "scoreboard teams add yellow",
            "scoreboard teams add green",
            "scoreboard teams add purple",
            "scoreboard teams option none nametagVisibility never",
            "scoreboard teams option none seeFriendlyInvisibles false",
            "scoreboard teams option blue nametagVisibility hideForOtherTeams",
            "scoreboard teams option red nametagVisibility hideForOtherTeams",
            "scoreboard teams option yellow nametagVisibility hideForOtherTeams",
            "scoreboard teams option green nametagVisibility hideForOtherTeams",
            "scoreboard teams option purple nametagVisibility hideForOtherTeams",
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
        ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
    }


    private void updatePlayerList() {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            updatePlayerListAsServer(server);
        } else {
            GuncoreMod.LOGGER.info("[{}] Requesting a player list update from server", ID);
            PacketHandler.instance().sendToServer(new UpdatePlayerListPacket());
        }
    }

    public void updatePlayerListAsServer(MinecraftServer server) {
        if (server == null) {
            GuncoreMod.LOGGER.error("[{}] Cannot update player list: server is null!", ID);
            return;
        }

        System.out.println(server.getPlayerList().getPlayers().stream().map(EntityPlayer::getName));

        GuncoreMod.LOGGER.info("[{}] {}", ID, "Updating player list from server");
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
            state = GameState.RUNNING;
            roundStartTime = System.currentTimeMillis();

            playerStats.values().forEach(p -> {
                p.resetStats();

                if (gameModeVariant == GameMode.Variant.LIVES)
                    p.setLives(getStartingLives());
            });

            if (gameMode != GameMode.FFA) {
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
        playerStats.values().forEach(PlayerStats::resetStats);
        ServerBroadcaster.message("&c&lRound reset");
        ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_BASS, 1f, 1f);
    }

    public void endRound() {
        if (state == GameState.RUNNING || state == GameState.PAUSED) {
            state = GameState.ENDED;
            ServerBroadcaster.message("&a&lRound ended");
            ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_BASS, 1f, 1f);
            determineWinner();
        } else {
            ServerBroadcaster.error("&7There is no round in progress!");
            ServerSoundPlayer.playForAll(SoundEvents.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

    public void tick(boolean isSecondTick) {
        if (state != GameState.RUNNING) return;

        long roundDurationSec = (System.currentTimeMillis() - roundStartTime) / 1000;

        if (gameModeVariant != GameMode.Variant.TIME) return;

        if (isSecondTick) {
            long roundTimeLeftSecs = getRoundLengthSec() - roundDurationSec;

            if (Arrays.stream(announcementIntervalsSecs).anyMatch(s -> s == roundTimeLeftSecs))
                printRoundTimeLeft(roundTimeLeftSecs);

            if (roundTimeLeftSecs <= 0)
                endRound();
        }
    }

    public void onPlayerKill(EntityPlayer killer, EntityPlayer victim) {
        if (state != GameState.RUNNING) return;

        PlayerStats killerStats = getStats(killer);
        killerStats.setKills(killerStats.getKills() + 1);

        PlayerStats victimStats = getStats(victim);
        victimStats.registerDeath();

        killer.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5f, 1f);

        if (gameModeVariant == GameMode.Variant.LIVES) {
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

        checkRoundEndConditions();
    }

    public void printRoundTimeLeft(long timeLeftSecs) {
        String time = TextUtils.formatTime((int) timeLeftSecs);
        ServerBroadcaster.message(String.format("&e&lTime Left: &f%s", time));
        ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_HAT, 1f, 1f);
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

    public void joinTeam(EntityPlayer player, Team team) {
        if (gameMode == GameMode.FFA)
            setGameMode(GameMode.TDM);

        switchTeamTo(player.getUniqueID(), team);
        String command = String.format("scoreboard teams join %s %s", team.display.toLowerCase(), player.getName());
        CommandExecutor.execute(command);

        ServerBroadcaster.message(String.format(
            "&7Player &f%s &7joined the %s%s Team",
            player.getName(), team.color, team.display
        ));
        ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
    }

    public void setGameMode(GameMode gameMode) {
        if (gameMode == GameMode.FFA) {
            playerStats.keySet().forEach(uuid -> switchTeamTo(uuid, Team.NONE));
            CommandExecutor.execute("scoreboard teams join none @a");
        }

        this.gameMode = gameMode;
        ServerBroadcaster.message("&fGame mode set to &l" + gameMode.display);
        ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
    }

    public void setGameModeVariant(GameMode.Variant gameModeVariant) {
        this.gameModeVariant = gameModeVariant;
        ServerBroadcaster.message("&fGame mode variant set to &l" + gameModeVariant.display);
        ServerSoundPlayer.playForAll(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
    }

    public PlayerStats getStats(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        playerStats.computeIfAbsent(uuid, k -> new PlayerStats(player.getName()));
        return playerStats.get(uuid);
    }

    private int getStartingLives() {
        GameConfig gameConfig = ConfigManager.instance().getCachedGameConfig();

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

    private int getRoundLengthSec() {
        GameConfig gameConfig = ConfigManager.instance().getCachedGameConfig();

        switch (gameMode) {
            case FFA:
                return gameConfig.ffaConfig.roundLengthSec;
            case TDM:
                return gameConfig.tdmConfig.roundLengthSec;
            case HOSTAGE:
                return gameConfig.hostageConfig.roundLengthSec;
            default:
                return 600;
        }
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameMode.Variant getGameModeVariant() {
        return gameModeVariant;
    }

    public long getRoundStartTime() {
        return roundStartTime;
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

                ServerBroadcaster.message(String.format("&6&lWinning team: %s%s Team", topTeam.color, topTeam.display));
                printTeams();

                break;
            }
        }
    }
}