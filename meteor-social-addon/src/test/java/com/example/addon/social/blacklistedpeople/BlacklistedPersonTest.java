package com.example.addon.social.blacklistedpeople;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BlacklistedPersonTest {
    @Test
    void stringConstructorSetsName() {
        BlacklistedPerson p = new BlacklistedPerson("Toxic");
        assertEquals("Toxic", p.getName());
        assertEquals("Toxic", p.name);
    }

    @Test
    void equalsAndHashCodeByName() {
        assertEquals(new BlacklistedPerson("Toxic"), new BlacklistedPerson("Toxic"));
        assertEquals(new BlacklistedPerson("Toxic").hashCode(), new BlacklistedPerson("Toxic").hashCode());
        assertNotEquals(new BlacklistedPerson("Toxic"), new BlacklistedPerson("Other"));
        assertNotEquals(new BlacklistedPerson("Toxic"), null);
        assertNotEquals(new BlacklistedPerson("Toxic"), "Toxic");
    }

    @Test
    void compareToOrdersByName() {
        assertTrue(new BlacklistedPerson("a").compareTo(new BlacklistedPerson("b")) < 0);
        assertTrue(new BlacklistedPerson("b").compareTo(new BlacklistedPerson("a")) > 0);
        assertEquals(0, new BlacklistedPerson("a").compareTo(new BlacklistedPerson("a")));
    }
}
