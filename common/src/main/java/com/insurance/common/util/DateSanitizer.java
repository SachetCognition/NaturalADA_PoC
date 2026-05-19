package com.insurance.common.util;

/**
 * Implements date < 1900000 reset logic (lines 108-111 of Reporting Mode program).
 */
public final class DateSanitizer {

    private DateSanitizer() {
    }

    public static int sanitize(int date) {
        return date < 1900000 ? 0 : date;
    }
}
