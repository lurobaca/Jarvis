package com.lurobaca.jarvis.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WakeWordGateTest {
    @Test fun `first activation is accepted`() {
        assertTrue(WakeWordGate(now = { 100 }).tryActivate())
    }

    @Test fun `activation inside cooldown is rejected`() {
        var time = 100L
        val gate = WakeWordGate(cooldownMillis = 3_000, now = { time })
        assertTrue(gate.tryActivate())
        time = 2_000
        assertFalse(gate.tryActivate())
    }

    @Test fun `activation after cooldown is accepted`() {
        var time = 100L
        val gate = WakeWordGate(cooldownMillis = 3_000, now = { time })
        assertTrue(gate.tryActivate())
        time = 3_100
        assertTrue(gate.tryActivate())
    }
}
