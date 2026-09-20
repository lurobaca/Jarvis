package com.lurobaca.jarvis.speech

interface AssistantVoiceService {
    fun speakAcknowledgement(onFinished: () -> Unit)
    fun release()
}
