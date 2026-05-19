package com.insurance.common.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountKeyTest {

    @Test
    void shouldParse18CharKey() {
        AccountKey result = AccountKey.fromComposite("001ABC123POL000001");
        assertEquals("001", result.branch());
        assertEquals("ABC123", result.agent());
        assertEquals("POL000001", result.policy());
    }

    @Test
    void shouldRoundTrip() {
        AccountKey original = new AccountKey("001", "ABC123", "POL000001");
        AccountKey parsed = AccountKey.fromComposite(original.toComposite());
        assertEquals(original, parsed);
    }

    @Test
    void shouldRejectNullInput() {
        assertThrows(IllegalArgumentException.class, () -> AccountKey.fromComposite(null));
    }

    @Test
    void shouldRejectWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> AccountKey.fromComposite("short"));
    }

    @Test
    void shouldComposeCorrectly() {
        AccountKey key = new AccountKey("002", "XYZ456", "987654321");
        assertEquals("002XYZ456987654321", key.toComposite());
    }
}
