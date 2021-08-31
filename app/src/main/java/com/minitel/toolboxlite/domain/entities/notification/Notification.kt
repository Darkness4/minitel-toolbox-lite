package com.minitel.toolboxlite.domain.entities.notification

import android.content.Context
import android.os.Parcelable
import com.minitel.toolboxlite.R
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(val id: Int, val title: String, val description: String) : Parcelable {
    companion object {
        fun fromIcsEvent(context: Context, icsEvent: IcsEvent) = Notification(
            id = icsEvent.uid.toLong(16).rem(Int.MAX_VALUE).toInt(),
            title = context.getString(R.string.notification_title).format(
                icsEvent.location,
                icsEvent.dtstart.toLocalTime(),
                icsEvent.dtend.toLocalTime()
            ),
            description = icsEvent.summary,
        )
    }
}
