package com.lurobaca.jarvis.launcher

import android.content.Context
import android.content.Intent
import android.net.Uri

class AndroidChatGptLauncher(private val context: Context) : ChatGptLauncher {
    override fun openVoice(): Boolean {
        val packageIntent = context.packageManager.getLaunchIntentForPackage(CHATGPT_PACKAGE)
        val intent = packageIntent ?: Intent(Intent.ACTION_VIEW, Uri.parse(CHATGPT_WEB))
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP,
        )
        return runCatching { context.startActivity(intent); true }.getOrDefault(false)
    }

    private companion object {
        const val CHATGPT_PACKAGE = "com.openai.chatgpt"
        const val CHATGPT_WEB = "https://chatgpt.com"
    }
}
