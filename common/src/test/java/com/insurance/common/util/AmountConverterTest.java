package com.insurance.common.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AmountConverterTest {

    @Test
    void shouldConvertPositiveAmount() {
        assertEquals(new BigDecimal("1234.56"), AmountConverter.toCurrency(123456));
    }

    @Test
    void shouldConvertNegativeAmount() {
        assertEquals(new BigDecimal("-1234.56"), AmountConverter.toCurrency(-123456));
    }

    @Test
    void shouldConvertZero() {
        assertEquals(new BigDecimal("0.00"), AmountConverter.toCurrency(0));
    }

    @Test
    void shouldTruncateNotRound() {
        // 12345 / 100 = 123.45 (no rounding issue)
        assertEquals(new BigDecimal("123.45"), AmountConverter.toCurrency(12345));
        // 99 / 100 = 0.99
        assertEquals(new BigDecimal("0.99"), AmountConverter.toCurrency(99));
    }

    @Test
    void shouldFormatPositiveWithEditMask() {
        BigDecimal amount = new BigDecimal("1234.56");
        String formatted = AmountConverter.formatWithEditMask(amount);
        assertTrue(formatted.contains("1234.56"));
        assertFalse(formatted.startsWith("-"));
    }

    @Test
    void shouldFormatNegativeWithEditMask() {
        BigDecimal amount = new BigDecimal("-1234.56");
        String formatted = AmountConverter.formatWithEditMask(amount);
        assertTrue(formatted.startsWith("-"));
        assertTrue(formatted.contains("1234.56"));
    }

    @Test
    void shouldFormatZeroWithEditMask() {
        BigDecimal amount = BigDecimal.ZERO;
        String formatted = AmountConverter.formatWithEditMask(amount);
        assertTrue(formatted.contains("0.00"));
    }

    @Test
    void shouldFormatNullAsZero() {
        String formatted = AmountConverter.formatWithEditMask(null);
        assertTrue(formatted.contains("0.00"));
    }
}
