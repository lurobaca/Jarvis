package com.lurobaca.jarvis.domain

class WakeWordGate(
    private val cooldownMillis: Long = 3_000,
    private val now: () -> Long = System::currentTimeMillis,
) {
    private var lastActivation = Long.MIN_VALUE

    @Synchronized
    fun tryActivate(): Boolean {
        val current = now()
        if (lastActivation != Long.MIN_VALUE && current - lastActivation < cooldownMillis) return false
        lastActivation = current
        return true
    }
}
