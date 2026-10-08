package com.example.addon.social;

import com.example.addon.social.modules.SocialColorsModule;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

/**
 * Pure static social name-color logic.
 *
 * Priority: self > scary > blacklisted > alt > friend > team > default.
 * Operates on booleans/strings only so it is unit-testable without MC runtime.
 * Live colors are read from {@link SocialColorsModule} when registered,
 * otherwise the defaults below are used.
 */
public final class SocialColorUtils {
    public static final SettingColor DEFAULT_SELF_COLOR = new SettingColor(250, 130, 30);
    public static final SettingColor DEFAULT_FRIEND_COLOR = new SettingColor(0, 255, 180);
    public static final SettingColor DEFAULT_SCARY_COLOR = new SettingColor(100, 15, 175);
    public static final SettingColor DEFAULT_BLACKLISTED_COLOR = new SettingColor(255, 0, 255);
    public static final SettingColor DEFAULT_ALT_COLOR = new SettingColor(255, 165, 0);

    private SocialColorUtils() {}

    public enum Status {
        Self, Scary, Blacklisted, Alt, Friend, Team, Player
    }

    public static Status resolveStatus(boolean isSelf, boolean isScary, boolean isBlacklisted, boolean isAlt, boolean isFriend, boolean hasTeamColor) {
        if (isSelf) return Status.Self;
        if (isScary) return Status.Scary;
        if (isBlacklisted) return Status.Blacklisted;
        if (isAlt) return Status.Alt;
        if (isFriend) return Status.Friend;
        if (hasTeamColor) return Status.Team;
        return Status.Player;
    }

    public static String statusName(boolean isSelf, boolean isScary, boolean isBlacklisted, boolean isAlt, boolean isFriend) {
        return switch (resolveStatus(isSelf, isScary, isBlacklisted, isAlt, isFriend, false)) {
            case Self -> "Self";
            case Scary -> "Scary";
            case Blacklisted -> "Blacklisted";
            case Alt -> "Alt";
            case Friend -> "Friend";
            default -> "Player";
        };
    }

    public static boolean hasSpecialStatus(boolean isSelf, boolean isScary, boolean isBlacklisted, boolean isAlt, boolean isFriend) {
        return resolveStatus(isSelf, isScary, isBlacklisted, isAlt, isFriend, false) != Status.Player;
    }

    /** Returns a copy of the configured color for the given status, or null for Team/Player. */
    public static Color colorFor(Status status) {
        SocialColorsModule module = Modules.get() != null ? Modules.get().get(SocialColorsModule.class) : null;
        return switch (status) {
            case Self -> copy(module != null ? module.selfColor.get() : DEFAULT_SELF_COLOR);
            case Scary -> copy(module != null ? module.scaryColor.get() : DEFAULT_SCARY_COLOR);
            case Blacklisted -> copy(module != null ? module.blacklistedColor.get() : DEFAULT_BLACKLISTED_COLOR);
            case Alt -> copy(module != null ? module.altColor.get() : DEFAULT_ALT_COLOR);
            case Friend -> copy(module != null ? module.friendColor.get() : DEFAULT_FRIEND_COLOR);
            default -> null;
        };
    }

    public static Color getNameColor(boolean isSelf, boolean isScary, boolean isBlacklisted, boolean isAlt, boolean isFriend, boolean hasTeamColor, Color teamColor, Color defaultColor) {
        Status status = resolveStatus(isSelf, isScary, isBlacklisted, isAlt, isFriend, hasTeamColor);
        Color configured = colorFor(status);
        if (configured != null) {
            configured.a(defaultColor.a);
            return configured;
        }
        if (status == Status.Team && teamColor != null) {
            Color c = new Color(teamColor.r, teamColor.g, teamColor.b, defaultColor.a);
            return c;
        }
        return defaultColor;
    }

    private static Color copy(Color color) {
        return new Color(color.r, color.g, color.b, color.a);
    }
}
