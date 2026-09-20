package com.lurobaca.jarvis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lurobaca.jarvis.service.WakeWordListenerService

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildContent())
        requestNotificationPermissionIfNeeded()
    }

    private fun buildContent(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        setPadding(48, 96, 48, 48)
        setBackgroundColor(Color.rgb(7, 17, 26))

        addView(TextView(context).apply {
            text = "JARVIS"
            textSize = 34f
            setTextColor(Color.rgb(0, 166, 255))
            gravity = Gravity.CENTER
        }, matchWrap())

        status = TextView(context).apply {
            text = "Estado: detenido"
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 40)
        }
        addView(status, matchWrap())

        addView(actionButton("Activar Jarvis") { activate() }, matchWrap())
        addView(actionButton("Detener Jarvis") { stopJarvis() }, matchWrap())
        addView(actionButton("Abrir ChatGPT") { openChatGpt() }, matchWrap())

        addView(TextView(context).apply {
            text = getString(R.string.setup_hint)
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 48, 0, 0)
        }, matchWrap())
    }

    private fun activate() {
        if (BuildConfig.PICOVOICE_ACCESS_KEY.isBlank()) {
            status.text = "Falta configurar PICOVOICE_ACCESS_KEY"
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_MICROPHONE)
            return
        }
        ContextCompat.startForegroundService(this, Intent(this, WakeWordListenerService::class.java))
        status.text = "Estado: escuchando “Jarvis”"
    }

    private fun stopJarvis() {
        startService(Intent(this, WakeWordListenerService::class.java).setAction(WakeWordListenerService.ACTION_STOP))
        status.text = "Estado: detenido"
    }

    private fun openChatGpt() {
        packageManager.getLaunchIntentForPackage("com.openai.chatgpt")?.let(::startActivity)
            ?: startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, results: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (requestCode == REQUEST_MICROPHONE && results.firstOrNull() == PackageManager.PERMISSION_GRANTED) activate()
        else if (requestCode == REQUEST_MICROPHONE) status.text = "El permiso de micrófono es obligatorio"
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS)
        }
    }

    private fun actionButton(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        setOnClickListener { action() }
    }

    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    private companion object {
        const val REQUEST_MICROPHONE = 10
        const val REQUEST_NOTIFICATIONS = 11
    }
}
