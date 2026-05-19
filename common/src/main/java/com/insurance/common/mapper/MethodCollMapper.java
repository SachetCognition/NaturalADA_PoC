package com.insurance.common.mapper;

/**
 * Maps METHOD-COLL first character (lines 70-81 of Reporting Mode program).
 */
public final class MethodCollMapper {

    private MethodCollMapper() {
    }

    public static String map(char mc1) {
        return switch (mc1) {
            case 'S' -> "SETTLED";
            case 'R' -> "REDEBITED";
            case 'X' -> "WITHDRAWN";
            case 'K' -> "UNMATCHED CASH";
            default -> null;
        };
    }
}
