package com.minitel.toolboxlite.presentation.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.minitel.toolboxlite.R
import com.minitel.toolboxlite.core.intent.CustomIntent
import com.minitel.toolboxlite.domain.entities.notification.Notification

class IcsEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == CustomIntent.INTENT_ACTION_ALARM) {
            val payload = intent.getParcelableExtra<Notification>("notification")
            payload?.let {
                val notification = NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                    .setContentTitle(payload.title)
                    .setContentText(payload.description)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

                with(NotificationManagerCompat.from(context)) {
                    notify(payload.id, notification)
                }
            }
        }
    }
}
