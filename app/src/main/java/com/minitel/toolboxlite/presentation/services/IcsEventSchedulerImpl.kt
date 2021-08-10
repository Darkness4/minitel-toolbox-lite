package com.minitel.toolboxlite.presentation.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.minitel.toolboxlite.core.intent.CustomIntent
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import com.minitel.toolboxlite.domain.entities.notification.Notification
import com.minitel.toolboxlite.domain.services.IcsEventScheduler
import org.threeten.bp.ZoneOffset
import timber.log.Timber
import javax.inject.Inject

class IcsEventSchedulerImpl @Inject constructor() : IcsEventScheduler {
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun schedule(context: Context, icsEvent: IcsEvent, earlyMinutes: Int) {
        Timber.d(
            "Schedule $icsEvent at ${
            icsEvent.dtstart.toInstant(ZoneOffset.UTC).toEpochMilli() - (earlyMinutes * 60000)
            }"
        )
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val alarmIntent = Intent(context, IcsEventReceiver::class.java).let { intent ->
            val notification = Notification.fromIcsEvent(icsEvent)
            intent.putExtra("notification", notification)
            intent.action = CustomIntent.INTENT_ACTION_ALARM
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    context,
                    notification.id,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getBroadcast(
                    context,
                    notification.id,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }

        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            icsEvent.dtstart.toInstant(ZoneOffset.UTC).toEpochMilli() - (earlyMinutes * 60000),
            alarmIntent
        )
    }
}
