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
    private constructor(builder: Builder) : this(
        dtstart = builder.dtstart!!,
        tzOffsetTo = builder.tzOffsetTo,
        tzOffsetFrom = builder.tzOffsetFrom,
        rRule = builder.rRule,
        tzName = builder.tzName,
    )

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var dtstart: LocalDateTime? = null
        var tzOffsetTo: String = ""
        var tzOffsetFrom: String = ""
        var rRule: String = ""
        var tzName: String = ""

        fun build() = IcsTimezoneDescription(this)

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
