package com.minitel.toolboxlite.domain.entities.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.TimeZone

@Parcelize
data class IcsTimezone(
    val tzid: TimeZone,
    val daylight: IcsTimezoneDescription,
    val standard: IcsTimezoneDescription,
) : Parcelable {
    class Builder {
        var tzid: TimeZone? = null
        val daylight = IcsTimezoneDescription.Builder()
        val standard = IcsTimezoneDescription.Builder()

        fun build() = IcsTimezone(
            tzid = tzid!!,
            daylight = daylight.build(),
            standard = standard.build(),
        )

        operator fun set(key: String, value: String) {
            when (key) {
                "TZID" -> tzid = TimeZone.getTimeZone(value)
            }
        }
    }
}
