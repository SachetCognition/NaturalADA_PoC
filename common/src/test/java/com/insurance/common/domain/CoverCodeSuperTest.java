package com.insurance.common.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoverCodeSuperTest {

    @Test
    void shouldParseCompositeKey() {
        CoverCodeSuper result = CoverCodeSuper.fromComposite("150110AU99999999");
        assertEquals(150, result.tableType());
        assertEquals(1, result.coverage());
        assertEquals(10, result.group());
        assertEquals("AU", result.area());
        assertEquals(99999999, result.dateKey());
    }

    @Test
    void shouldRoundTrip() {
        CoverCodeSuper original = new CoverCodeSuper(150, 1, 10, "AU", 99999999);
        CoverCodeSuper parsed = CoverCodeSuper.fromComposite(original.toComposite());
        assertEquals(original, parsed);
    }

    @Test
    void shouldHandleZeroValues() {
        CoverCodeSuper result = CoverCodeSuper.fromComposite("000000  00000000");
        assertEquals(0, result.tableType());
        assertEquals(0, result.coverage());
        assertEquals(0, result.group());
        assertEquals("  ", result.area());
        assertEquals(0, result.dateKey());
    }

    @Test
    void shouldHandleMaxValues() {
        CoverCodeSuper result = CoverCodeSuper.fromComposite("999999ZZ99999999");
        assertEquals(999, result.tableType());
        assertEquals(9, result.coverage());
        assertEquals(99, result.group());
        assertEquals("ZZ", result.area());
        assertEquals(99999999, result.dateKey());
    }

    @Test
    void shouldRejectNullInput() {
        assertThrows(IllegalArgumentException.class, () -> CoverCodeSuper.fromComposite(null));
    }

    @Test
    void shouldRejectWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> CoverCodeSuper.fromComposite("short"));
    }

    @Test
    void shouldFormatToComposite() {
        CoverCodeSuper key = new CoverCodeSuper(150, 1, 10, "AU", 20150331);
        assertEquals("150110AU20150331", key.toComposite());
    }
}
