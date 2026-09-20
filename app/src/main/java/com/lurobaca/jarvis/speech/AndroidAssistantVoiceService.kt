package com.lurobaca.jarvis.speech

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class AndroidAssistantVoiceService(context: Context) : AssistantVoiceService {
    private var ready = false
    private var pending: (() -> Unit)? = null
    private val tts = TextToSpeech(context.applicationContext) { status ->
        ready = status == TextToSpeech.SUCCESS
        if (ready) configureVoice()
    }

    private fun configureVoice() {
        val britishMale = tts.voices
            ?.filter { it.locale == Locale.UK }
            ?.firstOrNull { "male" in it.name.lowercase() }
        if (britishMale != null) tts.voice = britishMale else tts.language = Locale.UK
        tts.setSpeechRate(0.92f)
        tts.setPitch(0.88f)
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onError(utteranceId: String?) = finish()
            override fun onDone(utteranceId: String?) = finish()
            private fun finish() { pending?.also { pending = null }?.invoke() }
        })
    }

    override fun speakAcknowledgement(onFinished: () -> Unit) {
        if (!ready) return onFinished()
        pending = onFinished
        tts.speak("Sí, señor.", TextToSpeech.QUEUE_FLUSH, Bundle(), UTTERANCE_ID)
    }

    override fun release() {
        pending = null
        tts.stop()
        tts.shutdown()
    }

    private companion object { const val UTTERANCE_ID = "jarvis_acknowledgement" }
}
