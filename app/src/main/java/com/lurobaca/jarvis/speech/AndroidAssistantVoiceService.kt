package com.lurobaca.jarvis.speech

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class AndroidAssistantVoiceService(context: Context) : AssistantVoiceService {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var ready = false
    private var pending: (() -> Unit)? = null
    private val tts = TextToSpeech(context.applicationContext) { status ->
        ready = status == TextToSpeech.SUCCESS
        if (ready) configureVoice()
    }

    private fun configureVoice() {
        val spanishVoices = tts.voices.orEmpty().filter { it.locale.language == SPANISH.language }
        val masculineVoice = spanishVoices.firstOrNull { voice ->
            MALE_MARKERS.any { marker -> marker in voice.name.lowercase(Locale.ROOT) }
        }
        val latinVoice = spanishVoices.firstOrNull { it.locale.country in LATIN_COUNTRIES }

        when {
            masculineVoice != null -> tts.voice = masculineVoice
            latinVoice != null -> tts.voice = latinVoice
            else -> tts.language = SPANISH
        }
        // A lower pitch gives neutral engines a deeper, masculine character.
        tts.setSpeechRate(0.86f)
        tts.setPitch(0.72f)
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onError(utteranceId: String?) = finish()
            override fun onDone(utteranceId: String?) = finish()
            private fun finish() {
                val callback = pending.also { pending = null } ?: return
                mainHandler.post(callback)
            }
        })
    }

    override fun speakAcknowledgement(onFinished: () -> Unit) {
        if (!ready) return onFinished()
        pending = onFinished
        tts.speak("Sí, señor. ¿Qué necesita?", TextToSpeech.QUEUE_FLUSH, Bundle(), UTTERANCE_ID)
    }

    override fun release() {
        pending = null
        tts.stop()
        tts.shutdown()
    }

    private companion object {
        const val UTTERANCE_ID = "jarvis_acknowledgement"
        val SPANISH = Locale("es", "CR")
        val LATIN_COUNTRIES = setOf("CR", "MX", "US", "CO", "AR")
        val MALE_MARKERS = listOf("male", "masculino", "hombre")
    }
}
