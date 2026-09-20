package com.lurobaca.jarvis.launcher

import android.content.Context
import android.content.Intent
import android.net.Uri

class AndroidChatGptLauncher(private val context: Context) : ChatGptLauncher {
    override fun openVoice(): Boolean {
        val assistantIntents = listOf(
            Intent(Intent.ACTION_VOICE_COMMAND),
            Intent(Intent.ACTION_ASSIST),
        )
        assistantIntents.forEach { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (runCatching { context.startActivity(intent); true }.getOrDefault(false)) return true
        }

        val fallback = context.packageManager.getLaunchIntentForPackage(CHATGPT_PACKAGE)
            ?: Intent(Intent.ACTION_VIEW, Uri.parse(CHATGPT_WEB))
        fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return runCatching { context.startActivity(fallback); true }.getOrDefault(false)
    }

    private companion object {
        const val CHATGPT_PACKAGE = "com.openai.chatgpt"
        const val CHATGPT_WEB = "https://chatgpt.com"
    }
}
