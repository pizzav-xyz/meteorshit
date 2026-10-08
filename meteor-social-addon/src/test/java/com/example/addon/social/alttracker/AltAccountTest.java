package com.example.addon.social.alttracker;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AltAccountTest {
    @Test
    void stringConstructorStartsEmpty() {
        AltAccount acc = new AltAccount("Main");
        assertEquals("Main", acc.getMainAccount());
        assertTrue(acc.getAltAccounts().isEmpty());
        assertEquals("Main", acc.getDisplayText("Main"));
    }

    @Test
    void setConstructorCopiesAlts() {
        AltAccount acc = new AltAccount("Main", new HashSet<>(Arrays.asList("Alt1", "Alt2")));
        assertTrue(acc.hasAlt("Alt1"));
        assertTrue(acc.hasAlt("Alt2"));
    }

    @Test
    void addAndRemoveAlt() {
        AltAccount acc = new AltAccount("Main");
        assertFalse(acc.hasAlt("Alt1"));
        acc.addAlt("Alt1");
        assertTrue(acc.hasAlt("Alt1"));
        acc.removeAlt("Alt1");
        assertFalse(acc.hasAlt("Alt1"));
    }

    @Test
    void removeMissingAltIsNoop() {
        AltAccount acc = new AltAccount("Main");
        acc.removeAlt("Ghost");
        assertFalse(acc.hasAlt("Ghost"));
    }

    @Test
    void isMainOrAlt() {
        AltAccount acc = new AltAccount("Main");
        acc.addAlt("Alt1");
        assertTrue(acc.isMainOrAlt("Main"));
        assertTrue(acc.isMainOrAlt("Alt1"));
        assertFalse(acc.isMainOrAlt("Stranger"));
    }

    @Test
    void getDisplayTextForMainWithAlts() {
        AltAccount acc = new AltAccount("Main");
        acc.addAlt("Alt1");
        acc.addAlt("Alt2");
        assertEquals("Main (+2 alts)", acc.getDisplayText("Main"));
    }

    @Test
    void getDisplayTextForAltShowsMain() {
        AltAccount acc = new AltAccount("Main");
        acc.addAlt("Alt1");
        assertEquals("Alt1 (Main)", acc.getDisplayText("Alt1"));
    }

    @Test
    void getDisplayTextForStrangerReturnsName() {
        AltAccount acc = new AltAccount("Main");
        acc.addAlt("Alt1");
        assertEquals("Stranger", acc.getDisplayText("Stranger"));
    }

    @Test
    void getDisplayTextWithOnlinePlayers() {
        AltAccount acc = new AltAccount("Main");
        acc.addAlt("Alt1");
        acc.addAlt("Alt2");
        assertEquals("Main (+1)", acc.getDisplayText("Main", Arrays.asList("Alt1")));
        assertEquals("Main", acc.getDisplayText("Main", Arrays.asList("Nobody")));
        assertEquals("Alt1 (Main)", acc.getDisplayText("Alt1", Arrays.asList("Alt1")));
        assertEquals("Stranger", acc.getDisplayText("Stranger", Arrays.asList("Alt1")));
    }

    @Test
    void getAllAccountsContainsMainAndAlts() {
        AltAccount acc = new AltAccount("Main");
        acc.addAlt("Alt1");
        List<String> all = acc.getAllAccounts();
        assertEquals(2, all.size());
        assertTrue(all.contains("Main"));
        assertTrue(all.contains("Alt1"));
    }

    @Test
    void getAltAccountsReturnsDefensiveCopy() {
        AltAccount acc = new AltAccount("Main");
        acc.addAlt("Alt1");
        acc.getAltAccounts().clear();
        assertTrue(acc.hasAlt("Alt1"));
    }

    @Test
    void equalsAndHashCodeByMain() {
        assertEquals(new AltAccount("Main"), new AltAccount("Main"));
        assertEquals(new AltAccount("Main").hashCode(), new AltAccount("Main").hashCode());
        assertNotEquals(new AltAccount("Main"), new AltAccount("Other"));
        assertNotEquals(new AltAccount("Main"), null);
        assertNotEquals(new AltAccount("Main"), "Main");
    }

    @Test
    void compareToOrdersByMain() {
        assertTrue(new AltAccount("a").compareTo(new AltAccount("b")) < 0);
        assertTrue(new AltAccount("b").compareTo(new AltAccount("a")) > 0);
        assertEquals(0, new AltAccount("a").compareTo(new AltAccount("a")));
    }
}
