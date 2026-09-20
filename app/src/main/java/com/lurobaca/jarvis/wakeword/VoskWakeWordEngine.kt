package com.lurobaca.jarvis.wakeword

import android.content.Context
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import java.util.Locale

/** Offline wake-word adapter backed by the Vosk speech recognizer. */
class VoskWakeWordEngine(private val context: Context) : WakeWordEngine, RecognitionListener {
    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var detectionCallback: (() -> Unit)? = null
    private var errorCallback: ((Throwable) -> Unit)? = null
    private var loading = false
    private var listeningRequested = false

    override fun start(onDetected: () -> Unit, onError: (Throwable) -> Unit) {
        detectionCallback = onDetected
        errorCallback = onError
        listeningRequested = true

        speechService?.startListening(this)
        if (model != null || loading) return

        loading = true
        StorageService.unpack(
            context,
            MODEL_ASSET_PATH,
            MODEL_STORAGE_PATH,
            { loadedModel ->
                loading = false
                model = loadedModel
                runCatching {
                    SpeechService(Recognizer(loadedModel, SAMPLE_RATE), SAMPLE_RATE).also {
                        speechService = it
                        if (listeningRequested) it.startListening(this)
                    }
                }.onFailure(::reportError)
            },
            { exception ->
                loading = false
                reportError(exception)
            },
        )
    }

    override fun stop() {
        listeningRequested = false
        speechService?.stop()
    }

    override fun release() {
        listeningRequested = false
        speechService?.shutdown()
        speechService = null
        model?.close()
        model = null
        detectionCallback = null
        errorCallback = null
    }

    override fun onPartialResult(hypothesis: String) = inspect(hypothesis, "partial")
    override fun onResult(hypothesis: String) = inspect(hypothesis, "text")
    override fun onFinalResult(hypothesis: String) = inspect(hypothesis, "text")
    override fun onError(exception: Exception) = reportError(exception)
    override fun onTimeout() = Unit

    private fun inspect(hypothesis: String, field: String) {
        val text = runCatching { JSONObject(hypothesis).optString(field) }.getOrDefault("")
            .lowercase(Locale.ROOT)
        if (text.split(' ').any { it.trim() == WAKE_WORD }) detectionCallback?.invoke()
    }

    private fun reportError(throwable: Throwable) {
        errorCallback?.invoke(throwable)
    }

    private companion object {
        const val MODEL_ASSET_PATH = "model-en-us"
        const val MODEL_STORAGE_PATH = "model-en-us"
        const val WAKE_WORD = "jarvis"
        const val SAMPLE_RATE = 16_000.0f
    }
}
