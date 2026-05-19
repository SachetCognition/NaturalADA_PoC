package com.insurance.common.mapper;

/**
 * Maps ENTRY-TYPE codes (lines 56-69 of Reporting Mode program).
 */
public final class EntryTypeMapper {

    private EntryTypeMapper() {
    }

    public static String map(char entryType) {
        return switch (entryType) {
            case 'E' -> "ADDL";
            case 'F' -> "RETN";
            case 'B' -> "RENL";
            case 'C' -> "NEW";
            case 'M' -> "DEP";
            default -> null;
        };
    }
}
