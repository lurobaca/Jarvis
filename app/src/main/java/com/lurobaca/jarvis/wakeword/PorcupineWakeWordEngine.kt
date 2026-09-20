package com.lurobaca.jarvis.wakeword

import android.content.Context
import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineManager

class PorcupineWakeWordEngine(
    private val context: Context,
    private val accessKey: String,
) : WakeWordEngine {
    private var manager: PorcupineManager? = null

    override fun start(onDetected: () -> Unit, onError: (Throwable) -> Unit) {
        if (manager != null) return
        runCatching {
            check(accessKey.isNotBlank()) { "Falta PICOVOICE_ACCESS_KEY en local.properties" }
            manager = PorcupineManager.Builder()
                .setAccessKey(accessKey)
                .setKeyword(Porcupine.BuiltInKeyword.JARVIS)
                .setSensitivity(0.65f)
                .build(context) { onDetected() }
                .also { it.start() }
        }.onFailure(onError)
    }

    override fun stop() {
        runCatching { manager?.stop() }
    }

    override fun release() {
        stop()
        runCatching { manager?.delete() }
        manager = null
    }
}
