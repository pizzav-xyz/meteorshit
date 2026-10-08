package com.example.addon.social.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import meteordevelopment.meteorclient.commands.Command;
import com.example.addon.social.scarypeople.ScaryPeople;
import com.example.addon.social.scarypeople.ScaryPerson;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerInfo;

public class ScaryPeopleCommand extends Command {
    public ScaryPeopleCommand() {
        super("scarypeople", "Manages scary people.");
    }

    private static final SuggestionProvider<ClientSuggestionProvider> ONLINE_PLAYERS = (context, builder) -> {
        if (mc.getConnection() != null) {
            String remaining = builder.getRemaining().toLowerCase();
            for (PlayerInfo entry : mc.getConnection().getOnlinePlayers()) {
                String name = entry.getProfile().name();
                if (name.toLowerCase().startsWith(remaining)) {
                    builder.suggest(name);
                }
            }
        }
        return builder.buildFuture();
    };

    private static final SuggestionProvider<ClientSuggestionProvider> SCARY_PLAYERS = (context, builder) -> {
        String remaining = builder.getRemaining().toLowerCase();
        for (ScaryPerson scaryPerson : ScaryPeople.get()) {
            String name = scaryPerson.getName();
            if (name.toLowerCase().startsWith(remaining)) {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    };

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.then(literal("add")
            .then(argument("player", StringArgumentType.word())
                .suggests(ONLINE_PLAYERS)
                .executes(context -> {
                    String name = StringArgumentType.getString(context, "player");
                    ScaryPerson scaryPerson = new ScaryPerson(name);
                    if (ScaryPeople.get().add(scaryPerson)) {
                        ChatUtils.sendMsg(name.hashCode(), ChatFormatting.RED, "Added (highlight)%s (default)to scary people.".formatted(name));
                    } else error("Already marked as scary.");
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("remove")
            .then(argument("player", StringArgumentType.word())
                .suggests(SCARY_PLAYERS)
                .executes(context -> {
                    String name = StringArgumentType.getString(context, "player");
                    ScaryPerson scaryPerson = ScaryPeople.get().get(name);
                    if (scaryPerson != null && ScaryPeople.get().remove(scaryPerson)) {
                        ChatUtils.sendMsg(name.hashCode(), ChatFormatting.RED, "Removed (highlight)%s (default)from scary people.".formatted(name));
                    } else error("Not marked as scary.");
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("list").executes(context -> {
            info("--- Scary People ((highlight)%s(default)) ---", ScaryPeople.get().count());
            ScaryPeople.get().forEach(scaryPerson -> ChatUtils.info("(highlight)%s".formatted(scaryPerson.getName())));
            return SINGLE_SUCCESS;
        }));
    }
}