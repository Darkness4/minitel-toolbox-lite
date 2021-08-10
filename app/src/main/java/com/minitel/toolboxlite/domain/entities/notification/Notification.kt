package com.minitel.toolboxlite.domain.entities.notification

import android.os.Parcelable
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(val id: Int, val title: String, val description: String) : Parcelable {
    companion object {
        fun fromIcsEvent(icsEvent: IcsEvent) = Notification(
            id = icsEvent.uid.toLong(16).rem(Int.MAX_VALUE).toInt(),
            title = icsEvent.summary,
            description = "${icsEvent.location}\n${icsEvent.dtstart} - ${icsEvent.dtend}"
        )
    }
}
