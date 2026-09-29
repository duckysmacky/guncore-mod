package io.github.duckysmacky.guncore.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.PlayerStats;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import io.github.duckysmacky.guncore.server.game.GameManager;
import io.github.duckysmacky.guncore.server.menu.MenuManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = GuncoreMod.MOD_ID)
public class ModCommands {
    private static final String[] HELP_LINES = {
        "&6&l--- Guncore Commands ---",
        "&f/menu &7- Opens the equipment/game menu",
        "&f/guncore config_reload &7- Reloads the mod config from disk",
        "&f/guncore help &7- Shows this list",
        "&f/game start|end|reset|pause &7- Controls the current round",
        "&f/game mode <ffa|tdm|hostage> &7- Sets the game mode",
        "&f/game mode_variant <time|lives|kills> &7- Sets the win condition",
        "&f/game kills|lives|deaths <add|remove|set> <player> <amount> &7- Edits player stats",
        "&f/game register_kill <victim> [killer] &7- Registers a kill and a death",
        "&f/game scoreboard &7- Prints the scoreboard",
        "&f/game teams &7- Prints team rosters"
    };

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // mod utilities
        dispatcher.register(Commands.literal("guncore")
            .requires(src -> src.hasPermission(0))
            .then(Commands.literal("config_reload")
                .executes(ctx -> {
                    ConfigManager.instance().load();
                    MenuManager.instance().refreshMenu();
                    ctx.getSource().sendSuccess(() -> Component.literal(TextUtils.translateColorCodes("&a&lGuncore config reloaded")), false);
                    return 1;
                })
            )
            .then(Commands.literal("help")
                .executes(ctx -> {
                    for (String line : HELP_LINES)
                        ctx.getSource().sendSuccess(() -> Component.literal(TextUtils.translateColorCodes(line)), false);
                    return 1;
                })
            )
        );

        // menu
        dispatcher.register(Commands.literal("menu")
            .requires(source -> source.hasPermission(0))
            .executes(ctx ->  {
                if (ctx.getSource().isPlayer()) {
                    ServerPlayer player = ctx.getSource().getPlayer();
                    MenuManager.instance().getMainMenu().open(player);
                }
                return 1;
            })
        );

        // game control
        dispatcher.register(Commands.literal("game")
            .requires(src -> src.hasPermission(2))
            .then(Commands.literal("start")
                .executes(ctx -> {
                    GameManager.instance().startRound();
                    return 1;
                })
            )
            .then(Commands.literal("end")
                .executes(ctx -> {
                    GameManager.instance().endRound();
                    return 1;
                })
            )
            .then(Commands.literal("reset")
                .executes(ctx -> {
                    GameManager.instance().resetRound();
                    return 1;
                })
            )
            .then(Commands.literal("pause")
                .executes(ctx -> {
                    GameManager.instance().toggleRoundPause();
                    return 1;
                })
            )
            .then(Commands.literal("scoreboard")
                .executes(ctx -> {
                    GameManager.instance().printGameScoreboard();
                    return 1;
                })
            )
            .then(Commands.literal("teams")
                .executes(ctx -> {
                    GameManager.instance().printTeams();
                    return 1;
                })
            )
            .then(Commands.literal("mode")
                .then(Commands.argument("mode", StringArgumentType.word())
                    .executes(ctx -> {
                        String arg = StringArgumentType.getString(ctx, "mode");
                        try {
                            GameMode mode = GameMode.valueOf(arg.toUpperCase());
                            GameManager.instance().setGameMode(mode);
                            return 1;
                        } catch (IllegalArgumentException e) {
                            ctx.getSource().sendFailure(Component.literal("Invalid game mode: " + arg));
                            ctx.getSource().sendFailure(Component.literal("Possible values: " + Arrays.toString(GameMode.values())));
                            return 0;
                        }
                    })
                )
            )
            .then(Commands.literal("mode_variant")
                .then(Commands.argument("variant", StringArgumentType.word())
                    .executes(ctx -> {
                        String arg = StringArgumentType.getString(ctx, "variant");
                        try {
                            GameMode.Variant variant = GameMode.Variant.valueOf(arg.toUpperCase());
                            GameManager.instance().setGameModeVariant(variant);
                            return 1;
                        } catch (IllegalArgumentException e) {
                            ctx.getSource().sendFailure(Component.literal("Invalid game mode variant: " + arg));
                            ctx.getSource().sendFailure(Component.literal("Possible values: " + Arrays.toString(GameMode.Variant.values())));
                            return 0;
                        }
                    })
                )
            )
            .then(Commands.literal("register_kill")
                .then(Commands.argument("victim", EntityArgument.player())
                    .executes(ctx -> registerKill(ctx, ctx.getSource().getPlayer()))
                    .then(Commands.argument("killer", EntityArgument.player())
                        .executes(ctx -> registerKill(ctx, EntityArgument.getPlayer(ctx, "killer")))
                    )
                )
            )
            // kills / lives / deaths <add|remove|set> <player> <amount>
            .then(Commands.literal("kills")
                .then(makeStatCommand("kills"))
            )
            .then(Commands.literal("lives")
                .then(makeStatCommand("lives"))
            )
            .then(Commands.literal("deaths")
                .then(makeStatCommand("deaths"))
            )
        );
    }

    private static int registerKill(CommandContext<CommandSourceStack> ctx, ServerPlayer killer) throws CommandSyntaxException {
        if (killer == null) {
            ctx.getSource().sendFailure(Component.literal("Console must specify a killer: /game register_kill <victim> <killer>"));
            return 0;
        }

        ServerPlayer victim = EntityArgument.getPlayer(ctx, "victim");
        GameManager.instance().onPlayerDeath(victim, killer);
        ctx.getSource().sendSuccess(() -> Component.literal(TextUtils.translateColorCodes("&aRegistered")), false);
        return 1;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> makeStatCommand(String type) {
        return Commands.argument("action", StringArgumentType.word())
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("amount", IntegerArgumentType.integer())
                    .executes(ctx -> handleStatCommand(ctx, type))
                )
            );
    }

    private static int handleStatCommand(CommandContext<CommandSourceStack> ctx, String type) throws CommandSyntaxException {
        String action = StringArgumentType.getString(ctx, "action");
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");

        PlayerStats stats = GameManager.instance().getStats(player);
        if (stats == null) {
            ctx.getSource().sendFailure(Component.literal("Player " + player.getScoreboardName() + " has no stats!"));
            return 0;
        }

        switch (type) {
            case "kills" -> modifyStat(stats::getKills, stats::setKills, action, amount);
            case "lives" -> modifyStat(stats::getLives, stats::setLives, action, amount);
            case "deaths" -> modifyStat(stats::getDeaths, stats::setDeaths, action, amount);
        }

        int updatedValue = getStatValue(stats, type);
        String message = TextUtils.translateColorCodes(String.format(
            "&aUpdated %s's %s stat via %s (%d) -> %d",
            player.getScoreboardName(), type, action, amount, updatedValue
        ));
        ctx.getSource().sendSuccess(() -> Component.literal(message), true);
        return 1;
    }

    private static void modifyStat(Supplier<Integer> getter, Consumer<Integer> setter, String action, int amount) {
        switch (action) {
            case "add" -> setter.accept(getter.get() + amount);
            case "remove" -> setter.accept(getter.get() - amount);
            case "set" -> setter.accept(amount);
        }
    }

    private static int getStatValue(PlayerStats stats, String statType) {
        return switch (statType) {
            case "kills" -> stats.getKills();
            case "lives" -> stats.getLives();
            case "deaths" -> stats.getDeaths();
            default -> 0;
        };
    }
}
