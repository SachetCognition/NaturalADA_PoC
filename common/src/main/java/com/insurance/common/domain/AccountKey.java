package com.insurance.common.domain;

/**
 * Replaces REDEFINE of #ACC-KEY (lines 15-16 of Reporting Mode program).
 * accKey is 18 chars: 0-2=branch(A3), 3-8=agent(A6), 9-17=policy(A9).
 */
public record AccountKey(String branch, String agent, String policy) {

    public static AccountKey fromComposite(String accKey) {
        if (accKey == null || accKey.length() != 18) {
            throw new IllegalArgumentException("Account key must be exactly 18 characters, got: " +
                    (accKey == null ? "null" : accKey.length()));
        }
        String branch = accKey.substring(0, 3);
        String agent = accKey.substring(3, 9);
        String policy = accKey.substring(9, 18);
        return new AccountKey(branch, agent, policy);
    }

    public String toComposite() {
        return branch + agent + policy;
    }
}
