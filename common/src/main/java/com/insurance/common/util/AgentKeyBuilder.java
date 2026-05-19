package com.insurance.common.util;

/**
 * Builds agent lookup key (line 34 of Reporting Mode program).
 * COMPRESS '10' #BCH '3' #AGT INTO #MAIN-AGT-KEY LEAVING NO SPACE
 */
public final class AgentKeyBuilder {

    private AgentKeyBuilder() {
    }

    public static String build(String branch, String agent) {
        return "10" + branch + "3" + agent;
    }
}
