package com.insurance.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Replaces Natural's integer division by 100 with truncation (lines 83-97 of Reporting Mode).
 * Natural performs integer division which truncates toward zero.
 */
public final class AmountConverter {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private AmountConverter() {
    }

    public static BigDecimal toCurrency(long storedAmount) {
        return BigDecimal.valueOf(storedAmount).divide(HUNDRED, 2, RoundingMode.DOWN);
    }

    /**
     * Format matching Natural's EM=9999999.99 edit mask.
     * Produces a right-aligned numeric string with 2 decimal places.
     * Prefixes with '-' for negative amounts.
     */
    public static String formatWithEditMask(BigDecimal amount) {
        if (amount == null) {
            return "       0.00";
        }
        BigDecimal abs = amount.abs();
        String formatted = String.format("%10.2f", abs);
        if (amount.signum() < 0) {
            return "-" + formatted;
        }
        return " " + formatted;
    }
}
