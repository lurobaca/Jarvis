package com.lurobaca.jarvis.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.lurobaca.jarvis.MainActivity
import com.lurobaca.jarvis.R
import com.lurobaca.jarvis.data.PicovoiceAccessKeyStore
import com.lurobaca.jarvis.domain.WakeWordGate
import com.lurobaca.jarvis.launcher.AndroidChatGptLauncher
import com.lurobaca.jarvis.speech.AndroidAssistantVoiceService
import com.lurobaca.jarvis.wakeword.PorcupineWakeWordEngine
import com.lurobaca.jarvis.wakeword.WakeWordEngine

class WakeWordListenerService : Service() {
    private lateinit var wakeWordEngine: WakeWordEngine
    private lateinit var voice: AndroidAssistantVoiceService
    private val gate = WakeWordGate()

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startAsForeground()
        voice = AndroidAssistantVoiceService(this)
        wakeWordEngine = PorcupineWakeWordEngine(this, PicovoiceAccessKeyStore(this).get())
        wakeWordEngine.start(::onWakeWordDetected) { showError(it.message ?: "Error de escucha") }
    }

    private fun onWakeWordDetected() {
        if (!gate.tryActivate()) return
        wakeWordEngine.stop()
        voice.speakAcknowledgement {
            AndroidChatGptLauncher(this).openVoice()
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        wakeWordEngine.release()
        voice.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startAsForeground() {
        val notification = buildNotification("Escuchando “Jarvis”")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else startForeground(NOTIFICATION_ID, notification)
    }

    private fun showError(message: String) {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification(message))
    }

    private fun buildNotification(text: String): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 1, Intent(this, javaClass).setAction(ACTION_STOP), PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .addAction(0, "Detener", stopIntent)
            .build()
    }

    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Escucha de Jarvis", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val ACTION_STOP = "com.lurobaca.jarvis.STOP"
        private const val CHANNEL_ID = "jarvis_listening"
        private const val NOTIFICATION_ID = 1001
    }
}
