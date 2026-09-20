package com.lurobaca.jarvis.wakeword

interface WakeWordEngine {
    fun start(onDetected: () -> Unit, onError: (Throwable) -> Unit)
    fun stop()
    fun release()
}
