package com.example.addon.social.scarypeople;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScaryPersonTest {
    @Test
    void stringConstructorSetsName() {
        ScaryPerson p = new ScaryPerson("Griefer");
        assertEquals("Griefer", p.getName());
        assertEquals("Griefer", p.name);
    }

    @Test
    void equalsAndHashCodeByName() {
        assertEquals(new ScaryPerson("Griefer"), new ScaryPerson("Griefer"));
        assertEquals(new ScaryPerson("Griefer").hashCode(), new ScaryPerson("Griefer").hashCode());
        assertNotEquals(new ScaryPerson("Griefer"), new ScaryPerson("Other"));
        assertNotEquals(new ScaryPerson("Griefer"), null);
        assertNotEquals(new ScaryPerson("Griefer"), "Griefer");
    }

    @Test
    void compareToOrdersByName() {
        assertTrue(new ScaryPerson("a").compareTo(new ScaryPerson("b")) < 0);
        assertTrue(new ScaryPerson("b").compareTo(new ScaryPerson("a")) > 0);
        assertEquals(0, new ScaryPerson("a").compareTo(new ScaryPerson("a")));
    }
}
