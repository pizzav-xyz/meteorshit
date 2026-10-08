package com.example.addon.social.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.example.addon.social.blacklistedpeople.BlacklistedPeople;
import com.example.addon.social.blacklistedpeople.BlacklistedPerson;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerInfo;

public class BlacklistedPeopleCommand extends Command {
    public BlacklistedPeopleCommand() {
        super("blacklistedpeople", "Manages blacklisted people.", "blacklist", "bl");
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

    private static final SuggestionProvider<ClientSuggestionProvider> BLACKLISTED_PLAYERS = (context, builder) -> {
        String remaining = builder.getRemaining().toLowerCase();
        for (BlacklistedPerson person : BlacklistedPeople.get()) {
            String name = person.getName();
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
                    BlacklistedPerson blacklistedPerson = new BlacklistedPerson(name);
                    if (BlacklistedPeople.get().add(blacklistedPerson)) {
                        ChatUtils.sendMsg(name.hashCode(), ChatFormatting.RED, "Added (highlight)%s (default)to blacklisted people.".formatted(name));
                    } else error("Already blacklisted.");
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("remove")
            .then(argument("player", StringArgumentType.word())
                .suggests(BLACKLISTED_PLAYERS)
                .executes(context -> {
                    String name = StringArgumentType.getString(context, "player");
                    BlacklistedPerson blacklistedPerson = BlacklistedPeople.get().get(name);
                    if (blacklistedPerson != null && BlacklistedPeople.get().remove(blacklistedPerson)) {
                        ChatUtils.sendMsg(name.hashCode(), ChatFormatting.RED, "Removed (highlight)%s (default)from blacklisted people.".formatted(name));
                    } else error("Not blacklisted.");
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("list").executes(context -> {
            info("--- Blacklisted People ((highlight)%s(default)) ---", BlacklistedPeople.get().count());
            BlacklistedPeople.get().forEach(blacklistedPerson -> ChatUtils.info("(highlight)%s".formatted(blacklistedPerson.getName())));
            return SINGLE_SUCCESS;
        }));
    }
}
