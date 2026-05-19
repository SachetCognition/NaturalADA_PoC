package com.insurance.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateSanitizerTest {

    @Test
    void shouldResetDateBelow1900000() {
        assertEquals(0, DateSanitizer.sanitize(1800000));
    }

    @Test
    void shouldKeepValidDate() {
        assertEquals(20131001, DateSanitizer.sanitize(20131001));
    }

    @Test
    void shouldResetZero() {
        assertEquals(0, DateSanitizer.sanitize(0));
    }

    @Test
    void shouldKeepBoundaryDate() {
        assertEquals(1900000, DateSanitizer.sanitize(1900000));
    }

    @Test
    void shouldResetJustBelowBoundary() {
        assertEquals(0, DateSanitizer.sanitize(1899999));
    }
}
