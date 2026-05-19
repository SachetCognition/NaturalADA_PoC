package com.insurance.common.domain;

/**
 * Replaces the Natural REDEFINE of #COV-SUPER (lines 0240-0320 of Structured Mode program).
 * Composite key: #C-TAB(N3) + #C-COV(N1) + #C-GRP(N2) + #C-AREA(A2) + #C-DATE(N8) = 16 chars total.
 */
public record CoverCodeSuper(int tableType, int coverage, int group, String area, int dateKey) {

    public static CoverCodeSuper fromComposite(String raw) {
        if (raw == null || raw.length() != 16) {
            throw new IllegalArgumentException("Composite key must be exactly 16 characters, got: " +
                    (raw == null ? "null" : raw.length()));
        }
        int tab = Integer.parseInt(raw.substring(0, 3));
        int cov = Integer.parseInt(raw.substring(3, 4));
        int grp = Integer.parseInt(raw.substring(4, 6));
        String ar = raw.substring(6, 8);
        int date = Integer.parseInt(raw.substring(8, 16));
        return new CoverCodeSuper(tab, cov, grp, ar, date);
    }

    public String toComposite() {
        return String.format("%03d%d%02d%2s%08d", tableType, coverage, group, area, dateKey);
    }
}
