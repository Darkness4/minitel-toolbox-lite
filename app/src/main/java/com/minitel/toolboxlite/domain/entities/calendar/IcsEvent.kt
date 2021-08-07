package com.minitel.toolboxlite.domain.entities.calendar

import android.os.Parcelable
import com.minitel.toolboxlite.core.formatters.IcsDateTimeFormatter
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDateTime

@Parcelize
data class IcsEvent(
    val dtend: LocalDateTime,
    val uid: String,
    val dtstamp: LocalDateTime,
    val location: String,
    val description: String,
    val summary: String,
    val dtstart: LocalDateTime,
) : Parcelable {
    class Builder {
        var dtend: LocalDateTime? = null
        var uid: String = ""
        var dtstamp: LocalDateTime? = null
        var location: String = ""
        var description: String = ""
        var summary: String = ""
        var dtstart: LocalDateTime? = null

        fun build() = IcsEvent(
            dtend = dtend!!,
            uid = uid,
            dtstamp = dtstamp!!,
            location = location,
            description = description,
            summary = summary,
            dtstart = dtstart!!,
        )

        operator fun set(key: String, value: String) {
            when (key) {
                "DTEND" -> dtend = LocalDateTime.parse(value, IcsDateTimeFormatter)
                "UID" -> uid = value
                "DTSTAMP" -> dtstamp = LocalDateTime.parse(value, IcsDateTimeFormatter)
                "LOCATION" -> location = value
                "DESCRIPTION" -> description = value
                "SUMMARY" -> summary = value
                "DTSTART" -> dtstart = LocalDateTime.parse(value, IcsDateTimeFormatter)
            }
        }
    }
}
