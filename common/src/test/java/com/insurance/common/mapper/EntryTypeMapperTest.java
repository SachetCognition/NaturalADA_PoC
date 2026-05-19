package com.insurance.common.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntryTypeMapperTest {

    @Test
    void shouldMapE_toADDL() {
        assertEquals("ADDL", EntryTypeMapper.map('E'));
    }

    @Test
    void shouldMapF_toRETN() {
        assertEquals("RETN", EntryTypeMapper.map('F'));
    }

    @Test
    void shouldMapB_toRENL() {
        assertEquals("RENL", EntryTypeMapper.map('B'));
    }

    @Test
    void shouldMapC_toNEW() {
        assertEquals("NEW", EntryTypeMapper.map('C'));
    }

    @Test
    void shouldMapM_toDEP() {
        assertEquals("DEP", EntryTypeMapper.map('M'));
    }

    @Test
    void shouldReturnNullForUnknown() {
        assertNull(EntryTypeMapper.map('Z'));
        assertNull(EntryTypeMapper.map('A'));
        assertNull(EntryTypeMapper.map(' '));
    }
}
