package com.example.addon.social.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import com.example.addon.social.alttracker.AltAccount;
import com.example.addon.social.alttracker.AltTracker;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OnlineAltsCommand extends Command {
    public OnlineAltsCommand() {
        super("online-alts", "Counts how many online players are tracked as alt accounts.");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(context -> {
            if (mc.getConnection() == null) {
                error("You must be connected to a server to run this command.");
                return 1;
            }

            AltTracker altTracker = AltTracker.get();
            int onlineAltCount = 0;

            Set<String> uniqueMainAccountsWithOnlineAlt = new HashSet<>();

            List<PlayerInfo> playerList = new ArrayList<>(mc.getConnection().getOnlinePlayers());
            int totalPlayers = playerList.size();

            for (PlayerInfo entry : playerList) {
                String playerName = entry.getProfile().name();

                if (mc.player != null && mc.player.getName().getString().equals(playerName)) {
                    continue;
                }

                AltAccount group = altTracker.getGroupByPlayer(playerName);

                if (group != null) {
                    if (group.altAccounts.contains(playerName)) {
                        onlineAltCount++;
                        uniqueMainAccountsWithOnlineAlt.add(group.mainAccount);
                    }
                }
            }

            if (onlineAltCount == 0) {
                info("(highlight)0/%d(default) players online are tracked as alt accounts.", totalPlayers);
            } else {
                info("Found (highlight)%d/%d(default) players online are tracked as alt accounts.", onlineAltCount, totalPlayers);

                if (!uniqueMainAccountsWithOnlineAlt.isEmpty()) {
                    info("These belong to (highlight)%d unique main account(s)(default): %s",
                        uniqueMainAccountsWithOnlineAlt.size(),
                        String.join(", ", uniqueMainAccountsWithOnlineAlt));
                }
            }

            return SINGLE_SUCCESS;
        });
    }
}