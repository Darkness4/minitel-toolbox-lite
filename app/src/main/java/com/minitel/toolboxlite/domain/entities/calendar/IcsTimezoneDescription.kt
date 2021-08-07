package com.minitel.toolboxlite.domain.entities.calendar

import android.os.Parcelable
import com.minitel.toolboxlite.core.formatters.IcsDateTimeFormatter
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDateTime

@Parcelize
data class IcsTimezoneDescription(
    val dtstart: LocalDateTime,
    val tzOffsetTo: String,
    val tzOffsetFrom: String,
    val rRule: String,
    val tzName: String
) : Parcelable {
    class Builder {
        var dtstart: LocalDateTime? = null
        var tzOffsetTo: String = ""
        var tzOffsetFrom: String = ""
        var rRule: String = ""
        var tzName: String = ""

        fun build() = IcsTimezoneDescription(
            dtstart = dtstart!!,
            tzOffsetTo = tzOffsetTo,
            tzOffsetFrom = tzOffsetFrom,
            rRule = rRule,
            tzName = tzName,
        )

        operator fun set(key: String, value: String) {
            when (key) {
                "DTSTART" -> dtstart = LocalDateTime.parse(value, IcsDateTimeFormatter)
                "TZOFFSETTO" -> tzOffsetTo = value
                "TZOFFSETFROM" -> tzOffsetFrom = value
                "RRULE" -> rRule = value
                "TZNAME" -> tzName = value
            }
        }
    }
}
