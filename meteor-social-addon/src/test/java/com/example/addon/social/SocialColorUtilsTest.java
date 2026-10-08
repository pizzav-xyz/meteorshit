package com.example.addon.social;

import meteordevelopment.meteorclient.utils.render.color.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SocialColorUtilsTest {
    @Test
    void resolveStatusDefaultsToPlayer() {
        assertEquals(SocialColorUtils.Status.Player,
            SocialColorUtils.resolveStatus(false, false, false, false, false, false));
    }

    @Test
    void resolveStatusPriority() {
        assertEquals(SocialColorUtils.Status.Self,
            SocialColorUtils.resolveStatus(true, true, true, true, true, true));
        assertEquals(SocialColorUtils.Status.Scary,
            SocialColorUtils.resolveStatus(false, true, true, true, true, true));
        assertEquals(SocialColorUtils.Status.Blacklisted,
            SocialColorUtils.resolveStatus(false, false, true, true, true, true));
        assertEquals(SocialColorUtils.Status.Alt,
            SocialColorUtils.resolveStatus(false, false, false, true, true, true));
        assertEquals(SocialColorUtils.Status.Friend,
            SocialColorUtils.resolveStatus(false, false, false, false, true, true));
        assertEquals(SocialColorUtils.Status.Team,
            SocialColorUtils.resolveStatus(false, false, false, false, false, true));
    }

    @Test
    void resolveStatusSingleFlags() {
        assertEquals(SocialColorUtils.Status.Self, SocialColorUtils.resolveStatus(true, false, false, false, false, false));
        assertEquals(SocialColorUtils.Status.Scary, SocialColorUtils.resolveStatus(false, true, false, false, false, false));
        assertEquals(SocialColorUtils.Status.Blacklisted, SocialColorUtils.resolveStatus(false, false, true, false, false, false));
        assertEquals(SocialColorUtils.Status.Alt, SocialColorUtils.resolveStatus(false, false, false, true, false, false));
        assertEquals(SocialColorUtils.Status.Friend, SocialColorUtils.resolveStatus(false, false, false, false, true, false));
    }

    @Test
    void statusNameMapping() {
        assertEquals("Self", SocialColorUtils.statusName(true, false, false, false, false));
        assertEquals("Scary", SocialColorUtils.statusName(false, true, false, false, false));
        assertEquals("Blacklisted", SocialColorUtils.statusName(false, false, true, false, false));
        assertEquals("Alt", SocialColorUtils.statusName(false, false, false, true, false));
        assertEquals("Friend", SocialColorUtils.statusName(false, false, false, false, true));
        assertEquals("Player", SocialColorUtils.statusName(false, false, false, false, false));
    }

    @Test
    void statusNamePriority() {
        assertEquals("Self", SocialColorUtils.statusName(true, true, true, true, true));
        assertEquals("Scary", SocialColorUtils.statusName(false, true, true, true, true));
        assertEquals("Blacklisted", SocialColorUtils.statusName(false, false, true, true, true));
        assertEquals("Alt", SocialColorUtils.statusName(false, false, false, true, true));
    }

    @Test
    void hasSpecialStatus() {
        assertTrue(SocialColorUtils.hasSpecialStatus(true, false, false, false, false));
        assertTrue(SocialColorUtils.hasSpecialStatus(false, true, false, false, false));
        assertTrue(SocialColorUtils.hasSpecialStatus(false, false, true, false, false));
        assertTrue(SocialColorUtils.hasSpecialStatus(false, false, false, true, false));
        assertTrue(SocialColorUtils.hasSpecialStatus(false, false, false, false, true));
        assertFalse(SocialColorUtils.hasSpecialStatus(false, false, false, false, false));
    }

    @Test
    void defaultColorsHaveExpectedRgb() {
        assertEquals(250, SocialColorUtils.DEFAULT_SELF_COLOR.r);
        assertEquals(130, SocialColorUtils.DEFAULT_SELF_COLOR.g);
        assertEquals(30, SocialColorUtils.DEFAULT_SELF_COLOR.b);

        assertEquals(0, SocialColorUtils.DEFAULT_FRIEND_COLOR.r);
        assertEquals(255, SocialColorUtils.DEFAULT_FRIEND_COLOR.g);
        assertEquals(180, SocialColorUtils.DEFAULT_FRIEND_COLOR.b);

        assertEquals(100, SocialColorUtils.DEFAULT_SCARY_COLOR.r);
        assertEquals(15, SocialColorUtils.DEFAULT_SCARY_COLOR.g);
        assertEquals(175, SocialColorUtils.DEFAULT_SCARY_COLOR.b);

        assertEquals(255, SocialColorUtils.DEFAULT_BLACKLISTED_COLOR.r);
        assertEquals(0, SocialColorUtils.DEFAULT_BLACKLISTED_COLOR.g);
        assertEquals(255, SocialColorUtils.DEFAULT_BLACKLISTED_COLOR.b);

        assertEquals(255, SocialColorUtils.DEFAULT_ALT_COLOR.r);
        assertEquals(165, SocialColorUtils.DEFAULT_ALT_COLOR.g);
        assertEquals(0, SocialColorUtils.DEFAULT_ALT_COLOR.b);
    }

    @Test
    void colorForReturnsDefaultCopies() {
        // No Systems/Modules runtime is initialized in unit tests, so defaults are used.
        Color self = SocialColorUtils.colorFor(SocialColorUtils.Status.Self);
        assertNotNull(self);
        assertEquals(SocialColorUtils.DEFAULT_SELF_COLOR.r, self.r);
        assertEquals(SocialColorUtils.DEFAULT_SELF_COLOR.g, self.g);
        assertEquals(SocialColorUtils.DEFAULT_SELF_COLOR.b, self.b);
        assertNotSame(SocialColorUtils.DEFAULT_SELF_COLOR, self);

        Color scary = SocialColorUtils.colorFor(SocialColorUtils.Status.Scary);
        assertEquals(SocialColorUtils.DEFAULT_SCARY_COLOR.r, scary.r);
        assertEquals(SocialColorUtils.DEFAULT_SCARY_COLOR.g, scary.g);
        assertEquals(SocialColorUtils.DEFAULT_SCARY_COLOR.b, scary.b);

        Color blacklisted = SocialColorUtils.colorFor(SocialColorUtils.Status.Blacklisted);
        assertEquals(SocialColorUtils.DEFAULT_BLACKLISTED_COLOR.r, blacklisted.r);
        assertEquals(SocialColorUtils.DEFAULT_BLACKLISTED_COLOR.g, blacklisted.g);
        assertEquals(SocialColorUtils.DEFAULT_BLACKLISTED_COLOR.b, blacklisted.b);

        Color alt = SocialColorUtils.colorFor(SocialColorUtils.Status.Alt);
        assertEquals(SocialColorUtils.DEFAULT_ALT_COLOR.r, alt.r);
        assertEquals(SocialColorUtils.DEFAULT_ALT_COLOR.g, alt.g);
        assertEquals(SocialColorUtils.DEFAULT_ALT_COLOR.b, alt.b);

        Color friend = SocialColorUtils.colorFor(SocialColorUtils.Status.Friend);
        assertEquals(SocialColorUtils.DEFAULT_FRIEND_COLOR.r, friend.r);
        assertEquals(SocialColorUtils.DEFAULT_FRIEND_COLOR.g, friend.g);
        assertEquals(SocialColorUtils.DEFAULT_FRIEND_COLOR.b, friend.b);
    }

    @Test
    void colorForTeamAndPlayerIsNull() {
        assertNull(SocialColorUtils.colorFor(SocialColorUtils.Status.Team));
        assertNull(SocialColorUtils.colorFor(SocialColorUtils.Status.Player));
    }

    @Test
    void getNameColorUsesDefaultsWithDefaultAlpha() {
        Color def = new Color(10, 20, 30, 40);

        Color self = SocialColorUtils.getNameColor(true, false, false, false, false, false, null, def);
        assertEquals(SocialColorUtils.DEFAULT_SELF_COLOR.r, self.r);
        assertEquals(SocialColorUtils.DEFAULT_SELF_COLOR.g, self.g);
        assertEquals(SocialColorUtils.DEFAULT_SELF_COLOR.b, self.b);
        assertEquals(40, self.a);

        Color scary = SocialColorUtils.getNameColor(false, true, false, false, false, false, null, def);
        assertEquals(SocialColorUtils.DEFAULT_SCARY_COLOR.r, scary.r);
        assertEquals(40, scary.a);

        Color blacklisted = SocialColorUtils.getNameColor(false, false, true, false, false, false, null, def);
        assertEquals(SocialColorUtils.DEFAULT_BLACKLISTED_COLOR.r, blacklisted.r);
        assertEquals(40, blacklisted.a);

        Color alt = SocialColorUtils.getNameColor(false, false, false, true, false, false, null, def);
        assertEquals(SocialColorUtils.DEFAULT_ALT_COLOR.r, alt.r);
        assertEquals(40, alt.a);

        Color friend = SocialColorUtils.getNameColor(false, false, false, false, true, false, null, def);
        assertEquals(SocialColorUtils.DEFAULT_FRIEND_COLOR.r, friend.r);
        assertEquals(40, friend.a);
    }

    @Test
    void getNameColorPrioritySelfWins() {
        Color def = new Color(10, 20, 30, 40);
        Color c = SocialColorUtils.getNameColor(true, true, true, true, true, true, null, def);
        assertEquals(SocialColorUtils.DEFAULT_SELF_COLOR.r, c.r);
        assertEquals(SocialColorUtils.DEFAULT_SELF_COLOR.g, c.g);
        assertEquals(SocialColorUtils.DEFAULT_SELF_COLOR.b, c.b);
    }

    @Test
    void getNameColorTeamUsesTeamColorWithDefaultAlpha() {
        Color def = new Color(10, 20, 30, 40);
        Color team = new Color(1, 2, 3, 255);
        Color c = SocialColorUtils.getNameColor(false, false, false, false, false, true, team, def);
        assertEquals(1, c.r);
        assertEquals(2, c.g);
        assertEquals(3, c.b);
        assertEquals(40, c.a);
    }

    @Test
    void getNameColorPlayerReturnsDefault() {
        Color def = new Color(10, 20, 30, 40);
        assertSame(def, SocialColorUtils.getNameColor(false, false, false, false, false, false, null, def));
    }
}
