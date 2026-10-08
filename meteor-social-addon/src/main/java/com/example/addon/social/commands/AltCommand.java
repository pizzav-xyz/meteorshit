package com.example.addon.social.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import com.example.addon.social.alttracker.AltAccount;
import com.example.addon.social.alttracker.AltTracker;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerInfo;

public class AltCommand extends Command {
    public AltCommand() {
        super("alt", "Manages alt accounts tracking. Available commands: link, unlink, list");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.then(literal("link")
            .then(argument("main", StringArgumentType.string())
                .suggests((context, suggestionsBuilder) -> {
                    if (mc.player != null && mc.level != null && mc.getConnection() != null) {
                        String remaining = suggestionsBuilder.getRemaining().toLowerCase();
                        for (PlayerInfo entry : mc.getConnection().getOnlinePlayers()) {
                            String name = entry.getProfile().name();
                            if (name.toLowerCase().startsWith(remaining)) {
                                suggestionsBuilder.suggest(name);
                            }
                        }
                    }
                    return suggestionsBuilder.buildFuture();
                })
                .then(argument("alt", StringArgumentType.string())
                    .suggests((context, suggestionsBuilder) -> {
                        if (mc.player != null && mc.level != null && mc.getConnection() != null) {
                            String remaining = suggestionsBuilder.getRemaining().toLowerCase();
                            for (PlayerInfo entry : mc.getConnection().getOnlinePlayers()) {
                                String name = entry.getProfile().name();
                                if (name.toLowerCase().startsWith(remaining)) {
                                    suggestionsBuilder.suggest(name);
                                }
                            }
                        }
                        return suggestionsBuilder.buildFuture();
                    })
                    .executes(context -> {
                        String player1 = StringArgumentType.getString(context, "main");
                        String player2 = StringArgumentType.getString(context, "alt");

                        var tracker = AltTracker.get();
                        var group1 = tracker.getGroupByPlayer(player1);
                        var group2 = tracker.getGroupByPlayer(player2);

                        if (group1 != null && group2 != null && group1 == group2) {
                            warning("Players (highlight)%s (default)and (highlight)%s (default)are already linked.", player1, player2);
                            return SINGLE_SUCCESS;
                        }

                        if (group1 == null && group2 == null) {
                            if (tracker.linkAccounts(player1, player2)) {
                                info("(highlight)%s (default)has been linked as an alt of (highlight)%s", player2, player1);
                            }
                        } else if (group1 == null) {
                            if (tracker.linkAccounts(player1, player2)) {
                                info("(highlight)%s (default)has been linked as an alt of (highlight)%s", player1, group2.mainAccount);
                            }
                        } else if (group2 == null) {
                            if (tracker.linkAccounts(player1, player2)) {
                                info("(highlight)%s (default)has been linked as an alt of (highlight)%s", player2, group1.mainAccount);
                            }
                        } else {
                            if (tracker.linkAccounts(player1, player2)) {
                                info("Alt groups of (highlight)%s (default)and (highlight)%s (default)have been merged", group1.mainAccount, group2.mainAccount);
                            }
                        }

                        return SINGLE_SUCCESS;
                    })
                )
            )
        );

        builder.then(literal("unlink")
            .then(argument("player", StringArgumentType.string())
                .suggests((context, suggestionsBuilder) -> {
                    String remaining = suggestionsBuilder.getRemaining().toLowerCase();
                    for (AltAccount group : AltTracker.get()) {
                        String main = group.getMainAccount();
                        if (main.toLowerCase().startsWith(remaining)) suggestionsBuilder.suggest(main);
                        for (String alt : group.getAltAccounts()) {
                            if (alt.toLowerCase().startsWith(remaining)) suggestionsBuilder.suggest(alt);
                        }
                    }
                    return suggestionsBuilder.buildFuture();
                })
                .executes(context -> {
                    String player = StringArgumentType.getString(context, "player");

                    if (AltTracker.get().unlinkAccount(player)) {
                        info("Unlinked (highlight)%s (default)from its alt group", player);
                    } else {
                        error("Player (highlight)%s (default)is not being tracked", player);
                    }

                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("list")
            .executes(context -> {
                if (AltTracker.get().isEmpty()) {
                    info("No alt accounts are being tracked.");
                    return SINGLE_SUCCESS;
                }

                info("--- Alt Groups ((highlight)%d (default)groups, (highlight)%d (default)total players) ---",
                    AltTracker.get().count(), AltTracker.get().getTotalTrackedPlayers());

                for (AltAccount group : AltTracker.get()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("(highlight)").append(group.getMainAccount()).append("(default)");

                    if (!group.getAltAccounts().isEmpty()) {
                        sb.append(" -> ");
                        sb.append(String.join(", ", group.getAltAccounts()));
                    }

                    ChatUtils.info(sb.toString());
                }

                return SINGLE_SUCCESS;
            })
        );
    }
}