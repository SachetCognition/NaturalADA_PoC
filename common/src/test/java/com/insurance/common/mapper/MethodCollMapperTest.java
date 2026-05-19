package com.insurance.common.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MethodCollMapperTest {

    @Test
    void shouldMapS_toSETTLED() {
        assertEquals("SETTLED", MethodCollMapper.map('S'));
    }

    @Test
    void shouldMapR_toREDEBITED() {
        assertEquals("REDEBITED", MethodCollMapper.map('R'));
    }

    @Test
    void shouldMapX_toWITHDRAWN() {
        assertEquals("WITHDRAWN", MethodCollMapper.map('X'));
    }

    @Test
    void shouldMapK_toUNMATCHED_CASH() {
        assertEquals("UNMATCHED CASH", MethodCollMapper.map('K'));
    }

    @Test
    void shouldReturnNullForUnknown() {
        assertNull(MethodCollMapper.map('Z'));
        assertNull(MethodCollMapper.map('A'));
        assertNull(MethodCollMapper.map(' '));
    }
}
