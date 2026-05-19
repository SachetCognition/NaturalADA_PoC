package com.insurance.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgentKeyBuilderTest {

    @Test
    void shouldBuildAgentKey() {
        assertEquals("100013ABC123", AgentKeyBuilder.build("001", "ABC123"));
    }

    @Test
    void shouldBuildWithDifferentValues() {
        assertEquals("100103XYZ456", AgentKeyBuilder.build("010", "XYZ456"));
    }
}
