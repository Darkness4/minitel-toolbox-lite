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
    private constructor(builder: Builder) : this(
        tzid = builder.tzid!!,
        daylight = builder.daylight.build(),
        standard = builder.standard.build(),
    )

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var tzid: TimeZone? = null
        val daylight = IcsTimezoneDescription.Builder()
        val standard = IcsTimezoneDescription.Builder()

        fun build() = IcsTimezone(this)

        operator fun set(key: String, value: String) {
            when (key) {
                "TZID" -> tzid = TimeZone.getTimeZone(value)
            }
        }
    }
}
