package com.example.addon.social.modules;

import com.example.addon.social.MeteorSocialAddon;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class SocialColorsModule extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<SettingColor> selfColor = sgGeneral.add(new ColorSetting.Builder()
        .name("self-color")
        .description("Color for your own name.")
        .defaultValue(new SettingColor(250, 130, 30))
        .build()
    );

    public final Setting<SettingColor> friendColor = sgGeneral.add(new ColorSetting.Builder()
        .name("friend-color")
        .description("Color for friends.")
        .defaultValue(new SettingColor(0, 255, 180))
        .build()
    );

    public final Setting<SettingColor> scaryColor = sgGeneral.add(new ColorSetting.Builder()
        .name("scary-color")
        .description("Color for scary people.")
        .defaultValue(new SettingColor(100, 15, 175))
        .build()
    );

    public final Setting<SettingColor> blacklistedColor = sgGeneral.add(new ColorSetting.Builder()
        .name("blacklisted-color")
        .description("Color for blacklisted people.")
        .defaultValue(new SettingColor(255, 0, 255))
        .build()
    );

    public final Setting<SettingColor> altColor = sgGeneral.add(new ColorSetting.Builder()
        .name("alt-color")
        .description("Color for tracked alt accounts.")
        .defaultValue(new SettingColor(255, 165, 0))
        .build()
    );

    public SocialColorsModule() {
        super(MeteorSocialAddon.CATEGORY, "social-colors", "Name colors for social lists (self, friends, scary, blacklisted, alts).");
    }
}
