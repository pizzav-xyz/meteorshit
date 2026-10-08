package com.example.addon.social;

import com.example.addon.social.alttracker.AltTracker;
import com.example.addon.social.blacklistedpeople.BlacklistedPeople;
import com.example.addon.social.commands.AltCommand;
import com.example.addon.social.commands.BlacklistedPeopleCommand;
import com.example.addon.social.commands.OnlineAltsCommand;
import com.example.addon.social.commands.ScaryPeopleCommand;
import com.example.addon.social.gui.SocialTab;
import com.example.addon.social.modules.SocialColorsModule;
import com.example.addon.social.scarypeople.ScaryPeople;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class MeteorSocialAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Social");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Social Addon");

        Systems.add(new AltTracker());
        Systems.add(new ScaryPeople());
        Systems.add(new BlacklistedPeople());

        Commands.add(new AltCommand());
        Commands.add(new OnlineAltsCommand());
        Commands.add(new ScaryPeopleCommand());
        Commands.add(new BlacklistedPeopleCommand());

        Modules.get().add(new SocialColorsModule());

        Tabs.add(new SocialTab());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon.social";
    }

    @Override
    public String getCommit() {
        return null;
    }
}
